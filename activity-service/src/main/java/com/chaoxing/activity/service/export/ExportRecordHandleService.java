package com.chaoxing.activity.service.export;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.export.ExportDataDTO;
import com.chaoxing.activity.dto.query.ActivityManageQueryDTO;
import com.chaoxing.activity.dto.query.UserResultQueryDTO;
import com.chaoxing.activity.dto.query.admin.ActivityStatSummaryQueryDTO;
import com.chaoxing.activity.dto.query.admin.UserStatSummaryQueryDTO;
import com.chaoxing.activity.mapper.ExportRecordMapper;
import com.chaoxing.activity.model.ExportRecord;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.queue.ExportQueueService;
import com.chaoxing.activity.service.stat.UserStatSummaryQueryService;
import com.chaoxing.activity.service.user.result.UserResultQueryService;
import com.chaoxing.activity.util.FileUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/1 11:40 上午
 * <p>
 */
@Slf4j
@Service
public class ExportRecordHandleService {

    @Resource
    private ExportRecordMapper exportRecordMapper;

    @Resource
    private ExportRecordQueryService exportRecordQueryService;
    @Resource
    private ExportService exportService;
    @Resource
    private ExportQueueService exportQueueService;
    @Resource
    private ActivityStatSummaryQueryService activityStatSummaryQueryService;
    @Resource
    private UserStatSummaryQueryService userStatSummaryQueryService;
    @Resource
    private UserResultQueryService userResultQueryService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private CloudApiService cloudApiService;

    @Transactional(rollbackFor = Exception.class)
    public void add(String params, String createIp, String exportType, LoginUserDTO loginUser) {
        ExportRecord.ExportTypeEnum exportTypeEnum = ExportRecord.ExportTypeEnum.fromValue(exportType);
        ExportRecord exportRecord = ExportRecord.builder()
                .exportType(exportTypeEnum.getValue())
                .params(params)
                .createUid(loginUser.getUid())
                .createFid(loginUser.getFid())
                .createIp(createIp)
                .build();
        exportRecordMapper.insert(exportRecord);
        exportQueueService.push(exportRecord.getId());
    }

    /**导出处理逻辑
    * @Description
    * @author huxiaolong
    * @Date 2021-06-01 16:15:41
    * @param taskId
    * @param uploadRootPath
    * @return void
    */
    public void exportHandle(Integer taskId, String uploadRootPath) {
        ExportRecord exportRecord = exportRecordQueryService.getById(taskId);
        if (exportRecord == null) {
            return;
        }
        // 默认初始待处理
        Integer status = exportRecord.getStatus();
        Integer handleTimes = exportRecord.getHandleTimes();
        handleTimes = Optional.ofNullable(handleTimes).orElse(1);
        String cloudId = "";
        if (handleTimes <= CommonConstant.MAX_ERROR_TIMES && Objects.equals(status, ExportRecord.StatusEnum.WAIT_HANDLE.getValue())) {
            try {
                // 活动统计汇总导出
                ExportDataDTO exportData = getExportData(exportRecord);
                String exportType = exportRecord.getExportType();
                ExportRecord.ExportTypeEnum exportTypeEnum = ExportRecord.ExportTypeEnum.fromValue(exportType);
                String fileName = uploadRootPath + File.separator + exportTypeEnum.getName() + "-" + exportRecord.getCreateTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
                exportData.setFileName(fileName);
                cloudId = export(exportData, exportRecord.getCreateIp());
                status = ExportRecord.StatusEnum.SUCCESS.getValue();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("导出任务:{}的error:{}", taskId, e.getMessage());
                handleTimes++;
                if (handleTimes >= CommonConstant.MAX_ERROR_TIMES) {
                    status = ExportRecord.StatusEnum.FAIL.getValue();
                } else {
                    throw new BusinessException(e.getMessage());
                }
            }finally {
                // 处理结束，更新导出记录数据
                exportRecord.setStatus(status);
                exportRecordMapper.update(null, new UpdateWrapper<ExportRecord>()
                        .lambda()
                        .eq(ExportRecord::getId, taskId)
                        .set(ExportRecord::getCloudId, cloudId)
                        .set(ExportRecord::getHandleTimes, handleTimes)
                        .set(ExportRecord::getStatus, status)
                        .set(ExportRecord::getMessage, status)
                );
            }
        }
    }

    /**获取导出的数据
     * @Description 
     * @author wwb
     * @Date 2021-06-03 11:10:15
     * @param exportRecord
     * @return com.chaoxing.activity.dto.export.ExportDataDTO
    */
    private ExportDataDTO getExportData(ExportRecord exportRecord) {
        ExportDataDTO exportData = null;
        String params = exportRecord.getParams();
        String exportType = exportRecord.getExportType();
        ExportRecord.ExportTypeEnum exportTypeEnum = ExportRecord.ExportTypeEnum.fromValue(exportType);
        switch (exportTypeEnum) {
            case ORG_ACTIVITY_STAT:
                exportData = activityStatSummaryQueryService.packageExportData(JSON.parseObject(params, ActivityStatSummaryQueryDTO.class));
                break;
            case ORG_USER_STAT:
                UserStatSummaryQueryDTO queryParams = JSON.parseObject(params, UserStatSummaryQueryDTO.class);
                exportData = userStatSummaryQueryService.getExportDataDTO(queryParams);
                break;
            case ACTIVITY_INSPECTION_MANAGE:
                UserResultQueryDTO queryParam = JSON.parseObject(params, UserResultQueryDTO.class);
                exportData = userResultQueryService.packageExportData(queryParam);
            case ACTIVITY_MANAGE:
                ActivityManageQueryDTO activityManageQueryParams = JSONObject.parseObject(params, ActivityManageQueryDTO.class);
                LoginUserDTO exportUser = LoginUserDTO.buildDefault(activityManageQueryParams.getExportUid(), activityManageQueryParams.getFid());
                exportData = activityQueryService.packageExportData(activityManageQueryParams, exportUser);
            default:
        }
        exportData.setSheetName(exportTypeEnum.getName());
        return exportData;
    }

    /**导出
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-01 16:19:52
    * @param exportData
    * @param ip
    * @return java.lang.String 上传资源的云盘id
    */
    private String export(ExportDataDTO exportData, String ip) throws IOException {
        String cloudId = "";
        String fileName = exportData.getFileName();
        // 创建目录
        FileUtils.createFolder(fileName);
        ByteArrayOutputStream os = exportService.export(exportData);
        // 将导出数据流写到文件中
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        fileOutputStream.write(os.toByteArray());
        String message = "";
        int repeatTime = 0;
        while (StringUtils.isBlank(cloudId) && repeatTime < CommonConstant.MAX_ERROR_TIMES) {
            // 5次最大上传失败重试次数
            try {
                cloudId = uploadCloudFile(fileName, ip);
            } catch (Exception e) {
                message = e.getMessage();
                e.printStackTrace();
            }
            repeatTime++;
        }
        // 上传成功或失败后均删除文件
        FileUtils.deleteFile(fileName);
        if (StringUtils.isBlank(cloudId)) {
            throw new BusinessException(message);
        }
        return cloudId;
    }

    /**上传文件到云
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-01 16:17:49
    * @param fileName
    * @param ip
    * @return java.lang.String 上传资源的云盘id
    */
    private String uploadCloudFile(String fileName, String ip) {
        File file = new File(fileName);
        String responseStr = cloudApiService.upload(file, ip);
        JSONObject jsonObject = JSON.parseObject(responseStr);
        String status = jsonObject.getString("status");
        if (Objects.equals(status, "success")) {
            return jsonObject.getString("objectid");
        } else {
            throw new BusinessException("上传到云盘失败");
        }
    }

}

