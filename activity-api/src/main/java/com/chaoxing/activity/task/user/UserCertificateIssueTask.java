package com.chaoxing.activity.task.user;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.user.UserCertificateIssueQueue;
import com.chaoxing.activity.service.queue.user.handler.UserCertificateIssueQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户证书发放任务
 * @author wwb
 * @version ver 1.0
 * @className UserCertificateIssueTask
 * @description
 * @blame wwb
 * @date 2021-12-16 18:06:41
 */
@Slf4j
@Component
public class UserCertificateIssueTask {

    @Resource
    private UserCertificateIssueQueue userCertificateIssueQueue;
    @Resource
    private UserCertificateIssueQueueService userCertificateIssueQueueService;

    @Scheduled(fixedDelay = 10L)
    public void handle() throws InterruptedException {
        log.info("处理用户证书发放任务 start");
        UserCertificateIssueQueue.QueueParamDTO queueParam = userCertificateIssueQueue.pop();
        try {
            if (queueParam == null) {
                return;
            }
            log.info("根据参数:{} 处理用户证书发放任务", JSON.toJSONString(queueParam));
            userCertificateIssueQueueService.handle(queueParam);
            log.info("处理用户证书发放任务 success");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("根据参数:{} 处理用户证书发放任务 error:{}", JSON.toJSONString(queueParam), e);
            userCertificateIssueQueue.delayPush(queueParam);
        }finally {
            log.info("处理用户证书发放任务 end");
        }
    }

}