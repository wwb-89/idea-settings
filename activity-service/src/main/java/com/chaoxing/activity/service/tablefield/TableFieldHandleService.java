package com.chaoxing.activity.service.tablefield;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.ActivityTableFieldMapper;
import com.chaoxing.activity.mapper.MarketTableFieldMapper;
import com.chaoxing.activity.mapper.OrgTableFieldMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityTableField;
import com.chaoxing.activity.model.MarketTableField;
import com.chaoxing.activity.model.OrgTableField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className TableFieldHandleService
 * @description
 * @blame wwb
 * @date 2021-05-28 14:42:50
 */
@Slf4j
@Service
public class TableFieldHandleService {

    @Resource
    private OrgTableFieldMapper orgTableFieldMapper;
    @Resource
    private ActivityTableFieldMapper activityTableFieldMapper;
    @Resource
    private MarketTableFieldMapper marketTableFieldMapper;

    /**机构配置表格字段
     * @Description 
     * @author wwb
     * @Date 2021-05-28 14:50:29
     * @param fid
     * @param tableFieldId
     * @param orgTableFields
     * @param loginUser
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void orgConfig(Integer fid, Integer tableFieldId, List<OrgTableField> orgTableFields, LoginUserDTO loginUser) {
        // 删除历史
        orgTableFieldMapper.delete(new UpdateWrapper<OrgTableField>()
            .lambda()
                .eq(OrgTableField::getFid, fid)
                .eq(OrgTableField::getTableFieldId, tableFieldId)
        );
        if (CollectionUtils.isNotEmpty(orgTableFields)) {
            for (OrgTableField orgTableField : orgTableFields) {
                orgTableField.setFid(fid);
                orgTableField.setTableFieldId(tableFieldId);
                orgTableField.setCreateUid(loginUser.getUid());
                orgTableField.setUpdateUid(loginUser.getUid());
            }
            orgTableFieldMapper.batchAdd(orgTableFields);
        }
    }

    /**活动配置表格字段
    * @Description
    * @author huxiaolong
    * @Date 2021-06-24 14:49:25
    * @param activityId
    * @param tableFieldId
    * @param activityTableFields
    * @param loginUser
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void activityConfig(Integer activityId, Integer tableFieldId, List<ActivityTableField> activityTableFields, LoginUserDTO loginUser) {
        // 删除历史
        activityTableFieldMapper.delete(new UpdateWrapper<ActivityTableField>()
            .lambda()
                .eq(ActivityTableField::getActivityId, activityId)
                .eq(ActivityTableField::getTableFieldId, tableFieldId)
        );
        if (CollectionUtils.isNotEmpty(activityTableFields)) {
            for (ActivityTableField activityTableField : activityTableFields) {
                activityTableField.setActivityId(activityId);
                activityTableField.setTableFieldId(tableFieldId);
                activityTableField.setCreateUid(loginUser.getUid());
                activityTableField.setUpdateUid(loginUser.getUid());
            }
            activityTableFieldMapper.batchAdd(activityTableFields);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void marketTableFieldConfig(Integer fid, String activityFlag, Integer tableFieldId, List<MarketTableField> marketTableFields, LoginUserDTO loginUser) {
        Activity.ActivityFlag flagEnum = Optional.ofNullable(Activity.ActivityFlag.fromValue(activityFlag)).orElse(Activity.ActivityFlag.NORMAL);
        // 删除历史
        marketTableFieldMapper.delete(new UpdateWrapper<MarketTableField>()
                .lambda()
                .eq(MarketTableField::getFid, fid)
                .eq(MarketTableField::getActivityFlag, flagEnum.getValue())
                .eq(MarketTableField::getTableFieldId, tableFieldId)
        );
        if (CollectionUtils.isNotEmpty(marketTableFields)) {
            for (MarketTableField marketTableField : marketTableFields) {
                marketTableField.setFid(fid);
                marketTableField.setActivityFlag(flagEnum.getValue());
                marketTableField.setTableFieldId(tableFieldId);
                marketTableField.setCreateUid(loginUser.getUid());
                marketTableField.setUpdateUid(loginUser.getUid());
            }
            marketTableFieldMapper.batchAdd(marketTableFields);
        }
    }
}
