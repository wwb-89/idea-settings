package com.chaoxing.activity.admin.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.ActivityManager;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityManagerService activityManagerService;

	/**查询组织者管理
	 * @Description
	 * @author wwb
	 * @Date 2021-03-27 12:39:05
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@LoginRequired
	@RequestMapping("list")
	public RestRespDTO list(HttpServletRequest request, @PathVariable Integer activityId) {
		Page<ActivityManager> page = HttpServletRequestUtils.buid(request);
		activityManagerService.paging(page, activityId);
 		return RestRespDTO.success(page);
	}

	/**新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-28 21:28:13
	 * @param request
	 * @param activityId
	 * @param activityManager
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("add")
	public RestRespDTO activityAddManager(HttpServletRequest request, @PathVariable Integer activityId, ActivityManager activityManager) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityManagerService.add(activityManager, loginUser);
		return RestRespDTO.success(activityManager);
	}

	/**批量新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-28 21:28:24
	 * @param activityId
	 * @param activityManagers
	 * @param request
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("add/batch")
	public RestRespDTO activityAddManager(HttpServletRequest request, @PathVariable Integer activityId, @RequestBody List<ActivityManager> activityManagers) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityManagerService.batchAdd(activityManagers, loginUser);
		return RestRespDTO.success();
	}

	/**删除
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-28 21:27:26
	 * @param request
	 * @param activityId
	 * @param uid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("{uid}/delete")
	public RestRespDTO activityDeleteManagers(HttpServletRequest request, @PathVariable Integer activityId, @PathVariable Integer uid) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityManagerService.delete(activityId, uid, loginUser);
		return RestRespDTO.success();
	}

	/**批量删除
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-28 21:27:38
	 * @param request
	 * @param activityId
	 * @param uids
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("delete/batch")
	public RestRespDTO activityDeleteManagers(HttpServletRequest request, @PathVariable Integer activityId, @RequestBody List<Integer> uids) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityManagerService.batchDelete(activityId, uids, loginUser);
		return RestRespDTO.success();
	}

}
