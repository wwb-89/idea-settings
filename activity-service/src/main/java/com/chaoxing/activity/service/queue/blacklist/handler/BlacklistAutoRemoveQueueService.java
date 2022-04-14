package com.chaoxing.activity.service.queue.blacklist.handler;

import com.chaoxing.activity.model.Blacklist;
import com.chaoxing.activity.service.blacklist.BlacklistHandleService;
import com.chaoxing.activity.service.blacklist.BlacklistQueryService;
import com.chaoxing.activity.service.queue.notice.handler.BlacklistUserNoticeHandleService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className BlacklistAutoRemoveQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 18:26:01
 */
@Slf4j
@Service
public class BlacklistAutoRemoveQueueService {

    @Resource
    private BlacklistHandleService blacklistHandleService;
    @Resource
    private BlacklistUserNoticeHandleService blacklistUserNoticeHandleService;
    @Resource
    private BlacklistQueryService blacklistQueryService;

    public void handle(Integer marketId, Integer uid) {
        Blacklist userBlacklist = blacklistQueryService.getUserBlacklist(uid, marketId);
        // 自动移除黑名单
        blacklistHandleService.autoRemoveBlacklist(marketId, uid);
        if (userBlacklist == null) {
            return;
        }
        // 处理用户自动移除黑名单通知，放入黑名单通知队列
        blacklistUserNoticeHandleService.handleBlacklistRemoveNotice(marketId, Lists.newArrayList(userBlacklist));

    }

}
