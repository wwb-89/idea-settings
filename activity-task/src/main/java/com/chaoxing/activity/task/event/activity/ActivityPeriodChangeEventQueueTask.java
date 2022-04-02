package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityPeriodChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityPeriodChangeEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityPeriodChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动学时改变事件队列任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityPeriodChangeEventQueueTask
 * @description
 * @blame wwb
 * @date 2022-01-13 17:17:29
 */
@Slf4j
@Component
public class ActivityPeriodChangeEventQueueTask {

	@Resource
	private ActivityPeriodChangeEventQueue activityPeriodChangeEventQueue;
	@Resource
	private ActivityPeriodChangeEventQueueService activityPeriodChangeEventQueueService;

	@Scheduled(fixedDelay = 10L)
	public void handle() throws InterruptedException {
		log.info("处理活动学时改变事件队列任务 start");
		ActivityPeriodChangeEventOrigin eventOrigin = activityPeriodChangeEventQueue.pop();
		log.info("根据参数:{} 处理活动学时改变事件队列任务", JSON.toJSONString(eventOrigin));
		if (eventOrigin == null) {
			return;
		}
		try {
			activityPeriodChangeEventQueueService.handle(eventOrigin);
			log.info("处理活动学时改变事件队列任务 success");
		} catch (Exception e) {
			log.error("根据参数:{} 处理活动学时改变事件队列任务 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
			e.printStackTrace();
			activityPeriodChangeEventQueue.push(eventOrigin);
		} finally {
			log.info("处理活动学时改变事件队列任务 end");
		}
	}

}
