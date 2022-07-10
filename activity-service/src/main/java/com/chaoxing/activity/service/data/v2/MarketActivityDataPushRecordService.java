package com.chaoxing.activity.service.data.v2;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ActivityDataPushRecordMapper;
import com.chaoxing.activity.model.ActivityDataPushRecord;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**活动市场下活动数据推送记录服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataPushRecordService
 * @description
 * @blame wwb
 * @date 2021-11-01 15:53:11
 */
@Slf4j
@Service
public class MarketActivityDataPushRecordService {

    @Resource
    private ActivityDataPushRecordMapper activityDataPushRecordMapper;

    /**根据活动id和配置查询查询活动的推送记录
     * @Description 
     * @author wwb
     * @Date 2021-11-01 15:57:03
     * @param activityId
     * @param configId
     * @return com.chaoxing.activity.model.ActivityDataPushRecord
    */
    public ActivityDataPushRecord get(Integer activityId, Integer configId) {
        List<ActivityDataPushRecord> activityDataPushRecords = activityDataPushRecordMapper.selectList(new LambdaQueryWrapper<ActivityDataPushRecord>()
                .eq(ActivityDataPushRecord::getActivityId, activityId)
                .eq(ActivityDataPushRecord::getConfigId, configId)
        );
        return Optional.ofNullable(activityDataPushRecords).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
    }

    /**删除数据
     * @Description 
     * @author wwb
     * @Date 2021-11-01 16:03:00
     * @param id
     * @return void
    */
    public void delete(Integer id) {
        activityDataPushRecordMapper.deleteById(id);
    }

    /**新增活动推送记录
     * @Description 
     * @author wwb
     * @Date 2021-11-01 16:17:26
     * @param activityDataPushRecord
     * @return void
    */
    public void add(ActivityDataPushRecord activityDataPushRecord) {
        activityDataPushRecordMapper.insert(activityDataPushRecord);
    }

}