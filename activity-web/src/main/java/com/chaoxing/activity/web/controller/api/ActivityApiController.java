package com.chaoxing.activity.web.controller.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityDetail;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.collection.ActivityCollectionHandleService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.util.exception.BusinessException;
import com.chaoxing.activity.web.util.LoginUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**活动api服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityApiController
 * @description
 * @blame wwb
 * @date 2020-11-11 10:54:37
 */
@CrossOrigin
@RestController
@RequestMapping("api/activity")
public class ActivityApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private WfwAreaApiService wfwAreaApiService;
	@Resource
	private ActivityCollectionHandleService activityCollectionHandleService;

	/**可参与的活动列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-13 09:58:40
	 * @param request
	 * @param data
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("list/participate")
	public RestRespDTO list(HttpServletRequest request, String data) {
		ActivityQueryDTO activityQuery = JSON.parseObject(data, ActivityQueryDTO.class);
		String areaCode = activityQuery.getAreaCode();
		List<WfwAreaDTO> wfwRegionalArchitectures;
		Integer topFid = activityQuery.getTopFid();
		if (StringUtils.isNotBlank(areaCode)) {
			// 区域的
			wfwRegionalArchitectures = wfwAreaApiService.listByCode(areaCode);
		} else {
			if (topFid == null) {
				topFid = Optional.ofNullable(LoginUtils.getLoginUser(request)).map(LoginUserDTO::getFid).orElse(null);
			}
			wfwRegionalArchitectures = wfwAreaApiService.listByFid(topFid);
		}
		// 区域code不存在，且查询范围为1:所有，直接查询
		if (StringUtils.isBlank(areaCode) && Objects.equals(activityQuery.getScope(), 1)) {
			Page<Activity> page = HttpServletRequestUtils.buid(request);
			page = activityQueryService.pageFlag(page, activityQuery);
			return RestRespDTO.success(page);
		}
		List<Integer> fids = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwAreaDTO::getFid).collect(Collectors.toList());
			fids.addAll(subFids);
		} else {
			fids.add(topFid);
		}
		activityQuery.setFids(fids);
		Page<Activity> page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.listParticipate(page, activityQuery);
		return RestRespDTO.success(page);
	}

	/**
	* @Description 
	* @author huxiaolong
	* @Date 2021-09-03 15:47:34
	* @param request
	* @param data
	* @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("list/erdos/participate")
	public RestRespDTO erdosParticipateActivities(HttpServletRequest request, String data) {
		ActivityQueryDTO activityQuery = JSON.parseObject(data, ActivityQueryDTO.class);
		List<Integer> fids = new ArrayList<>();
		if (StringUtils.isNotBlank(activityQuery.getFlag())) {
			activityQuery.setFlags(Arrays.asList(activityQuery.getFlag().split(",")));
		}
		// 获取区域机构
		Integer topFid = activityQuery.getTopFid();
		if (topFid == null) {
			LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
			topFid = loginUser.getFid();
		}

		List<WfwAreaDTO> wfwRegionalArchitectures = wfwAreaApiService.listByFid(topFid);
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwAreaDTO::getFid).collect(Collectors.toList());
			fids.addAll(subFids);
		} else {
			fids.add(topFid);
		}
		activityQuery.setFids(fids);

		Page<Activity> page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.pageErdosParticipate(page, activityQuery);
		return RestRespDTO.success(page);
	}

	/**根据pageId获取活动的经纬度
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-21 23:47:50
	 * @param pageId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("address")
	public RestRespDTO address(Integer pageId) {
		Activity activity = activityQueryService.getByPageId(pageId);
		Optional.ofNullable(activity).orElseThrow(() -> new BusinessException("活动不存在"));
		return RestRespDTO.success(activity);
	}

	/**分页查询报名的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-27 19:34:56
	 * @param request
	 * @param sw
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("signed-up")
	public RestRespDTO pageSignedUp(HttpServletRequest request, String sw, String flag) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Page page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.pageSignedUp(page, loginUser, sw, flag, 0);
		return RestRespDTO.success(page);
	}

	/**分页查询收藏的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-27 20:45:13
	 * @param request
	 * @param sw
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("collected")
	public RestRespDTO pageCollected(HttpServletRequest request, String sw, String flag) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Page page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.pageCollected(page, loginUser, sw, flag);
		return RestRespDTO.success(page);
	}

	/**取消收藏
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-28 15:31:21
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("{activityId}/cancel-collect")
	public RestRespDTO cancelCollect(HttpServletRequest request, @PathVariable Integer activityId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		activityCollectionHandleService.cancelCollect(activityId, loginUser.getUid());
		return RestRespDTO.success();
	}

	/**获取活动简介
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-25 20:58:43
	 * @param websiteId
	 * @param pageId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("introduction")
	public RestRespDTO getActivityIntroduction(Integer websiteId, Integer pageId) {
		Activity activity = null;
		String introduction = "";
		if (pageId != null) {
			activity = activityQueryService.getByPageId(pageId);
		} else if (websiteId != null){
			activity = activityQueryService.getByWebsiteId(websiteId);
		}
		if (activity != null) {
			ActivityDetail activityDetail = activityQueryService.getDetailByActivityId(activity.getId());
			introduction = Optional.ofNullable(activityDetail).map(ActivityDetail::getIntroduction).orElse("");
		}
		return RestRespDTO.success(introduction);
	}

}