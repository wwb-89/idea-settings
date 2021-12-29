package com.chaoxing.activity.service.queue.activity.handler;

import com.chaoxing.activity.service.manager.module.ClazzInteractionApiService;
import com.chaoxing.activity.service.queue.activity.ClazzInteractionRemoveUserQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ClazzInteractionRemoveUserQueueService
 * @description
 * @blame wwb
 * @date 2021-12-29 18:28:13
 */
@Slf4j
@Service
public class ClazzInteractionRemoveUserQueueService {

    @Resource
    private ClazzInteractionApiService clazzInteractionApiService;

    public void handle(ClazzInteractionRemoveUserQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        clazzInteractionApiService.classRemoveUser(queueParam.getUid(), queueParam.getActivityId());
    }

}
