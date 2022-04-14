package com.chaoxing.activity.task.blacklist;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.blacklist.BlacklistAutoAddQueue;
import com.chaoxing.activity.service.queue.blacklist.handler.BlacklistAutoAddQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动结束自动添加黑名单任务
 * @author wwb
 * @version ver 1.0
 * @className BlacklistAutoAddTask
 * @description
 * @blame wwb
 * @date 2021-07-27 18:19:07
 */
@Slf4j
@Component
public class BlacklistAutoAddTask {

    @Resource
    private BlacklistAutoAddQueue blacklistAutoAddQueue;
    @Resource
    private BlacklistAutoAddQueueService blacklistAutoAddQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理活动结束自动添加黑名单任务 start");
        BlacklistAutoAddQueue.QueueParamDTO queueParamDto = blacklistAutoAddQueue.pop();
        try {
            if (queueParamDto == null) {
                return;
            }
            log.info("根据参数:{} 处理活动结束自动添加黑名单任务", JSON.toJSONString(queueParamDto));
            blacklistAutoAddQueueService.handle(queueParamDto.getActivityId());
            log.info("处理活动结束自动添加黑名单任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理活动结束自动添加黑名单任务 error:{}", JSON.toJSONString(queueParamDto), e.getMessage());
            e.printStackTrace();
            blacklistAutoAddQueue.push(queueParamDto);
        } finally {
            log.info("处理活动结束自动添加黑名单任务 end");
        }

    }

}