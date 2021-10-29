package com.chaoxing.activity.service.queue.event.user.handler;

import com.chaoxing.activity.dto.event.user.UserActivityStatChangeEventOrigin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserActivityStatChangeEventQueueService
 * @description 用户活动统计改变会触发数据推送
 * @blame wwb
 * @date 2021-10-29 16:09:34
 */
@Slf4j
@Service
public class UserActivityStatChangeEventQueueService {

    public void handle(UserActivityStatChangeEventOrigin eventOrigin) {

    }

}
