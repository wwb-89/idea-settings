package com.chaoxing.activity.task.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.activity.ActivityTimingReleaseQueue;
import com.chaoxing.activity.service.queue.activity.handler.ActivityTimingReleaseQueueService;
import com.chaoxing.activity.util.exception.ActivityNotExistException;
import com.chaoxing.activity.util.exception.ActivityReleasedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**活动定时发布任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityTimingReleaseTask
 * @description
 * @blame wwb
 * @date 2021-06-08 10:22:45
 */
@Slf4j
@Component
public class ActivityTimingReleaseTask {

	@Resource
	private ActivityTimingReleaseQueue activityTimingReleaseQueue;
	@Resource
	private ActivityTimingReleaseQueueService activityTimingReleaseQueueService;

	@Scheduled(fixedDelay = 1L)
	public void handle() throws InterruptedException {
		ActivityTimingReleaseQueue.QueueParamDTO queueParam = activityTimingReleaseQueue.pop();
		if (queueParam == null) {
			return;
		}
		try {
			activityTimingReleaseQueueService.handle(queueParam);
		} catch (Exception e) {
			log.error("根据参数:{} 处理活动定时发布error:{}", JSON.toJSONString(queueParam), e.getMessage());
			e.printStackTrace();
			if (!isIgnoreException(e)) {
				activityTimingReleaseQueue.push(queueParam);
			}
		}
	}

	/**是否忽略异常
	 * @Description 需要忽略的异常
	 * 1、活动不存
	 * 2、活动已发布
	 * @author wwb
	 * @Date 2021-06-09 10:18:31
	 * @param e
	 * @return boolean
	*/
	private boolean isIgnoreException(Exception e) {
		if (e instanceof ActivityNotExistException || e instanceof ActivityReleasedException) {
			return true;
		}
		return false;
	}

}