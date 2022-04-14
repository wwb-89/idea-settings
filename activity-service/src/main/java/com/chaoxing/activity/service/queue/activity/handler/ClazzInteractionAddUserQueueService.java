package com.chaoxing.activity.service.queue.activity.handler;

import com.chaoxing.activity.service.manager.module.ClazzInteractionApiService;
import com.chaoxing.activity.service.queue.activity.ClazzInteractionAddUserQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ClazzInteractionAddUserQueueService
 * @description
 * @blame wwb
 * @date 2021-12-29 18:27:02
 */
@Slf4j
@Service
public class ClazzInteractionAddUserQueueService {

    @Resource
    private ClazzInteractionApiService clazzInteractionApiService;

    public void handle(ClazzInteractionAddUserQueue.QueueParamDTO queueParam) {
        if (queueParam == null) {
            return;
        }
        clazzInteractionApiService.classAddUser(queueParam.getUid(), queueParam.getActivityId());
    }

}
