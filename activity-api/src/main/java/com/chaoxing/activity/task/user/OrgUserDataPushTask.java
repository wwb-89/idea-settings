package com.chaoxing.activity.task.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.user.OrgUserDataPushQueue;
import com.chaoxing.activity.service.queue.user.handler.OrgUserDataPushQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**机构用户数据推送任务
 * @author wwb
 * @version ver 1.0
 * @className OrgUserDataPushTask
 * @description
 * @blame wwb
 * @date 2021-11-02 17:01:22
 */
@Slf4j
@Component
public class OrgUserDataPushTask {

    @Resource
    private OrgUserDataPushQueue orgUserDataPushQueue;
    @Resource
    private OrgUserDataPushQueueService orgUserDataPushQueueService;

    @Scheduled(fixedDelay = 1L)
    public void handle() throws InterruptedException {
        OrgUserDataPushQueue.QueueParamDTO queueParam = orgUserDataPushQueue.pop();
        if (queueParam == null) {
            return;
        }
        try {
            orgUserDataPushQueueService.handle(queueParam);
        } catch (Exception e) {
            log.error("根据参数:{} 处理机构用户数据推送任务error:{}", JSON.toJSONString(queueParam), e.getMessage());
            e.printStackTrace();
            orgUserDataPushQueue.push(queueParam);
        }
    }

}
