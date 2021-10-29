package com.chaoxing.activity.service.queue.event.user.handler;

import com.chaoxing.activity.dto.event.user.UserSignedOutEventOrigin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserSignedOutEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-28 18:18:50
 */
@Slf4j
@Service
public class UserSignedOutEventQueueService {

    public void handle(UserSignedOutEventOrigin eventOrigin) {

    }

}
