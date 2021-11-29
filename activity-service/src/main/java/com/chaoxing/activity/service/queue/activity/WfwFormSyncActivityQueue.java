package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.dto.activity.create.ActivityCreateFromFormParamDTO;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**万能表单同步活动队列
 * @author wwb
 * @version ver 1.0
 * @className WfwFormSyncActivityQueue
 * @description
 * @blame wwb
 * @date 2021-11-29 10:40:38
 */
@Slf4j
@Service
public class WfwFormSyncActivityQueue {

    private static final String ACTIVITY_FORM_SYNC_OPERATE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "form_data" + CacheConstant.CACHE_KEY_SEPARATOR + "sync_operate";

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * @Description
     * @author huxiaolong
     * @Date 2021-08-27 16:45:16
     * @param queueParam
     * @return void
     */
    public void push(ActivityCreateFromFormParamDTO queueParam) {
        ListOperations listOperations = redisTemplate.opsForList();
        listOperations.leftPush(ACTIVITY_FORM_SYNC_OPERATE_KEY, queueParam);
    }

    /**
     * @Description
     * @author huxiaolong
     * @Date 2021-08-27 16:46:10
     * @param
     * @return com.chaoxing.activity.dto.activity.ActivityFormSyncParamDTO
     */
    public ActivityCreateFromFormParamDTO pop() {
        ListOperations<String, ActivityCreateFromFormParamDTO> listOperations = redisTemplate.opsForList();
        return listOperations.rightPop(ACTIVITY_FORM_SYNC_OPERATE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
    }

}
