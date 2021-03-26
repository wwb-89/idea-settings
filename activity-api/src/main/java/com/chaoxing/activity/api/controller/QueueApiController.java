package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.ActivityIsAboutToStartQueueService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**队列api服务
 * @author wwb
 * @version ver 1.0
 * @className QueueApiController
 * @description
 * @blame wwb
 * @date 2021-03-26 18:30:34
 */
@RestController
@RequestMapping("queue")
public class QueueApiController {

	@Resource
	private ActivityIsAboutToStartQueueService activityIsAboutToStartQueueService;
	@Resource
	private ActivityQueryService activityQueryService;

	/**初始化签到的开始结束时间队列
	 * @Description
	 * @author wwb
	 * @Date 2021-03-25 20:16:34
	 * @param
	 * @return com.chaoxing.sign.dto.RestRespDTO
	 */
	@RequestMapping("init/activity-is-about-to-start")
	public RestRespDTO initActivityIsAboutToStartQueue() {
		List<Activity> activities = activityQueryService.list();
		if (CollectionUtils.isNotEmpty(activities)) {
			for (Activity activity : activities) {
				activityIsAboutToStartQueueService.add(activity);
			}
		}
		return RestRespDTO.success();
	}

}