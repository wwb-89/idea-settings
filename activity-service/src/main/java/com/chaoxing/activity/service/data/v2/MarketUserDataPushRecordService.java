package com.chaoxing.activity.service.data.v2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.mapper.UserDataPushRecordMapper;
import com.chaoxing.activity.model.UserDataPushRecord;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**活动市场下用户数据推送记录服务
 * @author wwb
 * @version ver 1.0
 * @className MarketUserDataPushRecordService
 * @description
 * @blame wwb
 * @date 2021-11-03 13:16:52
 */
@Slf4j
@Service
public class MarketUserDataPushRecordService {

    @Resource
    private UserDataPushRecordMapper userDataPushRecordMapper;

    /**查询用户数据推送记录
     * @Description 
     * @author wwb
     * @Date 2021-11-03 14:01:28
     * @param uid
     * @param activityId
     * @param configId
     * @return com.chaoxing.activity.model.UserDataPushRecord
    */
    public UserDataPushRecord get(Integer uid, Integer activityId, Integer configId) {
        List<UserDataPushRecord> userDataPushRecords = userDataPushRecordMapper.selectList(new LambdaQueryWrapper<UserDataPushRecord>()
                .eq(UserDataPushRecord::getUid, uid)
                .eq(UserDataPushRecord::getActivityId, activityId)
                .eq(UserDataPushRecord::getConfigId, configId)
        );
        return Optional.ofNullable(userDataPushRecords).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
    }

    /**新增或更新
     * @Description 
     * @author wwb
     * @Date 2021-11-03 14:05:53
     * @param userDataPushRecord
     * @return void
    */
    public void addOrUpdate(UserDataPushRecord userDataPushRecord) {
        Integer uid = userDataPushRecord.getUid();
        Integer activityId = userDataPushRecord.getActivityId();
        Integer configId = userDataPushRecord.getConfigId();
        UserDataPushRecord existUserDataPushRecord = get(uid, activityId, configId);
        if (existUserDataPushRecord == null) {
            userDataPushRecordMapper.insert(userDataPushRecord);
        } else {
            userDataPushRecordMapper.update(null, new LambdaUpdateWrapper<UserDataPushRecord>()
                    .eq(UserDataPushRecord::getId, existUserDataPushRecord.getId())
                    .set(UserDataPushRecord::getTargetIdentify, userDataPushRecord.getTargetIdentify())
            );
        }
    }

    /**删除
     * @Description 
     * @author wwb
     * @Date 2021-11-03 14:15:32
     * @param id
     * @return void
    */
    public void delete(Long id) {
        userDataPushRecordMapper.deleteById(id);
    }

}