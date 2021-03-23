package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.ActivityManager;
import com.chaoxing.activity.service.activity.ActivityManagerService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.util.Pagination;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**活动管理者api服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityManagerApiController
 * @description
 * @blame wwb
 * @date 2021-03-23 17:08:56
 */
@RestController
@RequestMapping("api/activity/{activityId}/manager")
public class ActivityManagerApiController {

	@Resource
	private ActivityManagerService activityManagerService;
	@Resource
	private ActivityValidationService activityValidationService;

	@ResponseBody
	@RequestMapping("add")
	public RestRespDTO activityAddManager(HttpServletRequest request, @PathVariable Integer activityId, ActivityManager activityManager) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityManagerService.add(activityManager, loginUser);
		return RestRespDTO.success(activityManager);
	}

	@ResponseBody
	@RequestMapping("add/batch")
	public RestRespDTO activityAddManager(@PathVariable Integer activityId, @RequestBody List<ActivityManager> activityManagers, HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		List<ActivityManager> adds = new ArrayList<>(activityManagers.size());
		activityManagers.forEach(activityManager -> {
			activityManager.setCreateUid(loginUser.getUid());
			activityManager.setActivityId(activityId);
			if (activityManagerService.add(activityManager, loginUser)) {
				adds.add(activityManager);
			}
		});
		return RestRespDTO.success(adds);
	}

	@ResponseBody
	@RequestMapping("list")
	public RestRespDTO activityManagers(@PathVariable Integer activityId, Pagination pagination, HttpServletRequest request) {
		List<ActivityManager> activityManagers = activityManagerService.getActivityManagers(activityId, pagination);
		return RestRespDTO.success(activityManagers);
	}

	@ResponseBody
	@RequestMapping("delete")
	public RestRespDTO activityDeleteManagers(@PathVariable Integer activityId, Integer uid, HttpServletRequest request) {
		activityManagerService.delete(activityId, uid);
		return RestRespDTO.success();
	}

	@ResponseBody
	@RequestMapping("delete/batch")
	public RestRespDTO activityDeleteManagers(@PathVariable Integer activityId, @RequestBody List<Integer> uids, HttpServletRequest request) {
		activityManagerService.deleteBatch(activityId, uids);
		return RestRespDTO.success();
	}

}
