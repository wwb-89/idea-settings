package com.chaoxing.activity.task.event.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.user.UserCertificateIssueEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserCertificateIssueEventQueue;
import com.chaoxing.activity.service.queue.event.user.handler.UserCertificateIssueEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户证书发放事件任务
 * @author wwb
 * @version ver 1.0
 * @className UserCertificateIssueEventTask
 * @description
 * @blame wwb
 * @date 2021-12-16 15:59:08
 */
@Slf4j
@Component
public class UserCertificateIssueEventTask {

    @Resource
    private UserCertificateIssueEventQueue userCertificateIssueEventQueue;
    @Resource
    private UserCertificateIssueEventQueueService userCertificateIssueEventQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理用户证书发放事件任务 start");
        UserCertificateIssueEventOrigin eventOrigin = userCertificateIssueEventQueue.pop();
        try {
            if (eventOrigin == null) {
                return;
            }
            log.info("根据参数:{} 处理用户证书发放事件任务", JSON.toJSONString(eventOrigin));
            userCertificateIssueEventQueueService.handle(eventOrigin);
            log.info("处理用户证书发放事件任务 success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("根据参数:{} 处理用户证书发放事件任务 error:{}", JSON.toJSONString(eventOrigin), e);
            userCertificateIssueEventQueue.delayPush(eventOrigin);
        }finally {
            log.info("处理用户证书发放事件任务 end");
        }
    }

}