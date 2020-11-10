package com.chaoxing.activity.web.controller.api;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**活动分类服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyApiController
 * @description
 * @blame wwb
 * @date 2020-11-10 15:06:32
 */
@RestController
@RequestMapping("api/activity/classify")
public class ActivityClassifyApiController {

	public RestRespDTO add(HttpServletRequest request, ActivityClassify activityClassify) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);

		return RestRespDTO.success();
	}

	public RestRespDTO edit() {
		return RestRespDTO.success();
	}

	public RestRespDTO delete() {
		return RestRespDTO.success();
	}

}