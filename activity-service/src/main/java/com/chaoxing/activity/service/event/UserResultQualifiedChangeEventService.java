package com.chaoxing.activity.service.event;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.user.UserResultQualifiedQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户成绩合格窗台变更事件服务
 * @author wwb
 * @version ver 1.0
 * @className UserResultQualifiedChangeEventService
 * @description
 * @blame wwb
 * @date 2021-06-25 10:24:34
 */
@Slf4j
@Service
public class UserResultQualifiedChangeEventService {

    @Resource
    private UserResultQualifiedQueueService userResultQualifiedQueueService;
    @Resource
    private ActivityQueryService activityQueryService;

    public void change(Integer uid, Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        Integer signId = activity.getSignId();
        if (signId != null) {
            userResultQualifiedQueueService.push(new UserResultQualifiedQueueService.QueueParamDTO(uid, signId));
        }
    }

}