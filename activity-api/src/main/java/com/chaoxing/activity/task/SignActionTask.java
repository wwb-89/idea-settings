package com.chaoxing.activity.task;

import com.chaoxing.activity.service.queue.SignActionQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignActionTask
 * @description
 * @blame wwb
 * @date 2021-05-25 19:50:57
 */
@Slf4j
@Component
public class SignActionTask {

    @Resource
    private SignActionQueueService signActionQueueService;

    public void signInNumChangeActionHandle() {
        Integer signId = signActionQueueService.getSignInNumChangeAction();
        if (signId == null) {
            return;
        }
        // TODO
    }

}
