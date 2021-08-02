package com.chaoxing.activity.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.ActivityExternalDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Group;
import com.chaoxing.activity.model.LoginCustom;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.LoginService;
import com.chaoxing.activity.service.activity.ActivityIsAboutStartHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.collection.ActivityCollectionHandleService;
import com.chaoxing.activity.service.activity.collection.ActivityCollectionQueryService;
import com.chaoxing.activity.service.manager.wfw.WfwCoordinateApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.util.Model2DtoService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.constant.CookieConstant;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

	/** 登录地址占位 */
	private static final String LOGIN_URL_PLACEHOLDER = "url_placeholder";

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private WfwAreaApiService wfwAreaApiService;
	@Resource
	private GroupService groupService;
	@Resource
	private Model2DtoService model2DtoService;
	@Resource
	private WfwCoordinateApiService wfwCoordinateApiService;
	@Resource
	private LoginService loginService;
	@Resource
	private ActivityCollectionQueryService activityCollectionQueryService;
	@Resource
	private ActivityIsAboutStartHandleService activityIsAboutStartHandleService;
	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityCollectionHandleService activityCollectionHandleService;

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
		List<WfwAreaDTO> wfwRegionalArchitectures = Lists.newArrayList();
		if (StringUtils.isNotBlank(areaCode)) {
			wfwRegionalArchitectures = wfwAreaApiService.listByCode(areaCode);
		}
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwAreaDTO::getFid).collect(Collectors.toList());
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

	/**根据报名签到id查询活动定制登录url
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-21 15:10:17
	 * @param signId
	 * @param refer
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("custom-login-url")
	public RestRespDTO getActivityCustomLoginUrl(Integer signId, String refer) throws UnsupportedEncodingException {
		Activity activity = activityQueryService.getBySignId(signId);
		LoginCustom loginCustom = loginService.getLoginCustom(activity);
		String loginUrl = "";
		if (loginCustom != null) {
			loginUrl = loginCustom.getLoginUrl();
			if (StringUtils.isNotBlank(loginUrl) && StringUtils.isNotBlank(refer)) {
				Integer encodeNum = loginCustom.getEncodeNum();
				encodeNum = Optional.ofNullable(encodeNum).orElse(0);
				while (encodeNum-- > 0) {
					refer = URLEncoder.encode(refer, StandardCharsets.UTF_8.name());
				}
			}
		}
		refer = Optional.ofNullable(refer).filter(StringUtils::isNotBlank).orElse("");
		loginUrl = loginUrl.replace(LOGIN_URL_PLACEHOLDER, refer);
		return RestRespDTO.success(loginUrl);
	}

	/**根据报名签到id查询收藏活动的uid列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-03 15:18:12
	 * @param signId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("collected-uid")
	public RestRespDTO listCollectedUidBySignId(Integer signId) {
		Activity activity = activityQueryService.getBySignId(signId);
		List<Integer> collectedUids = activityCollectionQueryService.listCollectedUid(activity.getId());
		return RestRespDTO.success(collectedUids);
	}

	/**查询活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-02 10:29:45
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{activityId}")
	public RestRespDTO getById(@PathVariable Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		ActivityExternalDTO activityExternal = model2DtoService.activity2Dto(activity);
		return RestRespDTO.success(activityExternal);
	}

	/**是不是管理者
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-08 11:37:17
	 * @param signId
	 * @param uid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("manager-judge")
	public RestRespDTO isManager(@RequestParam Integer signId, @RequestParam Integer uid) {
		Activity activity = activityQueryService.getBySignId(signId);
		boolean manager = activityValidationService.isManageAble(activity.getId(), uid);
		return RestRespDTO.success(manager);
	}

	/**机构创建的活动列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-19 10:12:18
	 * @param fid
	 * @param activityFlag
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("org/{fid}/created")
	public RestRespDTO listOrgCreated(@PathVariable Integer fid, @RequestParam(defaultValue = "") String activityFlag) {
		return RestRespDTO.success(activityQueryService.listOrgCreated(fid, activityFlag));
	}

	/**活动参与的用户uid
	 * @Description
	 * @author wwb
	 * @Date 2021-04-19 10:14:24
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{activityId}/participated-uid")
	public RestRespDTO participatedUid(@PathVariable Integer activityId) {
		List<Integer> uids = activityQueryService.listSignedUpUid(activityId);
		return RestRespDTO.success(uids);
	}

	/**收藏活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-13 15:10:26
	 * @param activityId
	 * @param uid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{activityId}/collect")
	public RestRespDTO collect(@PathVariable Integer activityId, @CookieValue(name = CookieConstant.UID) Integer uid) {
		activityCollectionHandleService.collect(activityId, uid);
		return RestRespDTO.success();
	}

	/**取消收藏活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-13 15:12:56
	 * @param activityId
	 * @param uid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{activityId}/collect/cancel")
	public RestRespDTO cancelCollect(@PathVariable Integer activityId, @CookieValue(name = CookieConstant.UID) Integer uid) {
		activityCollectionHandleService.cancelCollect(activityId, uid);
		return RestRespDTO.success();
	}

	/**根据门户pageId查询活动id
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-21 15:46:11
	 * @param pageId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("from/page/{pageId}")
	public RestRespDTO getByPageId(@PathVariable Integer pageId) {
		return RestRespDTO.success(activityQueryService.getByPageId(pageId));
	}

	/**根据门户网站websiteId查询活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-21 16:05:43
	 * @param websiteId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("from/website/{websiteId}")
	public RestRespDTO getByWebsiteId(@PathVariable Integer websiteId) {
		return RestRespDTO.success(activityQueryService.getByWebsiteId(websiteId));
	}

}