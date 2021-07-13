package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.activity.ActivityUpdateParamDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.manager.mh.MhCloneResultDTO;
import com.chaoxing.activity.dto.query.ActivityManageQueryDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.PathVariable;
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
	public RestRespDTO create(HttpServletRequest request, String activityJsonStr, String participateScopeJsonStr, String signJsonStr) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		ActivityCreateParamDTO activityCreateParamDto = JSON.parseObject(activityJsonStr, ActivityCreateParamDTO.class);
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures = JSON.parseArray(participateScopeJsonStr, WfwRegionalArchitectureDTO.class);
		SignCreateParamDTO signAddEdit = JSON.parseObject(signJsonStr, SignCreateParamDTO.class);
		Integer activityId = activityHandleService.add(activityCreateParamDto, signAddEdit, wfwRegionalArchitectures, loginUser);
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
	public RestRespDTO edit(HttpServletRequest request, String activityJsonStr, String participateScopeJsonStr, String signJsonStr) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		ActivityUpdateParamDTO activityUpdateParamDto = JSON.parseObject(activityJsonStr, ActivityUpdateParamDTO.class);
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures = JSON.parseArray(participateScopeJsonStr, WfwRegionalArchitectureDTO.class);
		SignCreateParamDTO signAddEdit = JSON.parseObject(signJsonStr, SignCreateParamDTO.class);
		activityHandleService.edit(activityUpdateParamDto, signAddEdit, wfwRegionalArchitectures, loginUser);
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
	public RestRespDTO delele(HttpServletRequest request, @PathVariable Integer activityId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.delete(activityId, loginUser);
		return RestRespDTO.success();
	}

	/**绑定/选择模板
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-13 15:38:14
	 * @param request
	 * @param activityId
	 * @param webTemplateId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@PostMapping("{activityId}/bind/template/{webTemplateId}")
	public RestRespDTO bindWebTemplate(HttpServletRequest request, @PathVariable Integer activityId, @PathVariable Integer webTemplateId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		MhCloneResultDTO mhCloneResult = activityHandleService.bindWebTemplate(activityId, webTemplateId, loginUser);
		return RestRespDTO.success(mhCloneResult);
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
	public RestRespDTO release(HttpServletRequest request, @PathVariable Integer activityId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityHandleService.release(activityId, loginUser);
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
		activityHandleService.cancelRelease(activityId, loginUser);
		return RestRespDTO.success();
	}

}