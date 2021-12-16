package com.chaoxing.activity.service.queue.event.user;

import com.chaoxing.activity.dto.event.user.UserCertificateIssueEventOrigin;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户证书发放事件队列
 * @author wwb
 * @version ver 1.0
 * @className UserCertificateIssueEventQueue
 * @description 用户证书发放后的一些处理
 * @blame wwb
 * @date 2021-12-16 15:49:16
 */
@Slf4j
@Service
public class UserCertificateIssueEventQueue implements IQueue<UserCertificateIssueEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "user_certificate_issue";

    @Resource
    private RedissonClient redissonClient;

    public void push(UserCertificateIssueEventOrigin eventOrigin) {
        push(redissonClient, KEY, eventOrigin);
    }

    public void delayPush(UserCertificateIssueEventOrigin eventOrigin) {
        delayPush(redissonClient, KEY, eventOrigin);
    }

    public UserCertificateIssueEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}
