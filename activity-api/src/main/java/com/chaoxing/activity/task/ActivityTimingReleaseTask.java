package com.chaoxing.activity.task;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.queue.ActivityTimingReleaseQueueService;
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
	private ActivityTimingReleaseQueueService activityTimingReleaseQueueService;
	@Resource
	private ActivityHandleService activityHandleService;

	@Scheduled(fixedDelay = 1L)
	public void release() throws InterruptedException {
		ActivityTimingReleaseQueueService.QueueParamDTO queueParam = activityTimingReleaseQueueService.pop();
		if (queueParam == null) {
			return;
		}
		Integer activityId = queueParam.getActivityId();
		LoginUserDTO loginUser = queueParam.getLoginUser();
		try {
			activityHandleService.release(activityId, loginUser);
		} catch (Exception e) {
			e.printStackTrace();
			if (!isIgnoreException(e)) {
				activityTimingReleaseQueueService.push(queueParam);
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