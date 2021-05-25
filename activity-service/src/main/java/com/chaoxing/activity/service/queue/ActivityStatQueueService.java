package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.service.ActivityStatHandleService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;

/**活动统计队列服务
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/10 4:19 下午
 * <p>
 */

@Slf4j
@Service
public class ActivityStatQueueService {

    /** 队列缓存key */
    private static final String QUEUE_ACTIVITY_STAT_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_stat";

    @Resource
    private ActivityStatHandleService activityStatHandleService;

    @Resource
    private RedisTemplate redisTemplate;


    /**新增活动统计任务
     *
     * @Description
     * @author huxiaolong
     * @Date 2021-05-10 16:25:13
     * @param
     * @return void
     */
    @Transactional(rollbackFor = Exception.class)
    public void addActivityStatTask(Integer taskId) {
        if (taskId == null) {
            taskId = activityStatHandleService.addActivityStatTask();
        }

        if (taskId == null) {
            return;
        }

        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        listOperations.leftPush(QUEUE_ACTIVITY_STAT_CACHE_KEY, String.valueOf(taskId));
    }


    /**获取需要执行的活动统计任务id
     * @Description
     * @author wwb
     * @Date 2021-03-25 09:45:15
     * @param
     * @return java.lang.Integer
     */
    public Integer getActivityStatTask() {
        Integer taskId = null;
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        String taskIdStr = listOperations.rightPop(QUEUE_ACTIVITY_STAT_CACHE_KEY, Duration.ofMinutes(1));
        if (StringUtils.isNotBlank(taskIdStr)) {
            taskId = Integer.parseInt(taskIdStr);
        }
        return taskId;
    }
}
