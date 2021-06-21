package com.chaoxing.activity.task;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.queue.activity.ActivityNameChangeNoticeQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**门户网站标题更新任务
 * @author wwb
 * @version ver 1.0
 * @className MhWebTitleUpdateTask
 * @description
 * @blame wwb
 * @date 2021-03-26 15:31:34
 */
@Slf4j
@Component
public class MhWebTitleUpdateTask {

	@Resource
	private ActivityNameChangeNoticeQueueService activityNameChangeNoticeQueueService;
	@Resource
	private MhApiService mhApiService;
	@Resource
	private ActivityQueryService activityQueryService;

	/**网站标题推送
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 15:32:36
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void push() throws InterruptedException {
		Integer activityId = activityNameChangeNoticeQueueService.pop();
		if (activityId == null) {
			return;
		}
		Activity activity = activityQueryService.getById(activityId);
		if (activity != null) {
			Integer pageId = activity.getPageId();
			if (pageId != null) {
				String name = activity.getName();
				Integer createUid = activity.getCreateUid();
				try {
					mhApiService.updateWebTitle(pageId, name, createUid);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("更新门户网站title activityId:{} error:{}", activityId, e.getMessage());
					activityNameChangeNoticeQueueService.push(activityId);
				}
			}
		}
	}

}