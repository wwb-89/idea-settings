package com.chaoxing.activity.task;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.UserActionRecord;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.activity.ActivityStatSummaryQueueService;
import com.chaoxing.activity.service.queue.SignActionQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
    private ActivityQueryService activityQueryService;

    @Scheduled(fixedDelay = 1L)
    public void signActionTask() throws InterruptedException {
        SignActionQueueService.QueueParamDTO queueParam = signActionQueueService.pop();
        if (queueParam == null) {
            return;
        }
        // 活动统计需要重新计算签到数与签到率
        Integer signId = queueParam.getSignId();
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            activityStatSummaryQueueService.push(activity.getId());
        }
    }

}
