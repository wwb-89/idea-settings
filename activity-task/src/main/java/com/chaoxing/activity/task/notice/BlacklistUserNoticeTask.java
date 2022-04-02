package com.chaoxing.activity.task.notice;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.notice.BlacklistUserNoticeQueue;
import com.chaoxing.activity.service.queue.notice.handler.BlacklistUserNoticeQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**黑名单通知任务
 * @description:
 * @author: huxiaolong
 * @date: 2021/12/6 5:14 下午
 * @version: 1.0
 */
@Slf4j
@Component
public class BlacklistUserNoticeTask {

    @Resource
    private BlacklistUserNoticeQueue blacklistUserNoticeQueue;
    @Resource
    private BlacklistUserNoticeQueueService blacklistUserNoticeQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        BlacklistUserNoticeQueue.QueueParamDTO queueParam = blacklistUserNoticeQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            blacklistUserNoticeQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 给黑名单用户发送通知error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            blacklistUserNoticeQueue.push(queueParam);
        }
    }
}
