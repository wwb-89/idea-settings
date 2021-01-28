package com.chaoxing.activity.web.controller.api.outer;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.ActivityExternalDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityCollectionHandleService;
import com.chaoxing.activity.service.activity.ActivityCollectionValidateService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.WfwCoordinateApiService;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import com.chaoxing.activity.service.util.Model2DtoService;
import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
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
@CrossOrigin
@RestController
@RequestMapping("api/outer/activity")
public class ActivityApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;
	@Resource
	private Model2DtoService model2DtoService;
	@Resource
	private WfwCoordinateApiService wfwCoordinateApiService;
	@Resource
	private ActivityCollectionHandleService activityCollectionHandleService;
	@Resource
	private ActivityCollectionValidateService activityCollectionValidateService;

	/**组活动推荐
	 * @Description
	 * @author wwb
	 * @Date 2020-12-02 21:49:48
	 * @param request
	 * @param areaCode
	 * @param fid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@RequestMapping("recommend")
	public RestRespDTO groupRecommend(HttpServletRequest request, String areaCode, @RequestParam Integer fid) {
		return recommend(request, areaCode, fid);
	}

	/**通过坐标查询推荐活动
	 * @Description
	 * @author wwb
	 * @Date 2021-01-19 10:57:09
	 * @param request
	 * @param fid
	 * @param longitude
	 * @param latitude
	 * @param areaCode
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@RequestMapping("recommend/coordinate")
	public RestRespDTO groupCoordinateRecommend(HttpServletRequest request, @RequestParam Integer fid, BigDecimal longitude, BigDecimal latitude, String areaCode) {
		Integer wfwfid = wfwCoordinateApiService.getCoordinateAffiliationFid(fid, longitude, latitude);
		wfwfid = Optional.ofNullable(wfwfid).orElse(fid);
		return recommend(request, areaCode, wfwfid);
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
	private RestRespDTO recommend(HttpServletRequest request, String areaCode, @RequestParam Integer fid) {
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

	/**是否已收藏
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-28 20:47:22
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{activityId}/collected")
	public RestRespDTO isCollected(HttpServletRequest request, @PathVariable Integer activityId) {
		Integer uid = CookieUtils.getUid(request);
		boolean collected = activityCollectionValidateService.isCollected(activityId, uid);
		return RestRespDTO.success(collected);
	}

	/**收藏
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-28 20:28:15
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{activityId}/collect")
	public RestRespDTO collect(HttpServletRequest request, @PathVariable Integer activityId) {
		Integer uid = CookieUtils.getUid(request);
		activityCollectionHandleService.collect(activityId, uid);
		return RestRespDTO.success();
	}

	/**取消收藏
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-28 20:46:18
	 * @param request
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{activityId}/collect/cancel")
	public RestRespDTO cancelCollect(HttpServletRequest request, @PathVariable Integer activityId) {
		Integer uid = CookieUtils.getUid(request);
		activityCollectionHandleService.cancelCollect(activityId, uid);
		return RestRespDTO.success();
	}

}