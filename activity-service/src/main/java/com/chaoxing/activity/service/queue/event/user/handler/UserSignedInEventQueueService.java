package com.chaoxing.activity.service.queue.event.user.handler;

import com.chaoxing.activity.dto.event.user.UserSignedInEventOrigin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserSignedInEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-28 10:47:45
 */
@Slf4j
@Service
public class UserSignedInEventQueueService {

    public void handle(UserSignedInEventOrigin eventOrigin) {

    }

}
