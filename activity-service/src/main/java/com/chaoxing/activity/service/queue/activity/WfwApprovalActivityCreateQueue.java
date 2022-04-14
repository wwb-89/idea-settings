package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.dto.manager.wfwform.WfwApprovalActivityCreateDTO;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**表单审批创建活动队列
 * @author wwb
 * @version ver 1.0
 * @className WfwApprovalActivityCreateQueue
 * @description
 * @blame wwb
 * @date 2021-05-11 16:18:35
 */
@Slf4j
@Service
public class WfwApprovalActivityCreateQueue implements IQueue<WfwApprovalActivityCreateDTO> {

    private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "wfw_approval_create_activity";

    @Resource
    private RedissonClient redissonClient;

    public void push(WfwApprovalActivityCreateDTO wfwApprovalActivityCreateDto) {
        push(redissonClient, CACHE_KEY, wfwApprovalActivityCreateDto);
    }

    public void delayPush(WfwApprovalActivityCreateDTO wfwApprovalActivityCreateDto) {
        delayPush(redissonClient, CACHE_KEY, wfwApprovalActivityCreateDto);
    }

    public WfwApprovalActivityCreateDTO pop() throws InterruptedException {
        return pop(redissonClient, CACHE_KEY);
    }

}