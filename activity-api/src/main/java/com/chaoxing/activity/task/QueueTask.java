package com.chaoxing.activity.task;

import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.queue.SignQueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className QueueTask
 * @description
 * @blame wwb
 * @date 2021-03-24 14:10:38
 */
@Component
public class QueueTask {

	@Resource
	private SignQueueService signQueueService;
	@Resource
	private SignApiService signApiService;

	/**一直运行（redis队列中会阻塞获取队列数据）
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-24 14:14:12
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1)
	public void handleRatingQueue() {
		String value = signQueueService.get();
		Integer signId = signQueueService.getSignIdFromValue(value);
		if (signId != null) {
			Integer uid = signQueueService.getUidFromValue(value);
			if (uid != null) {
				signApiService.handleNoticeRating(signId, uid);
			}
		}
	}

}
