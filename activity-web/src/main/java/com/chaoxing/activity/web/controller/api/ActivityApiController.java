package com.chaoxing.activity.web.controller.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
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
import com.google.common.collect.Maps;
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
	@RequestMapping("list/forecast/activities")
	public RestRespDTO listForecastActivities(HttpServletRequest request, String data) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		ActivityQueryDTO activityQuery = JSON.parseObject(data, ActivityQueryDTO.class);
		activityQuery.setFids(getFidsByAreaCode(activityQuery.getTopFid(), activityQuery.getCode()));
		activityQuery.setCurrentUid(Optional.ofNullable(loginUser).map(LoginUserDTO::getUid).orElse(null));
		List<Activity> activities = activityQueryService.listAllForecastActivity(activityQuery);
		activityQueryService.fillTagNames(activities);
		return RestRespDTO.success(activities);
	}

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
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		ActivityQueryDTO activityQuery = JSON.parseObject(data, ActivityQueryDTO.class);
		Integer marketId = activityQuery.getMarketId();
		if (marketId == null) {
			String flag = activityQuery.getFlag();
			if (StringUtils.isNotBlank(flag)) {
				Page<Activity> page = HttpServletRequestUtils.buid(request);
				String code = activityQuery.getCode();
				if (StringUtils.isNotBlank(code)) {
					activityQuery.setFids(getFidsByAreaCode(activityQuery.getTopFid(), activityQuery.getCode()));
				}
				page = activityQueryService.pageFlag(page, activityQuery);
				packageActivitySignedStat(page);
				return RestRespDTO.success(page);
			}
		}
		activityQuery.setFids(getFidsByAreaCode(activityQuery.getTopFid(), activityQuery.getCode()));
		activityQuery.setCurrentUid(Optional.ofNullable(loginUser).map(LoginUserDTO::getUid).orElse(null));
		Page<Activity> page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.listParticipate(page, activityQuery);
		packageActivitySignedStat(page);
		activityQueryService.fillTagNames(page.getRecords());
		return RestRespDTO.success(page);
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

	/** 查询并设置活动已报名人数
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-27 15:12:25
	 * @param
	 * @return void
	 */
	private void packageActivitySignedStat(Page<Activity> page) {
		if (CollectionUtils.isEmpty(page.getRecords())) {
			return;
		}
		List<Integer> signIds = page.getRecords().stream().map(Activity::getSignId).filter(Objects::nonNull).collect(Collectors.toList());
		Map<Integer, SignStatDTO> signIdSignStatMap = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(signIds)) {
			List<SignStatDTO> signStats = signApiService.statSignSignUps(signIds);
			signIdSignStatMap = signStats.stream().collect(Collectors.toMap(SignStatDTO::getId, v -> v, (v1, v2) -> v2));
		}
		for (Activity activity : page.getRecords()) {
			// 活动报名签到状态数据
			SignStatDTO signStatItem = Optional.ofNullable(activity.getSignId()).map(signIdSignStatMap::get).orElse(null);
			boolean openSignUp = signStatItem != null && CollectionUtils.isNotEmpty(signStatItem.getSignUpIds());
			activity.setOpenSignUp(openSignUp);
			if (openSignUp) {
				activity.setSignedUpNum(Optional.ofNullable(signStatItem.getSignedUpNum()).orElse(0));
				activity.setPersonLimit(Optional.ofNullable(signStatItem.getLimitNum()).orElse(0));
			}
		}
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
				.coverUrl(DomainConstant.CLOUD_RESOURCE_DOMAIN + "/star3/origin/c9f16401f786b2357e98a6f37b13830e.png")
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
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("signed-up")
	public RestRespDTO pageSignedUp(HttpServletRequest request, String sw, String flag) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Page page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.pageSignedUp(page, loginUser, sw, flag);
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
		return RestRespDTO.success(introduction);
	}

}