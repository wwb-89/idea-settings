package com.chaoxing.activity.web.controller.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.module.SignFormDTO;
import com.chaoxing.activity.dto.query.ActivityManageQueryDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.web.util.HttpServletRequestUtils;
import com.chaoxing.activity.web.util.LoginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

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
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private GroupService groupService;

	/**创建活动
	 * @Description 需要活动对象
	 * @author wwb
	 * @Date 2020-11-13 09:45:31
	 * @param request
	 * @param activityJsonStr
	 * @param signJsonStr
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("new")
	public RestRespDTO create(HttpServletRequest request, String activityJsonStr, String signJsonStr) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Activity activity = JSON.parseObject(activityJsonStr, Activity.class);
		// 本期不开启审核
		activity.setOpenAudit(false);
		SignFormDTO signForm = JSON.parseObject(signJsonStr, SignFormDTO.class);
		activityHandleService.add(activity, signForm, loginUser, request);
		return RestRespDTO.success(activity);
	}

	/**修改活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-17 20:18:16
	 * @param request
	 * @param activityJsonStr
	 * @param signJsonStr
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("edit")
	public RestRespDTO edit(HttpServletRequest request, String activityJsonStr, String signJsonStr) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Activity activity = JSON.parseObject(activityJsonStr, Activity.class);
		// 本期不开启审核
		SignFormDTO signForm = JSON.parseObject(signJsonStr, SignFormDTO.class);
		activityHandleService.edit(activity, signForm, loginUser, request);
		return RestRespDTO.success(activity);
	}

	/**删除活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-19 12:28:19
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("{activityId}/delete")
	public RestRespDTO delele(HttpServletRequest request, @PathVariable Integer activityId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.delete(activityId, loginUser);
		return RestRespDTO.success();
	}

	/**绑定模板
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-13 15:38:14
	 * @param request
	 * @param activityId
	 * @param webTemplateId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("{activityId}/bind/template/{webTemplateId}")
	public RestRespDTO bindWebTemplate(HttpServletRequest request, @PathVariable Integer activityId, @PathVariable Integer webTemplateId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer pageId = activityHandleService.bindWebTemplate(activityId, webTemplateId, loginUser);
		return RestRespDTO.success(pageId);
	}

	/**可参与的活动列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-13 09:58:40
	 * @param request
	 * @param activityQuery
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("list/participate")
	public RestRespDTO list(HttpServletRequest request, ActivityQueryDTO activityQuery) {
		List<Integer> fids = activityQuery.getFids();
		if (CollectionUtils.isNotEmpty(fids)) {
			activityQuery.setFids(fids);
		} else {
			LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
			activityQuery.setFid(loginUser.getFid());
		}
		Page<Activity> page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.listParticipate(page, activityQuery);
		return RestRespDTO.success(page);
	}

	/**查询管理的活动列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-18 14:00:13
	 * @param request
	 * @param activityManageQuery
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("list/managing")
	public RestRespDTO listManaging(HttpServletRequest request, ActivityManageQueryDTO activityManageQuery) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityManageQuery.setCreateUid(loginUser.getUid());
		activityManageQuery.setCreateFid(loginUser.getFid());
		Page<Activity> page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.listManaging(page, activityManageQuery);
		return RestRespDTO.success(page);
	}

	/**发布活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-20 11:04:53
	 * @param request
	 * @param activityId
	 * @param fids
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("{activityId}/release")
	public RestRespDTO release(HttpServletRequest request, @PathVariable Integer activityId, @RequestParam("fids[]") List<Integer> fids) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.release(activityId, fids, loginUser);
		return RestRespDTO.success();
	}

	/**取消发布
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-20 11:06:19
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("{activityId}/release/cancel")
	public RestRespDTO cancelRelease(HttpServletRequest request, @PathVariable Integer activityId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.cancelRelease(activityId, loginUser);
		return RestRespDTO.success();
	}

	/**更新发布范围
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-20 11:07:07
	 * @param request
	 * @param activityId
	 * @param fids
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("{activityId}/release/update")
	public RestRespDTO updateReleaseScope(HttpServletRequest request, @PathVariable Integer activityId, @RequestParam("fids[]") List<Integer> fids) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.updateReleaseScope(activityId, fids, loginUser);
		return RestRespDTO.success();
	}

}