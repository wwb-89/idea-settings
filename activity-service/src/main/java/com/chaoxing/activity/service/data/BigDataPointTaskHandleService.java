package com.chaoxing.activity.service.data;

import com.chaoxing.activity.model.BigDataPointPushRecord;
import com.chaoxing.activity.model.UserStatSummary;
import com.chaoxing.activity.service.BigDataPointPushRecordService;
import com.chaoxing.activity.service.manager.bigdata.BigDataPointApiService;
import com.chaoxing.activity.service.queue.BigDataPointQueueService;
import com.chaoxing.activity.service.queue.BigDataPointTaskQueueService;
import com.chaoxing.activity.service.stat.UserStatSummaryQueryService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**大数据积分任务处理服务
 * @author wwb
 * @version ver 1.0
 * @className BigDataPointTaskHandleService
 * @description
 * @blame wwb
 * @date 2021-10-13 14:07:32
 */
@Slf4j
@Service
public class BigDataPointTaskHandleService {

    private static final List<Integer> PUSH_FIDS = Lists.newArrayList(23274, 170690);

    @Resource
    private BigDataPointPushRecordService bigDataPointPushRecordService;

    @Resource
    private BigDataPointApiService bigDataPointApiService;
    @Resource
    private UserStatSummaryQueryService userStatSummaryQueryService;

    @Resource
    private BigDataPointQueueService bigDataPointQueueService;

    /**处理任务
     * @Description 
     * @author wwb
     * @Date 2021-10-13 14:44:27
     * @param queueParam
     * @return void
    */
    public void handleTask(BigDataPointTaskQueueService.QueueParamDTO queueParam) {
        Integer createFid = Optional.ofNullable(queueParam).map(BigDataPointTaskQueueService.QueueParamDTO::getCreateFid).orElse(null);
        if (createFid == null) {
            return;
        }
        if (!PUSH_FIDS.contains(createFid)) {
            return;
        }
        // 查询活动下用户汇总数据
        List<UserStatSummary> userStatSummaries = userStatSummaryQueryService.listActivityStatData(queueParam.getActivityId());
        for (UserStatSummary userStatSummary : userStatSummaries) {
            BigDataPointQueueService.QueueParamDTO param = new BigDataPointQueueService.QueueParamDTO(userStatSummary.getUid(), createFid, userStatSummary.getActivityId(), calPointType(userStatSummary).getValue());
            bigDataPointQueueService.push(param);
        }
    }

    private BigDataPointApiService.PointTypeEnum calPointType(UserStatSummary userStatSummary) {
        Integer signInNum = Optional.ofNullable(userStatSummary.getSignInNum()).orElse(0);
        if (signInNum < 1) {
            return BigDataPointApiService.PointTypeEnum.PARTICIPATION;
        }
        BigDecimal signedInRate = Optional.ofNullable(userStatSummary.getSignedInRate()).orElse(BigDecimal.ZERO);
        // 活动报名的数量
        Integer signUpNum = Optional.ofNullable(userStatSummary.getSignUpNum()).orElse(0);
        if (signUpNum > 0) {
            // 有报名
            if (signedInRate.compareTo(BigDecimal.valueOf(1)) >= 0) {
                return BigDataPointApiService.PointTypeEnum.PARTICIPATION;
            } else {
                // 部分参与
                return BigDataPointApiService.PointTypeEnum.PART_PARTICIPATION;
            }
        } else {
            // 没有报名
            return BigDataPointApiService.PointTypeEnum.PARTICIPATION;
        }
    }

    public void dataPush(BigDataPointQueueService.QueueParamDTO queueParam) {
        Integer uid = queueParam.getUid();
        Integer activityId = queueParam.getActivityId();
        Integer pointType = queueParam.getPointType();
        BigDataPointApiService.PointTypeEnum pointTypeEnum = BigDataPointApiService.PointTypeEnum.fromValue(pointType);
        if (pointTypeEnum == null) {
            return;
        }
        switch (pointTypeEnum) {
            case PARTICIPATION:
            case PART_PARTICIPATION:
                bigDataPointPushRecordService.delete(uid, activityId);
                bigDataPointPushRecordService.add(uid, activityId, pointType);
                break;
            case CANCEL_PARTICIPATION:
            case CANCEL_PART_PARTICIPATION:
                // 获取推送时候的pointType
                BigDataPointPushRecord existBigDataPointPushRecord = bigDataPointPushRecordService.get(uid, activityId);
                if (existBigDataPointPushRecord != null) {
                    BigDataPointApiService.PointTypeEnum hisPointTypeEnum = BigDataPointApiService.PointTypeEnum.fromValue(existBigDataPointPushRecord.getPointType());
                    if (hisPointTypeEnum != null) {
                        pointTypeEnum = BigDataPointApiService.PointTypeEnum.fromValue(hisPointTypeEnum.getReverseValue());
                    }
                }
                bigDataPointPushRecordService.delete(uid, activityId);
                break;
            default:

        }
        LocalDateTime now = LocalDateTime.now();
        BigDataPointApiService.PointPushParamDTO param = new BigDataPointApiService.PointPushParamDTO(queueParam.getUid(), queueParam.getFid(), pointTypeEnum, now);
        bigDataPointApiService.pointPush(param);
    }

}