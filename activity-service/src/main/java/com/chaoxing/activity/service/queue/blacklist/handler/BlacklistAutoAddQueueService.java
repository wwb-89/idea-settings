package com.chaoxing.activity.service.queue.blacklist.handler;

import com.chaoxing.activity.service.blacklist.BlacklistHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className BlacklistAutoAddQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 18:25:37
 */
@Slf4j
@Service
public class BlacklistAutoAddQueueService {

    @Resource
    private BlacklistHandleService blacklistHandleService;

    public void handle(Integer activityId) {
        blacklistHandleService.activityEndHandleBlacklist(activityId);
    }

}
