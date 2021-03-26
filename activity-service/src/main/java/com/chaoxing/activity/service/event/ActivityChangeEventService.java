package com.chaoxing.activity.service.event;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.queue.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**活动改变事件服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityChangeEventService
 * @description
 * @blame wwb
 * @date 2021-03-26 14:53:00
 */
@Slf4j
@Service
public class ActivityChangeEventService {

	@Resource
	private ActivityStatusUpdateQueueService activityStatusUpdateQueueService;
	@Resource
	private ActivityNameChangeNoticeQueueService activityNameChangeNoticeQueueService;
	@Resource
	private ActivityIsAboutToStartQueueService activityIsAboutToStartQueueService;
	@Resource
	private ActivityCoverUrlSyncQueueService activityCoverUrlSyncQueueService;
	@Resource
	private ActivityReleaseScopeChangeQueueService activityReleaseScopeChangeQueueService;
	@Resource
	private SecondClassroomActivityPushQueueService secondClassroomActivityPushQueueService;

	/**活动改变
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 14:54:18
	 * @param activity
	 * @return void
	*/
	public void change(Activity activity) {
		Integer status = activity.getStatus();
		Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
		if (Objects.equals(Activity.StatusEnum.DELETED, statusEnum)) {
			// 活动被删除
			activityIsAboutToStartQueueService.remove(activity.getId());
			// 删除表单记录
			secondClassroomActivityPushQueueService.add(activity);
		} else if (Objects.equals(Activity.StatusEnum.RELEASED, statusEnum)) {
			// 往表单推送数据
			secondClassroomActivityPushQueueService.add(activity);
			// 活动发布范围改变
			activityReleaseScopeChangeQueueService.add(activity.getId());
		} else {
			// 往表单推送数据
			secondClassroomActivityPushQueueService.add(activity);
			// 订阅活动状态处理
			activityStatusUpdateQueueService.addTime(activity);
			// 通知门户修改网站的title
			Integer pageId = activity.getPageId();
			if (pageId != null) {
				activityNameChangeNoticeQueueService.addActivityId(activity.getId());
			}
			// 订阅活动通知发送
			activityIsAboutToStartQueueService.add(activity);
			// 活动封面url同步
			activityCoverUrlSyncQueueService.add(activity.getId());
		}
	}

}