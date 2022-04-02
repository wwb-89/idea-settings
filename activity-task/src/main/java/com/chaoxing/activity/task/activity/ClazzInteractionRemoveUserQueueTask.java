package com.chaoxing.activity.task.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.activity.ClazzInteractionRemoveUserQueue;
import com.chaoxing.activity.service.queue.activity.handler.ClazzInteractionRemoveUserQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**班级互动班级移除用户任务
 * @author wwb
 * @version ver 1.0
 * @className ClazzInteractionRemoveUserQueueTask
 * @description
 * @blame wwb
 * @date 2021-12-29 18:30:53
 */
@Slf4j
@Component
public class ClazzInteractionRemoveUserQueueTask {

    @Resource
    private ClazzInteractionRemoveUserQueue clazzInteractionRemoveUserQueue;
    @Resource
    private ClazzInteractionRemoveUserQueueService clazzInteractionRemoveUserQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理班级互动班级移除用户任务 start");
        ClazzInteractionRemoveUserQueue.QueueParamDTO queueParam = clazzInteractionRemoveUserQueue.pop();
        try {
            if (queueParam == null) {
                return;
            }
            log.info("根据参数:{} 处理班级互动班级移除用户任务", JSON.toJSONString(queueParam));
            clazzInteractionRemoveUserQueueService.handle(queueParam);
            log.info("处理班级互动班级移除用户任务 success");
        } catch (Exception e) {
            log.error("根据参数:{} 处理班级互动班级移除用户任务 error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            clazzInteractionRemoveUserQueue.delayPush(queueParam);
        } finally {
            log.info("处理班级互动班级移除用户任务 end");
        }
    }

}