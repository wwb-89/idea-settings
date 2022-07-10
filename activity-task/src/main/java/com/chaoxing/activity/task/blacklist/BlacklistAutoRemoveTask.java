package com.chaoxing.activity.task.blacklist;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.blacklist.BlacklistAutoRemoveQueue;
import com.chaoxing.activity.service.queue.blacklist.handler.BlacklistAutoRemoveQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**黑名单自动移除任务
 * @author wwb
 * @version ver 1.0
 * @className BlacklistAutoRemoveTask
 * @description
 * @blame wwb
 * @date 2021-07-27 18:03:20
 */
@Slf4j
@Component
public class BlacklistAutoRemoveTask {

    @Resource
    private BlacklistAutoRemoveQueue blacklistAutoRemoveQueue;
    @Resource
    private BlacklistAutoRemoveQueueService blacklistAutoRemoveQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理黑名单自动移除任务 start");
        BlacklistAutoRemoveQueue.QueueParamDTO queueParamDto = blacklistAutoRemoveQueue.pop();
        try {
            if (queueParamDto == null) {
                return;
            }
            log.info("根据参数:{} 处理黑名单自动移除任务", JSON.toJSONString(queueParamDto));
            blacklistAutoRemoveQueueService.handle(queueParamDto.getMarketId(), queueParamDto.getUid());
            log.info("处理黑名单自动移除任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理黑名单自动移除任务 error:{}", JSON.toJSONString(queueParamDto), e.getMessage());
            e.printStackTrace();
            blacklistAutoRemoveQueue.push(queueParamDto);
        } finally {
            log.info("处理黑名单自动移除任务 end");
        }
    }

}
