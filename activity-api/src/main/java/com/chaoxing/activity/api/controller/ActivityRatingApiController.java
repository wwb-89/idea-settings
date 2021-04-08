package com.chaoxing.activity.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityRatingDetail;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.rating.ActivityRatingQueryService;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**活动评价api服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityRatingApiController
 * @description
 * @blame wwb
 * @date 2021-03-15 18:25:02
 */
@RestController
@RequestMapping("activity/rating")
public class ActivityRatingApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityRatingQueryService activityRatingQueryService;

	/**根据报名签到id和uid列表查询活动评价列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-15 19:13:22
	 * @param data
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("list")
	public RestRespDTO listBySignId(@RequestBody String data) {
		JSONObject jsonObject = JSON.parseObject(data);
		Integer signId = jsonObject.getInteger("signId");
		List<Integer> uids = JSON.parseArray(jsonObject.getString("uids"), Integer.class);
		Activity activity = activityQueryService.getBySignId(signId);
		List<ActivityRatingDetail> activityRatingDetails = Lists.newArrayList();
		if (activity != null) {
			activityRatingDetails = activityRatingQueryService.listDetail(activity.getId(), uids);
		}
		return RestRespDTO.success(activityRatingDetails);
	}

}