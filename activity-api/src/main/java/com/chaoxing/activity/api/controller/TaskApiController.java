package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityCoverService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**任务服务
 * @author wwb
 * @version ver 1.0
 * @className TaskApiController
 * @description
 * @blame wwb
 * @date 2021-01-20 11:30:35
 */
@RestController
@RequestMapping("task")
public class TaskApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityCoverService activityCoverService;

	/**同步没有封面url的活动封面url
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-20 11:33:12
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("sync/activity-cover-url")
	public RestRespDTO syncNoCoverUrlActivityCoverUrl() {
		List<Activity> activities = activityQueryService.listEmptyCoverUrl();
		if (CollectionUtils.isNotEmpty(activities)) {
			for (Activity activity : activities) {
				activityCoverService.noticeUpdateCoverUrl(activity.getId(), activity.getCoverCloudId());
			}
		}
		return RestRespDTO.success();
	}

}