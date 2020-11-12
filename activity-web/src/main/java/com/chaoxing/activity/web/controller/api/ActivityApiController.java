package com.chaoxing.activity.web.controller.api;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.module.SignFormDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityModule;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**活动api服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityApiController
 * @description
 * @blame wwb
 * @date 2020-11-11 10:54:37
 */
@RestController
@RequestMapping("api/activity")
public class ActivityApiController {

	@Resource
	private ActivityHandleService activityHandleService;

	@PostMapping("new")
	public RestRespDTO create(HttpServletRequest request, String activityJsonStr, String signJsonStr, String modulesJsonStr) {
		Activity activity = JSON.parseObject(activityJsonStr, Activity.class);
		// 本期不开启审核
		activity.setOpenAudit(false);
		SignFormDTO signForm = JSON.parseObject(signJsonStr, SignFormDTO.class);
		List<ActivityModule> activityModules = JSON.parseArray(modulesJsonStr, ActivityModule.class);

		return RestRespDTO.success();
	}

}