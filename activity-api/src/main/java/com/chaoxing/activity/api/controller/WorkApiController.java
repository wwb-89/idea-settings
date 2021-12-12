package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

/**作品征集相关
 * @author wwb
 * @version ver 1.0
 * @className WorkApiController
 * @description
 * @blame wwb
 * @date 2021-12-12 11:54:21
 */
@Slf4j
@RestController
@RequestMapping("work")
public class WorkApiController {

	@Resource
	private ActivityQueryService activityQueryService;

	/**根据门户websiteId获取作品征集id
	 * @Description 
	 * @author wwb
	 * @Date 2021-12-12 11:56:50
	 * @param websiteId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("id/from-website-id/{websiteId}")
	public RestRespDTO getWorkIdByMhWebsiteId(@PathVariable Integer websiteId) {
		// 根据websiteId查询活动id
		Activity activity = activityQueryService.getByWebsiteId(websiteId);
		return RestRespDTO.success(Optional.ofNullable(activity).map(Activity::getWorkId).orElse(null));
	}

}
