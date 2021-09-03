package com.chaoxing.activity.api.controller.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
import com.chaoxing.activity.dto.manager.sign.UserSignParticipationStatDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.flag.ActivityFlagValidateService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.constant.ActivityMhUrlConstant;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**门户活动接口服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityMhApiController
 * @description
 * @blame wwb
 * @date 2021-03-09 14:57:37
 */
@RestController
@RequestMapping("mh/v2")
public class ActivityMhV2ApiController {

	private static final Integer MULTI_BTN_MAX_FLAG = 115;

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityFlagValidateService activityFlagValidateService;

	/**活动信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-25 16:14:56
	 * @param data
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/info")
	public RestRespDTO activityInfo(@RequestBody(required = false) String data) {
		JSONObject params = JSON.parseObject(data);
		Integer websiteId = params.getInteger("websiteId");
		// 根据websiteId查询活动id
		Activity activity = activityQueryService.getByWebsiteId(websiteId);
		return activityInfo(activity.getId(), data);
	}
	/**活动信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-02 15:14:24
	 * @param activityId
	 * @param data
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/{activityId}/info")
	public RestRespDTO activityInfo(@PathVariable Integer activityId, @RequestBody(required = false) String data) {
		Integer uid = null;
		if (StringUtils.isNotBlank(data)) {
			JSONObject params = JSON.parseObject(data);
			uid = params.getInteger("uid");
		}
		Activity activity = activityQueryService.getById(activityId);
		Map<String, String> fieldCodeNameRelation = activityQueryService.getFieldCodeNameRelation(activity);
		JSONObject jsonObject = new JSONObject();
		MhGeneralAppResultDataDTO mhGeneralAppResultDataDTO = new MhGeneralAppResultDataDTO();
		mhGeneralAppResultDataDTO.setType(3);
		mhGeneralAppResultDataDTO.setOrsUrl("");
		mhGeneralAppResultDataDTO.setPop(0);
		mhGeneralAppResultDataDTO.setPopUrl("");
		jsonObject.put("results", Lists.newArrayList(mhGeneralAppResultDataDTO));
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = Lists.newArrayList();
		// 活动名称
		mhGeneralAppResultDataFields.add(buildField(fieldCodeNameRelation.get("activity_name"), activity.getName(), "1"));
		// 开始时间
		mhGeneralAppResultDataFields.add(buildField(fieldCodeNameRelation.get("activity_time_scope"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getStartTime()), "100"));
		// 结束时间
		mhGeneralAppResultDataFields.add(buildField("活动结束时间", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getEndTime()), "101"));
		// 报名、签到人数
		Integer signId = activity.getSignId();
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> btns;
		List<Integer> availableFlags = Lists.newArrayList(109, 111, 113, 115, 116, 118);
		if (signId != null) {
			SignStatDTO signStat = signApiService.getSignParticipation(signId);
			if (signStat != null && CollectionUtils.isNotEmpty(signStat.getSignUpIds())) {
				// 报名时间
				mhGeneralAppResultDataFields.add(buildField(fieldCodeNameRelation.get("sign_up_time_scope"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(signStat.getSignUpStartTime()), "102"));
				mhGeneralAppResultDataFields.add(buildField("报名结束时间", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(signStat.getSignUpEndTime()), "103"));
				Integer participateNum = signStat.getSignedUpNum();
				String signedUpNumDescribe = String.valueOf(participateNum);
				Integer limitNum = signStat.getLimitNum();
				if (limitNum.intValue() > 0) {
					signedUpNumDescribe += "/" + limitNum;
				}
				mhGeneralAppResultDataFields.add(buildField("报名人数", signedUpNumDescribe, "106"));
				// 开启了报名名单公开则显示报名人数链接
				Boolean publicList = signStat.getPublicList();
				String signUpListUrl = "";
				if (Objects.equals(publicList, Boolean.TRUE)) {
					signUpListUrl = signApiService.getSignUpListUrl(signStat.getSignUpIds().get(0));
				}
				mhGeneralAppResultDataFields.add(buildField("报名人数链接", signUpListUrl, "107"));
			}
			// 通过报名签到获取按钮列表
			btns = packageBtns(activity, signId, uid, availableFlags);
			mhGeneralAppResultDataFields.addAll(btns);
		}else{
			// 是不是管理员
			if (activityValidationService.isManageAble(activity, uid)) {
				mhGeneralAppResultDataFields.addAll(buildBtnField("管理", getFlag(availableFlags), activityQueryService.getActivityManageUrl(activity.getId()), "2"));
			}
			// 评价
			Boolean openRating = activity.getOpenRating();
			openRating = Optional.ofNullable(openRating).orElse(Boolean.FALSE);
			if (openRating) {
				mhGeneralAppResultDataFields.addAll(buildBtnField("评价", getFlag(availableFlags), activityQueryService.getActivityRatingUrl(activity.getId()), "2"));
			}
		}
		// 活动地点
		String activityAddress = Optional.ofNullable(activity.getAddress()).orElse("") + Optional.ofNullable(activity.getDetailAddress()).orElse("");;
		String activityAddressLink = "https://api.hd.chaoxing.com/redirect/activity/"+ activityId +"/address";
		mhGeneralAppResultDataFields.add(buildField("活动地点", activityAddress, "104"));
		// 活动地点链接（线下的活动有）
		mhGeneralAppResultDataFields.add(buildField("活动地点链接", activityAddressLink, "117"));
		// 主办方
		mhGeneralAppResultDataFields.add(buildField(fieldCodeNameRelation.get("activity_organisers"), activity.getOrganisers(), "105"));
		// 海报
		mhGeneralAppResultDataFields.add(buildField("海报", "海报", "130"));
		mhGeneralAppResultDataFields.add(buildField("海报", String.format(ActivityMhUrlConstant.ACTIVITY_POSTERS_URL, activity.getId()), "131"));
		mhGeneralAppResultDataDTO.setFields(mhGeneralAppResultDataFields);
		return RestRespDTO.success(jsonObject);
	}

	/**封装按钮
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-09 18:39:37
	 * @param activity
	 * @param signId
	 * @param uid
	 * @param availableFlags
	 * @return java.util.List<com.chaoxing.activity.dto.mh.MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO>
	*/
	private List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> packageBtns(Activity activity, Integer signId, Integer uid, List<Integer> availableFlags) {
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> result = Lists.newArrayList();
		Integer status = activity.getStatus();
		Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
		boolean activityEnded = Objects.equals(Activity.StatusEnum.ENDED, statusEnum);
		UserSignParticipationStatDTO userSignParticipationStat = signApiService.userParticipationStat(signId, uid);
		if (userSignParticipationStat == null) {
			return result;
		}
		List<Integer> signInIds = userSignParticipationStat.getSignInIds();
		List<Integer> signUpIds = userSignParticipationStat.getSignUpIds();
		Boolean openWork = activity.getOpenWork();
		openWork = Optional.ofNullable(openWork).orElse(Boolean.FALSE);
		Integer workId = activity.getWorkId();
		boolean isManager = activityValidationService.isManageAble(activity, uid);
		// 报名信息
		boolean existSignUp = CollectionUtils.isNotEmpty(signUpIds);
		boolean existSignUpInfo = false;
		if (existSignUp) {
			// 如果开启了学生报名则需要报名（报名任意一个报名）才能看见"进入会场"
			if (activityFlagValidateService.isDualSelect(activity)) {
				// 双选会
				List<SignUpCreateParamDTO> signUps = userSignParticipationStat.getSignUps();
				boolean openedStudengSignUp = false;
				if (CollectionUtils.isNotEmpty(signUps)) {
					for (SignUpCreateParamDTO signUp : signUps) {
						if (!Objects.equals(SignUpCreateParamDTO.CustomSignUpTypeEnum.DUAL_SELECT_COMPANY.getValue(), signUp.getCustomSignUpType())) {
							openedStudengSignUp = true;
							break;
						}
					}
				}
				if (openedStudengSignUp) {
					// 必须要报名
					if (userSignParticipationStat.getSignedUp()) {
						result.addAll(buildBtnField("进入会场", getFlag(availableFlags), getDualSelectIndexUrl(activity), "1"));
					}
				} else {
					result.addAll(buildBtnField("进入会场", getFlag(availableFlags), getDualSelectIndexUrl(activity), "1"));
				}
			}
			if (userSignParticipationStat.getSignedUp()) {
				// 已报名
				if (CollectionUtils.isNotEmpty(signInIds)) {
					result.addAll(buildBtnField("去签到", getFlag(availableFlags), userSignParticipationStat.getSignInUrl(), "1"));
				}
				if (openWork && workId != null && !isManager) {
					result.addAll(buildBtnField("提交作品", getFlag(availableFlags), getWorkIndexUrl(workId), "1"));
				}
				existSignUpInfo = true;
			} else if (userSignParticipationStat.getSignUpAudit()) {
				// 审核中
				result.addAll(buildBtnField("报名审核中", getFlag(availableFlags), "", "0"));
				existSignUpInfo = true;
			} else if (activityEnded && userSignParticipationStat.getSignUpEnded()) {
				// 活动和报名都结束的情况显示活动已结束
				result.addAll(buildBtnField("活动已结束", getFlag(availableFlags), "", "0"));
			} else if (userSignParticipationStat.getSignUpEnded()) {
				result.addAll(buildBtnField("报名已结束", getFlag(availableFlags), "", "0"));
			} else if (userSignParticipationStat.getSignUpNotStart()) {
				result.addAll(buildBtnField("报名未开始", getFlag(availableFlags), "", "0"));
			} else if (!userSignParticipationStat.getInParticipationScope() && uid != null) {
				result.addAll(buildBtnField("不在参与范围内", getFlag(availableFlags), "", "0"));
			} else if (userSignParticipationStat.getNoPlaces()) {
				result.addAll(buildBtnField("名额已满", getFlag(availableFlags), "", "0"));
			} else {
				String showName = "报名参加";
				List<SignUpCreateParamDTO> signUps = userSignParticipationStat.getSignUps();
				if (signUpIds.size() == 1) {
					SignUpCreateParamDTO signUp = signUps.get(0);
					String btnName = signUp.getBtnName();
					if (StringUtils.isNotBlank(btnName)) {
						showName = btnName;
					}
				}
				result.addAll(buildBtnField(showName, getFlag(availableFlags), userSignParticipationStat.getSignUpUrl(), "1"));
			}
		}else {
			if (activityFlagValidateService.isDualSelect(activity)) {
				result.addAll(buildBtnField("进入会场", getFlag(availableFlags), getDualSelectIndexUrl(activity), "1"));
			}
			if (CollectionUtils.isNotEmpty(signInIds)) {
				result.addAll(buildBtnField("去签到", getFlag(availableFlags), userSignParticipationStat.getSignInUrl(), "1"));
			}
			if (openWork && workId != null && !isManager) {
				result.addAll(buildBtnField("提交作品", getFlag(availableFlags), getWorkIndexUrl(workId), "1"));
			}
		}
		// 是不是管理员
		if (isManager) {
			List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> btns = Lists.newArrayList();
			if (openWork && workId != null) {
				btns.addAll(buildBtnField("提交作品", getFlag(availableFlags), getWorkIndexUrl(workId), "1"));
			}
			btns.addAll(buildBtnField("管理", getFlag(availableFlags), activityQueryService.getActivityManageUrl(activity.getId()), "2"));
			for (MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO btn : btns) {
				result.add(btn);
			}
		}
		// 评价
		Boolean openRating = activity.getOpenRating();
		openRating = Optional.ofNullable(openRating).orElse(Boolean.FALSE);
		if (openRating) {
			result.addAll(buildBtnField("评价", getFlag(availableFlags), activityQueryService.getActivityRatingUrl(activity.getId()), "2"));
		}
		if (existSignUpInfo) {
			result.addAll(buildBtnField("报名信息", getFlag(availableFlags), userSignParticipationStat.getSignUpResultUrl(), "2"));
		}
		return result;
	}

	/**获取双选会主页地址
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-02 16:48:20
	 * @param activity
	 * @return java.lang.String
	*/
	private String getDualSelectIndexUrl(Activity activity) {
		return String.format(UrlConstant.DUAL_SELECT_INDEX_URL, activity.getId(), activity.getCreateFid());
	}

	/**获取作品征集主页地址
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-09 15:30:32
	 * @param workId
	 * @return java.lang.String
	*/
	private String getWorkIndexUrl(Integer workId) {
		return String.format(UrlConstant.WORK_INDEX_URL, workId);
	}

	private String getFlag(List<Integer> availableFlags) {
		if (availableFlags.isEmpty()) {
			return "";
		} else {
			Integer flag = availableFlags.get(0);
			availableFlags.remove(0);
			return String.valueOf(flag);
		}
	}

	private MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO buildField(String key, String value, String flag) {
		return MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key(key)
				.value(value)
				.flag(flag)
				.build();
	}
	private List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> buildBtnField(String value, String flag, String url, String type) {
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> result = Lists.newArrayList();
		result.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key(value)
				.value(url)
				.flag(flag)
				.build());
		Integer intFlag = Integer.parseInt(flag);
		if (intFlag.compareTo(MULTI_BTN_MAX_FLAG) < 0) {
			result.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(type)
					.flag(String.valueOf(intFlag + 1))
					.build());
		}
		return result;
	}

}