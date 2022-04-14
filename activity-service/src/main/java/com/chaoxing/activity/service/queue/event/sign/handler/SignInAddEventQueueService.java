package com.chaoxing.activity.service.queue.event.sign.handler;

import com.chaoxing.activity.dto.event.sign.SignChangeEventOrigin;
import com.chaoxing.activity.dto.event.sign.SignInAddEventOrigin;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.event.sign.SignChangeEventQueue;
import com.chaoxing.activity.service.queue.user.UserActionRecordValidQueue;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignInAddEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 19:10:04
 */
@Slf4j
@Service
public class SignInAddEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private SignChangeEventQueue signChangeEventQueue;
    @Resource
    private UserActionRecordValidQueue userActionRecordValidQueue;

    public void handle(SignInAddEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer signId = eventOrigin.getSignId();
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity == null) {
            return;
        }
        SignChangeEventOrigin signChangeEventOrigin = SignChangeEventOrigin.builder()
                .signId(signId)
                .timestamp(eventOrigin.getTimestamp())
                .build();
        signChangeEventQueue.push(signChangeEventOrigin);
        userActionRecordValidQueue.push(new UserActionRecordValidQueue.QueueParamDTO(activity.getId(), String.valueOf(eventOrigin.getSignInId()), true, DateUtils.timestamp2Date(eventOrigin.getTimestamp())));
    }

}
