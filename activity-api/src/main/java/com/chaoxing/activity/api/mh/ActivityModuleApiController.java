package com.chaoxing.activity.api.mh;

import com.chaoxing.activity.dto.RestRespDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityModuleApiController
 * @description 为门户提供活动模块的接口数据
 * @blame wwb
 * @date 2020-11-23 16:17:08
 */
@RestController
@RequestMapping("mh/activity")
public class ActivityModuleApiController {

	@RequestMapping("{activityId}/")
	public RestRespDTO websiteData(@PathVariable Integer activityId) {
		return RestRespDTO.success();
	}

}