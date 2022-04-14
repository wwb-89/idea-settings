package com.chaoxing.activity.task.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.activity.ClazzInteractionAddUserQueue;
import com.chaoxing.activity.service.queue.activity.handler.ClazzInteractionAddUserQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**班级互动班级添加用户任务
 * @author wwb
 * @version ver 1.0
 * @className ClazzInteractionAddUserQueueTask
 * @description
 * @blame wwb
 * @date 2021-12-29 18:30:20
 */
@Slf4j
@Component
public class ClazzInteractionAddUserQueueTask {

    @Resource
    private ClazzInteractionAddUserQueue clazzInteractionAddUserQueue;
    @Resource
    private ClazzInteractionAddUserQueueService clazzInteractionAddUserQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理班级互动班级添加用户任务 start");
        ClazzInteractionAddUserQueue.QueueParamDTO queueParam = clazzInteractionAddUserQueue.pop();
        try {
            if (queueParam == null) {
                return;
            }
            log.info("根据参数:{} 处理班级互动班级添加用户任务", JSON.toJSONString(queueParam));
            clazzInteractionAddUserQueueService.handle(queueParam);
            log.info("处理班级互动班级添加用户任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理班级互动班级添加用户任务 error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            clazzInteractionAddUserQueue.delayPush(queueParam);
        } finally {
            log.info("处理班级互动班级添加用户任务 end");
        }
    }

}