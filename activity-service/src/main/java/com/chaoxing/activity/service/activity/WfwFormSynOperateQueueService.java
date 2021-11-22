package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.dto.activity.create.ActivityCreateFromFormParamDTO;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/27 16:40
 * <p>
 */
@Slf4j
@Service
public class WfwFormSynOperateQueueService {
    
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
