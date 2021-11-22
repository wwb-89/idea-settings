package com.chaoxing.activity.task.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.activity.WfwFormActivityDataUpdateQueue;
import com.chaoxing.activity.service.queue.activity.handler.WfwFormActivityDataUpdateQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**万能表单关联活动数据更新任务
 * @author wwb
 * @version ver 1.0
 * @className WfwFormActivityDataUpdateTask
 * @description
 * @blame wwb
 * @date 2021-11-22 18:06:43
 */
@Slf4j
@Component
public class WfwFormActivityDataUpdateTask {

    @Resource
    private WfwFormActivityDataUpdateQueue wfwFormActivityDataUpdateQueue;
    @Resource
    private WfwFormActivityDataUpdateQueueService wfwFormActivityDataUpdateQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        WfwFormActivityDataUpdateQueue.QueueParamDTO queueParam = wfwFormActivityDataUpdateQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            wfwFormActivityDataUpdateQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 处理万能表单关联活动数据更新任务error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            wfwFormActivityDataUpdateQueue.push(queueParam);
        }
    }

}