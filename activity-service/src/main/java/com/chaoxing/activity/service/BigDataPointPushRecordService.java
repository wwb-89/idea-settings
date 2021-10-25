package com.chaoxing.activity.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.BigDataPointPushRecordMapper;
import com.chaoxing.activity.model.BigDataPointPushRecord;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**大数据积分推送记录服务
 * @author wwb
 * @version ver 1.0
 * @className BigDataPointPushRecordService
 * @description
 * @blame wwb
 * @date 2021-10-13 15:11:04
 */
@Slf4j
@Service
public class BigDataPointPushRecordService {

    @Resource
    private BigDataPointPushRecordMapper bigDataPointPushRecordMapper;

    public void add(Integer uid, Integer activityId, Integer pointType) {
        BigDataPointPushRecord existBigDataPointPushRecord = get(uid, activityId);
        if (existBigDataPointPushRecord != null) {
            delete(uid, activityId);
        }
        BigDataPointPushRecord bigDataPointPushRecord = BigDataPointPushRecord.builder()
                .uid(uid)
                .activityId(activityId)
                .pointType(pointType)
                .build();
        bigDataPointPushRecordMapper.insert(bigDataPointPushRecord);
    }

    public BigDataPointPushRecord get(Integer uid, Integer activityId) {
        List<BigDataPointPushRecord> bigDataPointPushRecords = bigDataPointPushRecordMapper.selectList(new LambdaQueryWrapper<BigDataPointPushRecord>()
                .eq(BigDataPointPushRecord::getActivityId, activityId)
                .eq(BigDataPointPushRecord::getUid, uid)
        );
        return Optional.ofNullable(bigDataPointPushRecords).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
    }

    public void delete(Integer uid, Integer activityId) {
        bigDataPointPushRecordMapper.delete(new LambdaQueryWrapper<BigDataPointPushRecord>()
                .eq(BigDataPointPushRecord::getActivityId, activityId)
                .eq(BigDataPointPushRecord::getUid, uid)
        );
    }

    /**根据活动id查询已经推送的大数据积分记录
     * @Description 
     * @author wwb
     * @Date 2021-10-25 16:27:28
     * @param activityId
     * @return java.util.List<com.chaoxing.activity.model.BigDataPointPushRecord>
    */
    public List<BigDataPointPushRecord> listByActivityId(Integer activityId) {
        return bigDataPointPushRecordMapper.selectList(new LambdaQueryWrapper<BigDataPointPushRecord>()
                .eq(BigDataPointPushRecord::getActivityId, activityId)
        );
    }

}