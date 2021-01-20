package com.chaoxing.activity.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.service.util.Model2DtoService;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.ActivityExternalDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Group;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.WfwCoordinateApiService;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityApiController
 * @description
 * @blame wwb
 * @date 2020-12-02 21:48:13
 */
@RestController
@RequestMapping("activity")
public class ActivityApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;
	@Resource
	private GroupService groupService;
	@Resource
	private Model2DtoService model2DtoService;
	@Resource
	private WfwCoordinateApiService wfwCoordinateApiService;

	/**组活动推荐
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-02 21:49:48
	 * @param request
	 * @param groupCode
	 * @param fid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("group/{groupCode}/{fid}")
	public RestRespDTO groupRecommend(HttpServletRequest request, @PathVariable String groupCode, @PathVariable Integer fid) {
		Group group = groupService.getByCode(groupCode);
		String areaCode = group.getAreaCode();
		return recommend(request, areaCode, fid);
	}

	/**通过坐标查询推荐活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-19 10:57:09
	 * @param request
	 * @param wfwfid
	 * @param longitude
	 * @param latitude
	 * @param areaCode
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("recommend/coordinate")
	public RestRespDTO groupCoordinateRecommend(HttpServletRequest request, @RequestParam Integer wfwfid, BigDecimal longitude, BigDecimal latitude, String areaCode) {
		Integer fid = wfwCoordinateApiService.getCoordinateAffiliationFid(wfwfid, longitude, latitude);
		fid = Optional.ofNullable(fid).orElse(wfwfid);
		return recommend(request, areaCode, fid);
	}

	/**查询推荐活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-19 11:04:55
	 * @param request
	 * @param areaCode
	 * @param fid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	private RestRespDTO recommend(HttpServletRequest request, String areaCode, Integer fid) {
		List<Integer> fids = Lists.newArrayList();
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures = Lists.newArrayList();
		if (StringUtils.isNotBlank(areaCode)) {
			wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByCode(areaCode);
		}
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwRegionalArchitectureDTO::getFid).collect(Collectors.toList());
			fids.addAll(subFids);
		} else {
			fids.add(fid);
		}
		ActivityQueryDTO activityQuery = ActivityQueryDTO.builder()
				.topFid(fid)
				.build();
		activityQuery.setFids(fids);
		Page page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.listParticipate(page, activityQuery);
		List<Activity> records = page.getRecords();
		if (CollectionUtils.isNotEmpty(records)) {
			List<ActivityExternalDTO> activityExternals = model2DtoService.activity2Dto(records);
			page.setRecords(activityExternals);
		}
		return RestRespDTO.success(page);
	}

	/**根据报名签到id查询活动名称
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-30 20:23:24
	 * @param signId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("name")
	public RestRespDTO getActivityName(Integer signId) {
		Activity activity = activityQueryService.getBySignId(signId);
		return RestRespDTO.success(activity.getName());
	}

	/**根据报名签到id查询活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-05 19:11:56
	 * @param signId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("")
	public RestRespDTO getActivityBySignId(Integer signId) {
		Activity activity = activityQueryService.getBySignId(signId);
		return RestRespDTO.success(activity);
	}

}