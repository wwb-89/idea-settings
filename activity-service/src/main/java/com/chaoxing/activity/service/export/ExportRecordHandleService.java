package com.chaoxing.activity.service.export;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.export.ExportDataDTO;
import com.chaoxing.activity.dto.query.admin.ActivityStatSummaryQueryDTO;
import com.chaoxing.activity.mapper.ExportRecordMapper;
import com.chaoxing.activity.model.ActivityStatTask;
import com.chaoxing.activity.model.ExportRecord;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.queue.ExportQueueService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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

    @Autowired
    private ExportRecordMapper exportRecordMapper;

    @Resource
    private ExportService exportService;

    @Resource
    private ExportQueueService exportQueueService;

    @Resource
    private ActivityStatSummaryQueryService activityStatSummaryQueryService;

    @Resource
    private CloudApiService cloudApiService;


    @Transactional(rollbackFor = Exception.class)
    public void add(String fileName, String params, Integer createUid, String createIp, ExportRecord.ExportTypeEnum exportTypeEnum) {
        ExportRecord exportRecord = ExportRecord.builder()
                .fileName(fileName)
                .exportType(exportTypeEnum.getValue())
                .params(params)
                .createUid(createUid)
                .createIp(createIp)
                .build();
        exportRecordMapper.insert(exportRecord);
        exportQueueService.push(exportRecord.getId());
    }

    /**导出处理逻辑
    * @Description
    * @author huxiaolong
    * @Date 2021-06-01 16:15:41
    * @param exportRecord
    * @param uploadRootPath
    * @return boolean
    */
    public boolean exportHandle(ExportRecord exportRecord, String uploadRootPath) {
        // 默认初始待处理
        Integer status = ExportRecord.StatusEnum.WAIT_HANDLE.getValue();
        Integer taskId = exportRecord.getId();
        boolean result = false;
        try {
            // 活动统计汇总导出
            if (Objects.equals(exportRecord.getExportType(), ExportRecord.ExportTypeEnum.ORG_ACTIVITY_STAT.getValue())) {
                result = activityStatSummaryExport(exportRecord, uploadRootPath);
            }
            if (result) {
                status = ExportRecord.StatusEnum.SUCCESS.getValue();
            }
        } catch (BusinessException e) {
            log.error("导出任务:{}的error:{}", taskId, e.getMessage());
            e.printStackTrace();
            status = ExportRecord.StatusEnum.FAIL.getValue();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("导出任务:{}的error:{}", taskId, e.getMessage());
            status = ExportRecord.StatusEnum.FAIL.getValue();
        }
        // 处理结束，更新导出记录数据
        exportRecord.setStatus(status);
        exportRecordMapper.update(exportRecord, new UpdateWrapper<ExportRecord>()
                .lambda()
                .eq(ExportRecord::getId, taskId)
        );
        return Objects.equals(ActivityStatTask.Status.SUCCESS.getValue(), status);

    }

    /**活动统计汇总记录导出
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-01 16:19:52
    * @param exportRecord
* @param uploadRootPath
    * @return boolean
    */
    private boolean activityStatSummaryExport(ExportRecord exportRecord, String uploadRootPath) {
        boolean result = Boolean.TRUE;
        ActivityStatSummaryQueryDTO queryParams = null;
        int errorTimes = Optional.ofNullable(exportRecord.getHandleTimes()).orElse(0);
        String errorMsg = "";
        if (!StringUtils.isEmpty(exportRecord.getParams())) {
            queryParams = JSON.parseObject(exportRecord.getParams(), ActivityStatSummaryQueryDTO.class);
        }
        String fileName = uploadRootPath + File.separator +
                "活动统计汇总记录-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                ".xlsx";
        try {
            // 获取需要导出到excel的数据流
            ExportDataDTO exportData = activityStatSummaryQueryService.packageExportData(queryParams);
            exportData.setSheetName("活动统计汇总数据");
            ByteArrayOutputStream os = exportService.export(exportData);
            // 将导出数据流写到文件中
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(os.toByteArray());
            // 开始上传文件
            boolean uploadFlag = Boolean.FALSE;
            for (int i = 0; i < CommonConstant.MAX_ERROR_TIMES; i++) {
                // 5次最大上传失败重试次数
                uploadFlag = uploadCloudFile(fileName, exportRecord);
                if (uploadFlag) {
                    break;
                }
            }
            // 上传成功或失败后均删除文件
            deleteFile(fileName);
            if (uploadFlag) {
                // 在外面处理获取下载地址，防止获取下载地址的时候异常导致文件重试上传重复文件
                exportRecord.setDownloadUrl(cloudApiService.getFileDownloadUrl(exportRecord.getCloudId()));
            } else {
                throw new BusinessException("上传文件:" + fileName + " 失败！异常信息:" + errorMsg);
            }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            exportRecord.setHandleTimes(++errorTimes);
            exportRecord.setMessage(errorMsg);
            result = Boolean.FALSE;
            if (errorTimes >= CommonConstant.MAX_ERROR_TIMES) {
                throw new BusinessException("导出任务:" + exportRecord.getId() + "失败！异常信息:" + errorMsg);
            }
        }
        return result;
    }

    /**上传文件到云
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-01 16:17:49
    * @param fileName
    * @param exportRecord
    * @return boolean
    */
    private boolean uploadCloudFile(String fileName, ExportRecord exportRecord) {
        File file = new File(fileName);
        String responseStr = cloudApiService.upload(file, exportRecord.getCreateIp());
        JSONObject jsonObject = JSON.parseObject(responseStr);
        String status = jsonObject.getString("status");
        if (Objects.equals(status, "success")) {
            String cloudId = jsonObject.getString("objectid");
            // 将文件cloudId设置到导出记录
            exportRecord.setCloudId(cloudId);
            return true;
        }
        return false;
    }

    /**文件删除
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-01 16:18:22
    * @param fileName
    * @return void
    */
    private void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /***导出处理任务
    * @Description
    * @author huxiaolong
    * @Date 2021-06-01 16:15:21
    * @param taskId
    * @param uploadRootPath
    * @return boolean
    */
    public boolean handleExportTask(Integer taskId, String uploadRootPath) throws IOException {
        ExportRecord exportRecord = exportRecordMapper.selectById(taskId);

        Integer status = exportRecord.getStatus();
        ExportRecord.StatusEnum statusEnum = ExportRecord.StatusEnum.fromValue(status);

        if (Objects.equals(ExportRecord.StatusEnum.WAIT_HANDLE, statusEnum)) {
            // 导出任务处理中
            return exportHandle(exportRecord, uploadRootPath);
        }
        return Objects.equals(ExportRecord.StatusEnum.SUCCESS, statusEnum);
    }
}

