package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.activity.stat.ActivityStatHandleService;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**活动统计队列服务
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/10 4:19 下午
 * <p>
 */

@Slf4j
@Service
public class ActivityStatQueue implements IQueue<Integer> {

    /** 队列缓存key */
    private static final String QUEUE_ACTIVITY_STAT_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_stat";

    @Resource
    private ActivityStatHandleService activityStatHandleService;

    @Resource
    private RedissonClient redissonClient;


    /**向队列批量新增统计任务
    * @Description
    * @author huxiaolong
    * @Date 2021-06-09 17:35:34
    * @param
    * @return void
    */
    public void batchAddActivityStatTask() {
        List<Integer> taskIds = activityStatHandleService.reAddAllActivityStat();
        RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(QUEUE_ACTIVITY_STAT_CACHE_KEY);
        for (Integer taskId : taskIds) {
            blockingDeque.offer(taskId);
        }
    }


    /**重跑失败任务
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-14 14:38:07
     * @return
     */
    public void rerunFailedStatActivityTask() {
        List<Integer> taskIds = activityStatHandleService.rerunFailedStatActivity();
        RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(QUEUE_ACTIVITY_STAT_CACHE_KEY);
        for (Integer taskId : taskIds) {
            blockingDeque.offer(taskId);
        }
    }


    /**将失败任务改为待处理，并将所有待处理的任务加入任务队列
     * @Description
     * @author huxiaolong
     * @Date 2021-12-15 11:30:29
     * @return
     */
    public void rerunWaitHandleStatActivityTask() {
        List<Integer> taskIds = activityStatHandleService.getWaitHandleStatActivityTaskIds();
        RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(QUEUE_ACTIVITY_STAT_CACHE_KEY);
        for (Integer taskId : taskIds) {
            blockingDeque.offer(taskId);
        }
    }

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
        push(redissonClient, QUEUE_ACTIVITY_STAT_CACHE_KEY, taskId);
    }


    /**获取需要执行的活动统计任务id
     * @Description
     * @author wwb
     * @Date 2021-03-25 09:45:15
     * @param
     * @return java.lang.Integer
     */
    public Integer getActivityStatTask() throws InterruptedException {
        return pop(redissonClient, QUEUE_ACTIVITY_STAT_CACHE_KEY);
    }
}
