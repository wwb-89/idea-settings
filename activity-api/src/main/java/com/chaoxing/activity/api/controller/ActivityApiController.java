package com.chaoxing.activity.api.controller;

import cn.hutool.http.HtmlUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.api.vo.ActivityStatSummaryVO;
import com.chaoxing.activity.api.vo.UserResultVO;
import com.chaoxing.activity.api.vo.UserSignUpStatusVo;
import com.chaoxing.activity.api.vo.UserStatSummaryVO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.UserResultDTO;
import com.chaoxing.activity.dto.activity.ActivityExternalDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateFromFormParamDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateFromPreachParamDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.query.ActivityCreateParticipateQueryDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.dto.query.UserResultQueryDTO;
import com.chaoxing.activity.dto.query.admin.ActivityStatSummaryQueryDTO;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.GroupService;
import com.chaoxing.activity.service.LoginService;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.collection.ActivityCollectionHandleService;
import com.chaoxing.activity.service.activity.collection.ActivityCollectionQueryService;
import com.chaoxing.activity.service.activity.create.ActivityCreateService;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.manager.wfw.WfwCoordinateApiService;
import com.chaoxing.activity.service.notice.MarketNoticeTemplateService;
import com.chaoxing.activity.service.notice.SystemNoticeTemplateService;
import com.chaoxing.activity.service.queue.activity.WfwFormSyncActivityQueue;
import com.chaoxing.activity.service.queue.activity.handler.WfwFormSyncActivityQueueService;
import com.chaoxing.activity.service.stat.UserStatSummaryQueryService;
import com.chaoxing.activity.service.user.result.UserResultQueryService;
import com.chaoxing.activity.service.util.Model2DtoService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.constant.ActivityMhUrlConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.CookieConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.chaoxing.activity.vo.ActivityVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityCollectionHandleService activityCollectionHandleService;
	@Resource
	private ActivityStatSummaryQueryService activityStatSummaryQueryService;
	@Resource
	private UserStatSummaryQueryService userStatSummaryQueryService;
	@Resource
	private UserResultQueryService userResultQueryService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private ActivityHandleService activityHandleService;
	@Resource
	private PassportApiService passportApiService;
	@Resource
	private WfwFormSyncActivityQueue wfwFormSyncActivityQueue;
	@Resource
	private ActivityCreateService activityCreateService;
	@Resource
	private MarketHandleService marketHandleService;
	@Resource
	private WfwFormSyncActivityQueueService activityFormSyncService;
	@Resource
	private MarketNoticeTemplateService marketNoticeTemplateService;
	@Resource
	private SystemNoticeTemplateService systemNoticeTemplateService;
	@Resource
	private MarketQueryService marketQueryService;

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

	/**根据报名的万能表单id查询相应的活动信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-10-28 12:45:47
	 * @param wfwFormId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("from-wfw-form-id")
	public RestRespDTO getActivityBySignUpWfwFormId(Integer wfwFormId) {
		Integer signId = signApiService.getSignIdByWfwFormId(wfwFormId);
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
		List<Integer> collectedUids = Lists.newArrayList();
		if (activity != null) {
			collectedUids = activityCollectionQueryService.listCollectedUid(activity.getId());
		}
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
		Activity activity = activityValidationService.activityExist(activityId);
		ActivityExternalDTO activityExternal = model2DtoService.activity2Dto(activity);
		Integer companySignUpWfwFormId = signApiService.getActivityCompanySignUpWfwFormId(activity.getSignId());
		activityExternal.setCompanySignUpFormId(companySignUpWfwFormId);
		return RestRespDTO.success(activityExternal);
	}

	/**是不是管理者
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-08 11:37:17
	 * @param signId
	 * @param uid
	 * @param fid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("manager-judge")
	public RestRespDTO isManager(@RequestParam Integer signId, @RequestParam Integer uid, @RequestParam Integer fid) {
		Activity activity = activityQueryService.getBySignId(signId);
		if (activity == null) {
			return RestRespDTO.success(true);
		}
		if (Objects.equals(activity.getCreateFid(), fid)) {
			return RestRespDTO.success(true);
		}
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
		Activity activity = activityQueryService.getByWebsiteId(websiteId);
		Optional.ofNullable(activity).orElseThrow(() -> new BusinessException("活动不存在"));
		ActivityExternalDTO activityExternal = model2DtoService.activity2Dto(activity);
		Integer companySignUpWfwFormId = signApiService.getActivityCompanySignUpWfwFormId(activity.getSignId());
		activityExternal.setCompanySignUpFormId(companySignUpWfwFormId);
		return RestRespDTO.success(activityExternal);
	}

	/**根据fid或marketId查询活动统计接口
	* @Description
	* @author huxiaolong
	* @Date 2021-08-02 14:36:23
	* @param statSummaryQueryItem
	* @return
	*/
	@RequestMapping("stat/summary")
	public RestRespDTO pageActivityStatResult(HttpServletRequest request, ActivityStatSummaryQueryDTO statSummaryQueryItem) {
		Page page = HttpServletRequestUtils.buid(request);
		page = activityStatSummaryQueryService.activityStatSummaryPage(page, statSummaryQueryItem);

		List<ActivityStatSummaryDTO> records = page.getRecords();
		List<ActivityStatSummaryVO> activityStatSummaryVoList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(records)) {
			records.forEach(v -> {
				activityStatSummaryVoList.add(ActivityStatSummaryVO.buildActivityStatSummaryVo(v));
			});
			page.setRecords(activityStatSummaryVoList);
		}
		return RestRespDTO.success(page);
	}

	/**根据活动activityId查询成绩考核接口
	* @Description
	* @author huxiaolong
	* @Date 2021-08-02 14:36:23
	* @param activityId
	* @return
	*/
	@RequestMapping("{activityId}/result")
	public RestRespDTO pageActivityUserResult(HttpServletRequest request, @PathVariable Integer activityId) {
		Page page = HttpServletRequestUtils.buid(request);
		page = userResultQueryService.pageUserResult(page, UserResultQueryDTO.builder().activityId(activityId).build());
		List<UserResultDTO> records = page.getRecords();
		List<UserResultVO> userResultVOList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(records)) {
			records.forEach(v -> {
				userResultVOList.add(UserResultVO.buildUserResult(v));
			});
			page.setRecords(userResultVOList);
		}

		return RestRespDTO.success(page);
	}

	/**根据fid 或 marketId 或者 uid(uids) 查询用户统计结果接口
	* @Description
	* @author huxiaolong
	* @Date 2021-08-02 14:36:23
	* @param fid
	* @param marketId
	* @param uids
	* @return
	*/
	@RequestMapping("user/stat")
	public RestRespDTO pageUserStatResult(HttpServletRequest request, Integer fid, Integer marketId, String uids) {
		Page page = HttpServletRequestUtils.buid(request);
		page = userStatSummaryQueryService.pageUserStatResult(page, fid, marketId, uids);
		List<UserStatSummary> records = page.getRecords();
		List<UserStatSummaryVO> userStatSummaryVOList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(records)) {
			records.forEach(v -> {
				userStatSummaryVOList.add(UserStatSummaryVO.buildUserStatSummaryVO(v));
			});
			page.setRecords(userStatSummaryVOList);
		}
		return RestRespDTO.success(page);
	}

	/**根据活动activityId 和 uid(uids) 查询用户报名情况接口
	* @Description
	* @author huxiaolong
	* @Date 2021-08-02 14:36:23
	* @param activityId
	* @param uids
	* @return
	*/
	@RequestMapping("{activityId}/user/sign-up")
	public RestRespDTO pageActivityUserSignUpResult(@PathVariable Integer activityId, String uids) {
		Activity activity = activityQueryService.getById(activityId);
		List<Integer> uidList = Lists.newArrayList();
		if (StringUtils.isNotBlank(uids)) {
			uidList = Arrays.stream(uids.split(",")).map(Integer::valueOf).collect(Collectors.toList());
		}
		String data = signApiService.listUserSignUpBySignIdUids(activity.getSignId(), uidList);
		return RestRespDTO.success(JSON.parseArray(data, UserSignUpStatusVo.class));
	}
	
	/**宣讲会创建活动
	* @Description 
	* @author huxiaolong
	* @Date 2021-08-12 17:50:53
	* @param activityCreateDto
	* @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("new/with-shared")
	public RestRespDTO newSharedActivity(@RequestBody ActivityCreateFromPreachParamDTO activityCreateDto) {
		Integer uid = activityCreateDto.getUid();
		Optional.ofNullable(uid).orElseThrow(() -> new BusinessException("用户id不能为空"));
		Integer fid = activityCreateDto.getFid();
		PassportUserDTO passportUserDTO = passportApiService.getByUid(uid);
		String orgName = passportApiService.getOrgName(fid);
		LoginUserDTO loginUserDto = LoginUserDTO.buildDefault(Integer.valueOf(uid), passportUserDTO.getRealName(), fid, orgName);
		Activity activity = activityHandleService.newSharedActivity(activityCreateDto, loginUserDto);
		return RestRespDTO.success(activity);
	}

	/**更新部分活动信息
	* @Description 
	* @author huxiaolong
	* @Date 2021-08-20 17:46:27
	* @param activityCreateDTO
	* @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("partial-info/update")
	public RestRespDTO updatePartialActivityInfo(@RequestBody ActivityCreateFromPreachParamDTO activityCreateDTO) {
		PassportUserDTO passportUserDTO = passportApiService.getByUid(activityCreateDTO.getUid());
		LoginUserDTO loginUserDTO = LoginUserDTO.buildDefault(activityCreateDTO.getUid(), passportUserDTO.getRealName(), activityCreateDTO.getFid(), "");
		activityHandleService.updatePartialActivityInfo(activityCreateDTO, loginUserDTO);
		return RestRespDTO.success();
	}
	
	/**删除活动（宣讲会）
	* @Description 
	* @author huxiaolong
	* @Date 2021-08-12 17:50:59
	* @param activityId
	* @param fid
	* @param uid
	* @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{activityId}/delete/with-shared")
	public RestRespDTO sharedNewActivity(@PathVariable Integer activityId, @RequestParam Integer fid, @RequestParam Integer uid) {
		activityHandleService.deleteActivityUnderFid(fid, activityId, uid);
		return RestRespDTO.success();
	}

	/**更新活动发布状态
	* @Description 宣讲会使用
	* @author huxiaolong
	* @Date 2021-08-12 18:04:34
	* @param activityId
	* @param fid
	* @param uid
	* @param released
	* @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{activityId}/update/release-status")
	public RestRespDTO updateActivityReleaseStatus(@PathVariable Integer activityId, @RequestParam Integer fid, @RequestParam Integer uid, boolean released) {
		OperateUserDTO operateUser = OperateUserDTO.build(uid, fid);
		if (released) {
			activityHandleService.releaseOrgActivity(activityId, fid, operateUser);
		} else {
			activityHandleService.cancelReleaseOrgActivity(activityId, fid, operateUser);
		}
		return RestRespDTO.success();
	}

	/** 万能表单数据新增/修改/删除后同步修改活动
	* @Description
	* @author huxiaolong
	* @Date 2021-08-26 16:46:53
	* @param activityFormSyncParam
	* @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("sync/from/wfw-form")
	public RestRespDTO activitySyncOperate(@Valid ActivityCreateFromFormParamDTO activityFormSyncParam) {
		wfwFormSyncActivityQueue.push(activityFormSyncParam);
		return RestRespDTO.success();
	}

	/**根据作品征集id查询活动标识
	 * @Description 
	 * @author wwb
	 * @Date 2021-09-02 11:20:26
	 * @param workId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("flag/from-work-id")
	public RestRespDTO getFlagByWorkId(Integer workId) {
		Activity activity = activityQueryService.getByWorkId(workId);
		return RestRespDTO.success(Optional.ofNullable(activity).map(Activity::getActivityFlag).orElse(""));
	}

	/**活动克隆
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-09-06 18:57:48
	 * @param activityId
	 * @param fid
	 * @param uid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@RequestMapping("{activityId}/clone")
	public RestRespDTO cloneActivityToOrg(@PathVariable Integer activityId, @RequestParam Integer fid, @RequestParam Integer uid) {
		PassportUserDTO passportUserDTO = passportApiService.getByUid(uid);
		List<WfwAreaDTO> releaseScopes = wfwAreaApiService.listByFid(fid);
		WfwAreaDTO wfwArea = Optional.ofNullable(releaseScopes).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(v.getFid(), fid)).findFirst().orElse(new WfwAreaDTO());
		LoginUserDTO loginUser = LoginUserDTO.buildDefault(Integer.valueOf(passportUserDTO.getUid()), passportUserDTO.getRealName(), fid, wfwArea.getName());
		activityHandleService.cloneActivityToOrg(activityId, fid, releaseScopes, loginUser);
		return RestRespDTO.success();
	}

	/**从活动发布平台拷贝活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-09-15 15:51:18
	 * @param activityId
	 * @param flag
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("copy-from-activity-release")
	public RestRespDTO copyActivityReleaseActivity(@RequestParam Integer activityId, @RequestParam String flag) {
		activityCreateService.createFromActivityRelease(activityId, flag);
		return RestRespDTO.success();
	}

	/**门户活动海报地址
	 * @Description
	 * @author wwb
	 * @Date 2021-09-18 11:25:18
	 * @param websiteId
	 * @return java.lang.String
	 */
	@RequestMapping("mh/activity/poster")
	public String mhPosterUrl(@RequestParam Integer websiteId) {
		Activity activity = activityQueryService.getByWebsiteId(websiteId);
		Integer activityId = Optional.ofNullable(activity).map(Activity::getId).orElse(1);
		return "redirect:" + String.format(ActivityMhUrlConstant.ACTIVITY_POSTERS_URL, activityId);
	}

	/**根据市场wfwAppId删除市场，及市场关联的活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-01 16:52:57
	 * @param wfwAppId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@RequestMapping("wfw-app/delete")
	public RestRespDTO marketAppDelete(@RequestParam("wfwAppId") Integer wfwAppId) {
		marketHandleService.deleteByWfwAppId(wfwAppId);
		return RestRespDTO.success();
	}

	/**通用表单配置发布状态更新接口
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-13 01:12:47
	 * @param fid
	 * @param formId
	 * @param uid
	 * @param formUserId
	 * @param marketId
	 * @param flag
	 * @param released
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@RequestMapping("update/release-status/from/wfw-form")
	public RestRespDTO updateReleaseStatusFromWfwForm(Integer fid, Integer formId, Integer uid, Integer formUserId, Integer marketId, String flag, boolean released) {
		activityFormSyncService.syncUpdateReleaseStatus(fid, formId, uid, formUserId, marketId, flag, released);
		return RestRespDTO.success();
	}

	/**根据signId获取通知类型为noticeType的通知模板
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-17 14:27:56
	 * @param marketId
	 * @param noticeType
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@RequestMapping("notice-template")
	public RestRespDTO getNoticeTemplate(Integer marketId, String noticeType) {
		return RestRespDTO.success(marketNoticeTemplateService.getMarketOrSystemNoticeTemplate(marketId, noticeType));
	}

	/**根据signId获取通知模板字段内容信息
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-17 14:27:48
	 * @param signId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@RequestMapping("notice-template/field")
	public RestRespDTO getNoticeTemplate(@RequestParam Integer signId) {
		Activity activity = activityQueryService.getBySignId(signId);
		if (activity == null) {
			return RestRespDTO.success();
		}
		return RestRespDTO.success(systemNoticeTemplateService.buildNoticeField(activity));
	}

	/**
	 * 分页查询机构创建或发布到该机构的活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-12-01 10:39:35
	 * @param request
	 * @param activityQuery
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("create-participate/page")
	public RestRespDTO createParticipateActivityPage(HttpServletRequest request, ActivityCreateParticipateQueryDTO activityQuery) {
		Page page = HttpServletRequestUtils.buid(request);
		page = activityQueryService.createParticipateActivityPage(page, activityQuery);
		List<Activity> activities = page.getRecords();
		if (CollectionUtils.isNotEmpty(activities)) {
			page.setRecords(ActivityVO.activitiesConvert2Vo(activities));
		}
		return RestRespDTO.success(page);
	}

	/**
	 * 活动归档
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-12-01 12:03:11
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("{activityId}/outer/archive")
	public RestRespDTO archiveActivity(@PathVariable Integer activityId) {
		activityHandleService.updateActivityArchive(activityId, true);
		return RestRespDTO.success();
	}
	/**
	 * 活动恢复
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-12-01 12:03:11
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("{activityId}/outer/recovery")
	public RestRespDTO recoveryActivity(@PathVariable Integer activityId) {
		activityHandleService.updateActivityArchive(activityId, false);
		return RestRespDTO.success();
	}

	/**
	 * 活动发布
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-12-01 12:07:32
	 * @param activityId
	 * @param fid
	 * @param uid
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("{activityId}/outer/release")
	public RestRespDTO releaseActivity(@PathVariable Integer activityId, @RequestParam Integer fid, @RequestParam Integer uid) {
		activityHandleService.release(activityId, OperateUserDTO.build(uid, fid));
		return RestRespDTO.success();
	}

	/**厦门研修平台根据活动id查询活动信息
	 * @Description 
	 * @author huxiaolong
	 * @Date 2021-12-09 18:26:04
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("{activityId}/outer/info")
	public RestRespDTO getActivityInfo(@PathVariable Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		ActivityVO activityVO = ActivityVO.activityConvert2Vo(activity);
		String timescope = activity.getStartTime().format(CommonConstant.FULL_TIME_FORMAT) + "-" + activity.getEndTime().format(CommonConstant.FULL_TIME_FORMAT);
		activityVO.setTimeScope(timescope);
		ActivityDetail activityDetail = activityQueryService.getDetailByActivityId(activityId);
		if (activityDetail != null && StringUtils.isNotBlank(activityDetail.getIntroduction())) {
			activityVO.setIntroduction(HtmlUtil.cleanHtmlTag(activityDetail.getIntroduction()));
		}
		return RestRespDTO.success(activityVO);
	}

	/**
	 * 活动下架
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-12-01 12:07:32
	 * @param activityId
	 * @param fid
	 * @param uid
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("{activityId}/outer/cancel-release")
	public RestRespDTO cancelReleaseActivity(@PathVariable Integer activityId, @RequestParam Integer fid, @RequestParam Integer uid) {
		activityHandleService.cancelRelease(activityId, OperateUserDTO.build(uid, fid));
		return RestRespDTO.success();
	}

	/**活动删除接口(外部接口)
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-12-02 15:23:49
	 * @param activityId
	 * @param fid
	 * @param uid
	 * @param marketId
	 * @param flag
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("{activityId}/outer/delete")
	public RestRespDTO deleteActivity(@PathVariable Integer activityId, @RequestParam Integer fid, @RequestParam Integer uid, Integer marketId, String flag) {
		OperateUserDTO operateUser = OperateUserDTO.build(uid, fid);
		if (marketId == null && StringUtils.isNotBlank(flag)) {
			 marketId = marketQueryService.getMarketIdByFlag(fid, flag);
		}
		if (marketId == null) {
			activityHandleService.deleteActivity(activityId, operateUser);
		} else {
			activityHandleService.deleteMarketActivity(activityId, marketId, operateUser);
		}
		return RestRespDTO.success();
	}

}