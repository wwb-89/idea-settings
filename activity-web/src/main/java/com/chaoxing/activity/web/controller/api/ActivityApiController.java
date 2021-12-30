package com.chaoxing.activity.web.controller.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityDetail;
import com.chaoxing.activity.model.GroupRegionFilter;
import com.chaoxing.activity.service.GroupRegionFilterService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.collection.ActivityCollectionHandleService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.util.constant.DomainConstant;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	@Resource
	private SignApiService signApiService;
	@Resource
	private GroupRegionFilterService groupRegionFilterService;

	/**加载预告的活动列表
	 * @Description
	 * @author huxiaolong
	 * @Date 2020-11-25 15:58:40
	 * @param request
	 * @param data
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@Deprecated
	@RequestMapping("list/forecast/activities")
	public RestRespDTO listForecastActivities(HttpServletRequest request, String data) {
		return RestRespDTO.success(Lists.newArrayList());
	}

	/**分页查询可参与的活动列表
	 * keepOldRule时，沿用旧的规则查询已发布、进行中、已结束的活动
	 * 反之仅查询进行中、已结束的活动
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
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Page<Activity> page = HttpServletRequestUtils.buid(request);
		page = pageActivities(page, activityQuery, loginUser);
		return RestRespDTO.success(page);
	}

	/**加载预告的活动列表
	 * @Description
	 * @author huxiaolong
	 * @Date 2020-11-25 15:58:40
	 * @param activityQuery
	 * @param loginUser
	 */
	private List<Activity> listForecastActivity(ActivityQueryDTO activityQuery, LoginUserDTO loginUser) {
		activityQuery.setStatusList(Lists.newArrayList(2));
		Page<Activity> page = new Page<>(1, Integer.MAX_VALUE);
		page = pageActivities(page, activityQuery, loginUser);
		return Optional.ofNullable(page.getRecords()).map(Lists::newArrayList).orElse(Lists.newArrayList());
	}

	/**分页查询可参与的活动列表
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-29 18:24:33
	 * @param page
	 * @param activityQuery
	 * @param loginUser
	 */
	private Page<Activity> pageActivities(Page<Activity> page, ActivityQueryDTO activityQuery, LoginUserDTO loginUser) {
		String areaCode = activityQuery.getAreaCode();
		activityQuery.setCurrentUid(Optional.ofNullable(loginUser).map(LoginUserDTO::getUid).orElse(null));
		if (Objects.equals(activityQuery.getScope(), 1) || StringUtils.isNotBlank(areaCode)) {
			activityQuery.setMarketId(null);
			String flag = activityQuery.getFlag();
			if (StringUtils.isNotBlank(flag)) {
				if (StringUtils.isNotBlank(areaCode)) {
					activityQuery.setFids(getFidsByAreaCode(activityQuery.getTopFid(), activityQuery.getAreaCode()));
				}
				page = activityQueryService.pageFlag(page, activityQuery);
				return page;
			}
		}
		activityQuery.setFids(getFidsByAreaCode(activityQuery.getTopFid(), activityQuery.getAreaCode()));
		page = activityQueryService.listParticipate(page, activityQuery);
		activityQueryService.fillTagNames(page.getRecords());
		return page;
	}

	private List<Integer> getFidsByAreaCode(Integer topFid, String areaCode) {
		List<WfwAreaDTO> wfwRegionalArchitectures = Lists.newArrayList();
		if (StringUtils.isNotBlank(areaCode)) {
			// 区域的
			wfwRegionalArchitectures = wfwAreaApiService.listByCode(areaCode);
		}
		List<Integer> fids = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwAreaDTO::getFid).collect(Collectors.toList());
			fids.addAll(subFids);
		} else {
			GroupRegionFilter groupRegionFilter = groupRegionFilterService.getByCode(areaCode);
			fids.add(Optional.ofNullable(groupRegionFilter).map(GroupRegionFilter::getManageFid).orElse(topFid));
		}
		return fids;
	}

	/**可参与的活动（鄂尔多斯）
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
		// 获取区域机构
		if (activityQuery.getTopFid() == null) {
			LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
			activityQuery.setTopFid(loginUser.getFid());
		}
		Page<Activity> page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.pageErdosParticipate(page, activityQuery);
		List<Activity> result = Lists.newArrayList();
		if (page.getCurrent() == 1) {
			result.add(erdosDefaultActivity());
		}
		if (CollectionUtils.isNotEmpty(page.getRecords())) {
			result.addAll(page.getRecords());
		}
		page.setRecords(result);
		page.setSize(page.getSize() + 1);
		page.setTotal(page.getTotal() + 1);
		activityQueryService.fillTagNames(page.getRecords());
		return RestRespDTO.success(page);
	}

	/**创建鄂尔多斯默认活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-28 18:31:23
	 * @param
	 * @return com.chaoxing.activity.model.Activity
	 */
	private Activity erdosDefaultActivity() {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return Activity.builder()
				.name("学生测评积分活动")
				.organisers("鄂尔多斯市教育局")
				.startTime(LocalDateTime.parse("2021-05-31 00:00", dateTimeFormatter))
				.endTime(LocalDateTime.parse("2022-07-01 23:59", dateTimeFormatter))
				.status(3)
				.coverCloudId("c9f16401f786b2357e98a6f37b13830e")
				.coverUrl(DomainConstant.CLOUD_RESOURCE + "/star3/origin/c9f16401f786b2357e98a6f37b13830e.png")
				.previewUrl("https://tsjy.chaoxing.com/plaza/x?courseId=218798245&classId=42416979")
				.build();
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
	 * @param flag	活动标识
	 * @param loadWaitAudit	是否直接加载待审核报名
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@LoginRequired
	@RequestMapping("signed-up")
	public RestRespDTO pageSignedUp(HttpServletRequest request, String sw, String flag, Boolean loadWaitAudit) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Page page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.pageSignedUp(page, loginUser, sw, flag, loadWaitAudit);
		activityQueryService.fillTagNames(page.getRecords());
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
		activityQueryService.fillTagNames(page.getRecords());
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
		if (StringUtils.isBlank(introduction)) {
			introduction = "<span style='color: rgb(165, 165, 165);'>暂无介绍</span>";
		}
		return RestRespDTO.success(introduction);
	}

}