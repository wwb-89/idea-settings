package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.activity.ActivityUpdateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.query.ActivityManageQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

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

	/**创建活动
	 * @Description 需要活动对象
	 * @author wwb
	 * @Date 2020-11-13 09:45:31
	 * @param request
	 * @param activityJsonStr
	 * @param participateScopeJsonStr
	 * @param signJsonStr
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@PostMapping("new")
	public RestRespDTO create(HttpServletRequest request, String activityJsonStr, String participateScopeJsonStr, String releaseClassIdJsonStr, String signJsonStr, Boolean isClone) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		ActivityCreateParamDTO activityCreateParamDto = JSON.parseObject(activityJsonStr, ActivityCreateParamDTO.class);
		List<WfwAreaDTO> wfwRegionalArchitectures = StringUtils.isBlank(participateScopeJsonStr) ? null : JSON.parseArray(participateScopeJsonStr, WfwAreaDTO.class);
		List<Integer> releaseClassIds = StringUtils.isBlank(releaseClassIdJsonStr) ? null : JSON.parseArray(releaseClassIdJsonStr, Integer.class);
		SignCreateParamDTO signAddEdit = JSON.parseObject(signJsonStr, SignCreateParamDTO.class);
		isClone = Optional.ofNullable(isClone).orElse(false);
		Integer activityId = activityHandleService.add(activityCreateParamDto, signAddEdit, wfwRegionalArchitectures, releaseClassIds, loginUser, isClone);
		return RestRespDTO.success(activityId);
	}

	/**修改活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-17 20:18:16
	 * @param request
	 * @param activityJsonStr
	 * @param participateScopeJsonStr
	 * @param signJsonStr
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@PostMapping("edit")
	public RestRespDTO edit(HttpServletRequest request, String activityJsonStr, String participateScopeJsonStr, String releaseClassIdJsonStr, String signJsonStr) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		ActivityUpdateParamDTO activityUpdateParamDto = JSON.parseObject(activityJsonStr, ActivityUpdateParamDTO.class);
		List<Integer> releaseClassIds = StringUtils.isBlank(releaseClassIdJsonStr) ? null : JSON.parseArray(releaseClassIdJsonStr, Integer.class);
		List<WfwAreaDTO> wfwRegionalArchitectures = StringUtils.isBlank(participateScopeJsonStr) ? null : JSON.parseArray(participateScopeJsonStr, WfwAreaDTO.class);
		SignCreateParamDTO signAddEdit = JSON.parseObject(signJsonStr, SignCreateParamDTO.class);
		activityHandleService.edit(activityUpdateParamDto, signAddEdit, wfwRegionalArchitectures, releaseClassIds, loginUser);
		return RestRespDTO.success(activityUpdateParamDto);
	}

	/**删除活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-19 12:28:19
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@PostMapping("{activityId}/delete")
	public RestRespDTO delele(HttpServletRequest request, @PathVariable Integer activityId, Integer marketId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		OperateUserDTO operateUser = loginUser.buildOperateUserDTO();
		if (marketId == null) {
			activityHandleService.deleteActivity(activityId, operateUser);
		} else {
			activityHandleService.deleteMarketActivity(activityId, marketId, operateUser);
		}
		return RestRespDTO.success();
	}

	/**查询管理的活动列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-18 14:00:13
	 * @param request
	 * @param activityManageQuery
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("list/managing")
	public RestRespDTO listManaging(HttpServletRequest request, ActivityManageQueryDTO activityManageQuery) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityManageQuery.setTopFid(loginUser.getFid());
		Page<Activity> page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.listManaging(page, activityManageQuery, loginUser);
		return RestRespDTO.success(page);
	}

	/**置顶活动
	* @Description
	* @author huxiaolong
	* @Date 2021-08-10 17:37:11
	* @param activityId
	* @param marketId
	* @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@PostMapping("{activityId}/set-top")
	public RestRespDTO setActivityTop(@PathVariable Integer activityId, @RequestParam("marketId") Integer marketId) {
		activityHandleService.setActivityTop(activityId, marketId);
		return RestRespDTO.success();
	}

	/**取消活动置顶
	* @Description
	* @author huxiaolong
	* @Date 2021-08-10 17:37:11
	* @param activityId
	* @param marketId
	* @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@PostMapping("{activityId}/cancel-top")
	public RestRespDTO cancelActivityTop(@PathVariable Integer activityId, @RequestParam("marketId") Integer marketId) {
		activityHandleService.cancelActivityTop(activityId, marketId);
		return RestRespDTO.success();
	}

	/**发布活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-20 11:04:53
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@PostMapping("{activityId}/release")
	public RestRespDTO marketRelease(HttpServletRequest request, @PathVariable Integer activityId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.release(activityId, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**取消发布（下架）
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-20 11:06:19
	 * @param request
	 * @param activityId 活动id
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@PostMapping("{activityId}/release/cancel")
	public RestRespDTO cancelRelease(HttpServletRequest request, @PathVariable Integer activityId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.cancelRelease(activityId, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**查询活动已报名用户信息
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-29 17:45:17
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@PostMapping("{activityId}/signed-up/users")
	public RestRespDTO signedUpUsers(@PathVariable Integer activityId) {
		return RestRespDTO.success(activityQueryService.listSignedUpUsers(activityId));
	}

}