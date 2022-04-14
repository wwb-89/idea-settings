package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.event.activity.CustomAppInterfaceCallQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.CustomAppInterfaceCallQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**自定义应用接口调用任务
 * @description:
 * @author: huxiaolong
 * @date: 2022/2/15 2:09 PM
 * @version: 1.0
 */
@Slf4j
@Component
public class CustomAppInterfaceCallTask {
    @Resource
    private CustomAppInterfaceCallQueue customAppInterfaceCallQueue;
    @Resource
    private CustomAppInterfaceCallQueueService customAppInterfaceCallQueueService;


    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        CustomAppInterfaceCallQueue.QueueParamDTO param = customAppInterfaceCallQueue.pop();
        if (param == null) {
            return;
        }
        try {
            customAppInterfaceCallQueueService.handle(param);
        } catch (Exception e) {
            log.error("根据参数:{} 调用自定义接口任务error:{}", JSON.toJSONString(param), e.getMessage());
            e.printStackTrace();
        }

    }
}
