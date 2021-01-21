package com.chaoxing.activity.web.controller.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.web.util.LoginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
	private ActivityQueryService activityQueryService;
	@Resource
	private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;

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
		List<Integer> fids = new ArrayList<>();
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures;
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer topFid = activityQuery.getTopFid();
		if (StringUtils.isNotBlank(areaCode)) {
			wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByCode(areaCode);
		} else {
			Integer fid = loginUser.getFid();
			if (!Objects.equals(topFid, fid)) {
				wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByFid(topFid);
			} else {
				wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByFid(fid);
			}
		}
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwRegionalArchitectureDTO::getFid).collect(Collectors.toList());
			fids.addAll(subFids);
		} else {
			fids.add(topFid);
		}
		activityQuery.setFids(fids);
		Page<Activity> page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.listParticipate(page, activityQuery);
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
		// 没有经纬度则设置一个默认的
		BigDecimal longitude = Optional.ofNullable(activity.getLongitude()).orElse(CommonConstant.DEFAULT_LONGITUDE);
		BigDecimal dimension = Optional.ofNullable(activity.getDimension()).orElse(CommonConstant.DEFAULT_DIMENSION);
		activity.setLongitude(longitude);
		activity.setDimension(dimension);
		return RestRespDTO.success(activity);
	}

}