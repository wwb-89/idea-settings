package com.chaoxing.activity.task;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.UserStatSummary;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.SignActionQueueService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueueService;
import com.chaoxing.activity.service.queue.user.UserActionRecordValidQueueService;
import com.chaoxing.activity.service.queue.user.UserStatSummaryQueueService;
import com.chaoxing.activity.service.stat.UserStatSummaryQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**报名签到改变
 * @author wwb
 * @version ver 1.0
 * @className SignActionTask
 * @description 报名签到改变：
 * 1、报名的新增、删除
 * 2、签到的新增、删除
 * 影响：
 * 1、活动统计中的报名人数、签到人数、签到率。
 * 2、删除报名和签到的时候会删除用户行为中的报名行为、签到行为。
 * 3、用户的活动情况（表单记录的数据）需要更新。
 * @blame wwb
 * @date 2021-05-25 19:50:57
 */
@Slf4j
@Component
public class SignActionTask {

    @Resource
    private SignActionQueueService signActionQueueService;
    @Resource
    private ActivityStatSummaryQueueService activityStatSummaryQueueService;
    @Resource
    private UserActionRecordValidQueueService userActionRecordValidQueueService;
    @Resource
    private UserStatSummaryQueueService userStatSummaryQueueService;
    @Resource
    private UserStatSummaryQueryService userStatSummaryQueryService;

    @Resource
    private ActivityQueryService activityQueryService;

    @Scheduled(fixedDelay = 1L)
    public void consumerSignAction() throws InterruptedException {
        SignActionQueueService.QueueParamDTO queueParam = signActionQueueService.pop();
        if (queueParam == null) {
            return;
        }
        // 活动统计需要重新计算签到数与签到率
        Integer signId = queueParam.getSignId();
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            Integer activityId = activity.getId();
            // 根据活动id查询用户活动汇总记录
            List<UserStatSummary> userStatSummaries = userStatSummaryQueryService.listActivityStatData(activityId);
            if (CollectionUtils.isNotEmpty(userStatSummaries)) {
                for (UserStatSummary userStatSummary : userStatSummaries) {
                    UserStatSummaryQueueService.QueueParamDTO userStatSummaryQueueParam = new UserStatSummaryQueueService.QueueParamDTO(userStatSummary.getUid(), activityId);
                    userStatSummaryQueueService.pushUserSignStat(userStatSummaryQueueParam);
                }
            }
            activityStatSummaryQueueService.push(activityId);
            SignActionQueueService.SignActionEnum signAction = queueParam.getSignAction();
            boolean valid = true;
            switch (signAction) {
                case ADD_SIGN_UP:
                case ADD_SIGN_IN:
                    // 新增报名、签到
                    break;
                case DELETE_SIGN_UP:
                case DELETE_SIGN_IN:
                    // 删除报名、签到
                    valid = false;
                    break;
                default:
            }
            if (signAction != null) {
                userActionRecordValidQueueService.push(new UserActionRecordValidQueueService.QueueParamDTO(activityId, queueParam.getIdentify(), valid, queueParam.getTime()));
            }
        }
    }

}
