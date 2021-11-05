package com.chaoxing.activity.service.queue.event.sign.handler;

import com.chaoxing.activity.dto.event.sign.SignChangeEventOrigin;
import com.chaoxing.activity.dto.event.sign.SignUpAddEventOrigin;
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
 * @className SignUpAddEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 19:09:31
 */
@Slf4j
@Service
public class SignUpAddEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private UserActionRecordValidQueue userActionRecordValidQueue;
    @Resource
    private SignChangeEventQueue signChangeEventQueue;

    public void handle(SignUpAddEventOrigin eventOrigin) {
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
        userActionRecordValidQueue.push(new UserActionRecordValidQueue.QueueParamDTO(activity.getId(), String.valueOf(eventOrigin.getSignUpId()), true, DateUtils.timestamp2Date(eventOrigin.getTimestamp())));
    }

}
