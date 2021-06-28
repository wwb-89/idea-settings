package com.chaoxing.activity.service.activity.stat;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.export.ExportDataDTO;
import com.chaoxing.activity.dto.query.admin.ActivityStatSummaryQueryDTO;
import com.chaoxing.activity.dto.sign.SignParticipateScopeDTO;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.mapper.ActivityClassifyMapper;
import com.chaoxing.activity.mapper.ActivityStatSummaryMapper;
import com.chaoxing.activity.mapper.TableFieldDetailMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.util.DateUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/25 1:53 下午
 * <p>
 */
@Slf4j
@Service
public class ActivityStatSummaryQueryService {

    @Resource
    private ActivityClassifyMapper activityClassifyMapper;
    @Resource
    private ActivityStatSummaryMapper activityStatSummaryMapper;
    @Resource
    private TableFieldDetailMapper tableFieldDetailMapper;
    @Resource
    private SignApiService signApiService;
    @Resource
    private TableFieldQueryService tableFieldQueryService;

    /**对活动统计汇总进行分页查询
    * @Description
    * @author huxiaolong
    * @Date 2021-05-25 16:32:27
    * @param page
    * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO>
    */
    public Page<ActivityStatSummaryDTO> activityStatSummaryPage(Page<ActivityStatSummaryDTO> page, ActivityStatSummaryQueryDTO queryParam) {
        List<Integer> externalIds = queryParam.getExternalIds();
        List<Integer> searchSignIds = signApiService.listSignIdsByExternalIds(externalIds);
        if (queryParam.getOrderFieldId() != null) {
            TableFieldDetail field = tableFieldDetailMapper.selectById(queryParam.getOrderFieldId());
            if (field != null) {
                queryParam.setOrderField(field.getCode());
            }
        }
        // 传递了参与范围的组织架构id集，报名签到id却为空，则返回空
        if (CollectionUtils.isNotEmpty(externalIds) && CollectionUtils.isEmpty(searchSignIds)) {
            return page;
        }
        String orderType = queryParam.getOrderType() == null ? null : queryParam.getOrderType().getValue();
        page = activityStatSummaryMapper.activityStatSummaryPage(page, queryParam, orderType, searchSignIds);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return page;
        }
        packagePageData(page);
        return page;
    }

    /**对page里面的数据进行封装和转换
    * @Description
    * @author huxiaolong
    * @Date 2021-05-31 10:33:53
    * @param page
    * @return void
    */
    private void packagePageData(Page<ActivityStatSummaryDTO> page) {
        List<Integer> activityIds = Lists.newArrayList();
        List<Integer> signIds = Lists.newArrayList();
        Set<Integer> classifyIds = Sets.newHashSet();
        for (ActivityStatSummaryDTO record : page.getRecords()) {
            activityIds.add(record.getActivityId());
            if (record.getSignId() != null) {
                signIds.add(record.getSignId());
            }
            if (record.getActivityClassifyId() != null) {
                classifyIds.add(record.getActivityClassifyId());
            }
            // 起止时间
            record.setActivityStartEndTime(DateUtils.activityTimeScope(record.getStartTime(), record.getEndTime()));
        }

        List<SignParticipateScopeDTO> signParticipateScopes = signApiService.listSignParticipateScopeBySignIds(signIds);
        Map<Integer, String> signParticipateScopeMap = Maps.newHashMap();
        for (SignParticipateScopeDTO item : signParticipateScopes) {
            signParticipateScopeMap.put(item.getSignId(), item.getExternalName());
        }

        Map<Integer, String> classifyMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(classifyIds)) {
            List<ActivityClassify> classifies = activityClassifyMapper.listByIds(classifyIds);
            classifyMap = classifies.stream().collect(Collectors.toMap(ActivityClassify::getId, ActivityClassify::getName, (v1, v2) -> v2));
        }
        for (ActivityStatSummaryDTO record : page.getRecords()) {
            Integer classifyId = record.getActivityClassifyId();
            Integer signId = record.getSignId();
            if (classifyId != null) {
                record.setActivityClassify(classifyMap.get(classifyId));
            }
            if (signId != null) {
                record.setParticipateScope(signParticipateScopeMap.get(signId));
            }
        }
    }

    /**获取excel表头
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-01 16:20:48
    * @param tableFieldDetails
    * @return java.util.List<java.util.List<java.lang.String>>
    */
    private List<List<String>> listActivityStatHeader(List<TableFieldDetail> tableFieldDetails) {
        List<List<String>> headers = Lists.newArrayList();
        List<String> idHeader = Lists.newArrayList();
        idHeader.add("活动id");
        headers.add(idHeader);
        for (TableFieldDetail tableFieldDetail : tableFieldDetails) {
            List<String> header = Lists.newArrayList();
            header.add(tableFieldDetail.getName());
            headers.add(header);
        }
        return headers;
    }

    private String valueToString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    /**获取数据
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-01 16:19:14
    * @param records
    * @param tableFieldDetails
    * @return java.util.List<java.util.List<java.lang.String>>
    */
    private List<List<String>> listData(List<ActivityStatSummaryDTO> records, List<TableFieldDetail> tableFieldDetails) {
        List<List<String>> data = Lists.newArrayList();
        if (CollectionUtils.isEmpty(records)) {
            return data;
        }
        for (ActivityStatSummaryDTO record : records) {
            List<String> itemData = Lists.newArrayList();
            itemData.add(String.valueOf(record.getActivityId()));
            for (TableFieldDetail tableFieldDetail : tableFieldDetails) {
                itemData.add(String.valueOf(record.getActivityId()));
                String code = tableFieldDetail.getCode();
                switch (code) {
                    case "activityName":
                        itemData.add(record.getActivityName());
                        break;
                    case "activityStatus":
                        itemData.add(Activity.getStatusDescription(record.getActivityStatus()));
                        break;
                    case "activityClassify":
                        itemData.add(record.getActivityClassify());
                        break;
                    case "activityCreator":
                        itemData.add(record.getActivityCreator());
                        break;
                    case "participateScope":
                        itemData.add(record.getParticipateScope());
                        break;
                    case "signedInNum":
                        itemData.add(valueToString(record.getSignedInNum()));
                        break;
                    case "signInRate":
                        itemData.add(valueToString(record.getSignInRate()));
                        break;
                    case "rateNum":
                        itemData.add(valueToString(record.getRateNum()));
                        break;
                    case "rateScore":
                        itemData.add(valueToString(record.getRateScore()));
                        break;
                    case "integral":
                        itemData.add(valueToString(record.getIntegral()));
                        break;
                    case "activityStartEndTime":
                        itemData.add(DateUtils.activityTimeScope(record.getStartTime(), record.getEndTime()));
                        break;
                    case "qualifiedNum":
                        itemData.add(valueToString(record.getQualifiedNum()));
                        break;
                    case "signedUpNum":
                        itemData.add(valueToString(record.getSignedUpNum()));
                        break;
                    case "avgParticipateTimeLength":
                        itemData.add(valueToString(record.getAvgParticipateTimeLength()));
                        break;
                    default:

                }
            }
            data.add(itemData);
        }
        return data;
    }

    /**封装easyexcel导出所需导出实体
    * @Description
    * @author huxiaolong
    * @Date 2021-06-01 16:18:42
    * @param queryParam
    * @return com.chaoxing.activity.dto.export.ExportDataDTO
    */
    public ExportDataDTO packageExportData(ActivityStatSummaryQueryDTO queryParam) {
        Integer fid = queryParam.getFid();
        List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listOrgShowTableFieldDetail(fid, TableField.Type.ACTIVITY_STAT, TableField.AssociatedType.ORG);
        ExportDataDTO exportData = new ExportDataDTO();
        Page<ActivityStatSummaryDTO> page = new Page<>(1, Integer.MAX_VALUE);
        page = activityStatSummaryPage(page, queryParam);
        List<List<String>> headers = listActivityStatHeader(tableFieldDetails);
        exportData.setHeaders(headers);
        List<List<String>> data = listData(page.getRecords(), tableFieldDetails);
        exportData.setData(data);
        return exportData;
    }
}
