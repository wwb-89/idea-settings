package com.chaoxing.activity.service.queue.event.user.handler;

import com.chaoxing.activity.dto.event.user.UserStatSummaryChangeEventOrigin;
import com.chaoxing.activity.service.queue.user.UserDataPrePushQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户活动统计信息改变事件任务队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryChangeEventQueueService
 * @description 用户活动统计改变会触发数据推送
 * @blame wwb
 * @date 2021-10-29 16:09:34
 */
@Slf4j
@Service
public class UserStatSummaryChangeEventQueueService {

    @Resource
    private UserDataPrePushQueue userDataPrePushQueue;

    public void handle(UserStatSummaryChangeEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer uid = eventOrigin.getUid();
        Integer activityId = eventOrigin.getActivityId();
        UserDataPrePushQueue.QueueParamDTO queueParam = new UserDataPrePushQueue.QueueParamDTO(uid, activityId);
        userDataPrePushQueue.push(queueParam);
    }

}
