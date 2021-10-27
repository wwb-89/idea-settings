package com.chaoxing.activity.task.user;

import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.queue.user.UserRatingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**用户评价任务
 * @author wwb
 * @version ver 1.0
 * @className UserRatingTask
 * @description
 * @blame wwb
 * @date 2021-03-24 14:10:38
 */
@Slf4j
@Component
public class UserRatingTask {

	@Resource
	private UserRatingQueue userRatingQueueService;
	@Resource
	private SignApiService signApiService;

	/**通知报名签到（用户评价信息有修改）
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-24 14:14:12
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void notice() throws InterruptedException {
		UserRatingQueue.QueueParamDTO queueParam = userRatingQueueService.pop();
		if (queueParam == null) {
			return;
		}
		Integer signId = queueParam.getSignId();
		Integer uid = queueParam.getUid();
		try {
			signApiService.handleNoticeRating(queueParam.getSignId(), queueParam.getUid());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("通知报名签到用户评价变更signId:{} uid:{} error:{}", signId, uid, e.getMessage());
			userRatingQueueService.push(queueParam);
		}
	}

}
