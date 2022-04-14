package com.chaoxing.activity.task.event.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.event.activity.ActivityCreditChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.activity.ActivityCreditChangeEventQueue;
import com.chaoxing.activity.service.queue.event.activity.handler.ActivityCreditChangeEventQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动学分改变事件队列任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCreditChangeEventQueueTask
 * @description
 * @blame wwb
 * @date 2022-01-13 17:25:49
 */
@Slf4j
@Component
public class ActivityCreditChangeEventQueueTask {

	@Resource
	private ActivityCreditChangeEventQueue activityCreditChangeEventQueue;
	@Resource
	private ActivityCreditChangeEventQueueService activityCreditChangeEventQueueService;

	@Scheduled(fixedDelay = 10L)
	public void handle() throws InterruptedException {
		log.info("处理活动学分改变事件队列任务 start");
		ActivityCreditChangeEventOrigin eventOrigin = activityCreditChangeEventQueue.pop();
		log.info("根据参数:{} 处理活动学分改变事件队列任务", JSON.toJSONString(eventOrigin));
		if (eventOrigin == null) {
			return;
		}
		try {
			activityCreditChangeEventQueueService.handle(eventOrigin);
			log.info("处理活动学分改变事件队列任务 success");
		} catch (Exception e) {
			log.error("根据参数:{} 处理活动学分改变事件队列任务 error:{}", JSON.toJSONString(eventOrigin), e.getMessage());
			e.printStackTrace();
			activityCreditChangeEventQueue.push(eventOrigin);
		} finally {
			log.info("处理活动学分改变事件队列任务 end");
		}
	}

}
