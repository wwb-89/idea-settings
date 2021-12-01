package com.chaoxing.activity.task.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.user.UserResultQueue;
import com.chaoxing.activity.service.queue.user.handler.UserResultQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户成绩更新任务
 * @author wwb
 * @version ver 1.0
 * @className UserScoreTask
 * @description 重新计算用户的成绩
 * @blame wwb
 * @date 2021-06-24 14:55:07
 */
@Slf4j
@Component
public class UserScoreTask {

    @Resource
    private UserResultQueue userResultQueue;
    @Resource
    private UserResultQueueService userResultQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        UserResultQueue.QueueParamDTO queueParam = userResultQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            userResultQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 处理用户成绩更新任务error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            userResultQueue.push(queueParam);
        }
    }

}