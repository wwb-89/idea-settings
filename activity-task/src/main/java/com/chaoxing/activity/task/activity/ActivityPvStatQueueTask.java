package com.chaoxing.activity.task.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.activity.ActivityPvStatQueue;
import com.chaoxing.activity.service.queue.activity.handler.ActivityPvStatQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动pv统计队列任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityPvStatQueueTask
 * @description
 * @blame wwb
 * @date 2022-01-19 15:57:15
 */
@Slf4j
@Component
public class ActivityPvStatQueueTask {

	@Resource
	private ActivityPvStatQueue activityPvStatQueue;
	@Resource
	private ActivityPvStatQueueService activityPvStatQueueService;

	@Scheduled(fixedDelay = 10L)
	public void handle() throws InterruptedException {
		log.info("处理活动pv统计队列任务 start");
		ActivityPvStatQueue.QueueParamDTO queueParam = activityPvStatQueue.pop();
		try {
			if (queueParam == null) {
				return;
			}
			log.info("根据参数:{} 处理活动pv统计队列任务", JSON.toJSONString(queueParam));
			activityPvStatQueueService.handle(queueParam);
			log.info("处理活动pv统计队列任务 success");
		} catch (Exception e) {
			log.error("根据参数:{} 处理活动pv统计队列任务 error:{}", JSON.toJSONString(queueParam), e.getMessage());
			e.printStackTrace();
			activityPvStatQueue.delayPush(queueParam);
		} finally {
			log.info("处理活动pv统计队列任务 end");
		}
	}

}
