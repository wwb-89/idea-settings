package com.chaoxing.activity.service.queue.event.sign.handler;

import com.chaoxing.activity.dto.event.sign.SignChangeEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.UserStatSummary;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueue;
import com.chaoxing.activity.service.queue.user.UserStatSummaryQueue;
import com.chaoxing.activity.service.stat.UserStatSummaryQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignChangeEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 19:35:17
 */
@Slf4j
@Service
public class SignChangeEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private UserStatSummaryQueryService userStatSummaryQueryService;
    @Resource
    private UserStatSummaryQueue userStatSummaryQueue;
    @Resource
    private ActivityStatSummaryQueue activityStatSummaryQueue;

    public void handle(SignChangeEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer signId = eventOrigin.getSignId();
        Activity activity = activityQueryService.getBySignId(signId);
        Integer activityId = activity.getId();
        // 根据活动id查询用户活动汇总记录
        List<UserStatSummary> userStatSummaries = userStatSummaryQueryService.listActivityStatData(activityId);
        if (CollectionUtils.isNotEmpty(userStatSummaries)) {
            for (UserStatSummary userStatSummary : userStatSummaries) {
                UserStatSummaryQueue.QueueParamDTO userStatSummaryQueueParam = new UserStatSummaryQueue.QueueParamDTO(userStatSummary.getUid(), activityId);
                userStatSummaryQueue.pushUserSignStat(userStatSummaryQueueParam);
            }
        }
        activityStatSummaryQueue.push(activityId);
    }

}