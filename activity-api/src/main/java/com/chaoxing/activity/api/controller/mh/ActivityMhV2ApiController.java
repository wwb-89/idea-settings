package com.chaoxing.activity.api.controller.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpStatDTO;
import com.chaoxing.activity.dto.manager.sign.UserSignParticipationStatDTO;
import com.chaoxing.activity.dto.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.BaiduMapUtils;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.enums.ActivityTypeEnum;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
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

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private ActivityValidationService activityValidationService;

	@RequestMapping("activity/{activityId}/info")
	public RestRespDTO activityInfo(@PathVariable Integer activityId, @RequestBody String data) {
		Activity activity = activityQueryService.getById(activityId);
		JSONObject jsonObject = new JSONObject();
		MhGeneralAppResultDataDTO mhGeneralAppResultDataDTO = new MhGeneralAppResultDataDTO();
		mhGeneralAppResultDataDTO.setType(3);
		mhGeneralAppResultDataDTO.setOrsUrl("");
		mhGeneralAppResultDataDTO.setPop(0);
		mhGeneralAppResultDataDTO.setPopUrl("");
		jsonObject.put("results", Lists.newArrayList(mhGeneralAppResultDataDTO));
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = Lists.newArrayList();
		mhGeneralAppResultDataDTO.setFields(mhGeneralAppResultDataFields);
		// 活动名称
		mhGeneralAppResultDataFields.add(buildField("活动名称", activity.getName(), "1"));
		// 开始时间
		mhGeneralAppResultDataFields.add(buildField("活动时间", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getStartTime()), "100"));
		// 结束时间
		mhGeneralAppResultDataFields.add(buildField("活动结束时间", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getEndTime()), "101"));
		// 报名、签到人数
		SignUpStatDTO signUpStat = signApiService.getSignParticipation(activity.getSignId());
		if (signUpStat.getId() != null) {
			// 报名时间
			mhGeneralAppResultDataFields.add(buildField("报名时间", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(signUpStat.getSignUpStartTime()), "102"));
			mhGeneralAppResultDataFields.add(buildField("报名结束时间", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(signUpStat.getSignUpEndTime()), "103"));
			StringBuilder signedUpNumDescribe = new StringBuilder();
			Integer limitNum = signUpStat.getLimitNum();
			Integer participateNum = signUpStat.getSignedUpNum();
			if (participateNum.compareTo(0) > 0 || limitNum.intValue() > 0) {
				signedUpNumDescribe.append(participateNum);
				if (limitNum.intValue() > 0) {
					signedUpNumDescribe.append("/");
					signedUpNumDescribe.append(limitNum);
				}
			}
			mhGeneralAppResultDataFields.add(buildField("报名人数", signedUpNumDescribe.toString(), "106"));
			// 开启了报名名单公开则显示报名人数链接
			Boolean publicList = signUpStat.getPublicList();
			String signUpListUrl = "";
			if (Objects.equals(publicList, Boolean.TRUE)) {
				signUpListUrl = signApiService.getSignUpListUrl(signUpStat.getId());
			}
			mhGeneralAppResultDataFields.add(buildField("报名人数链接", signUpListUrl, "107"));
		}
		// 活动地点
		String activityAddress = "";
		String activityAddressLink = "";
		String activityType = activity.getActivityType();
		ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.fromValue(activityType);
		if (ActivityTypeEnum.OFFLINE.equals(activityTypeEnum)) {
			// 线下活动才有活动地点
			activityAddress = Optional.ofNullable(activity.getAddress()).orElse("") + Optional.ofNullable(activity.getDetailAddress()).orElse("");
			activityAddressLink = BaiduMapUtils.generateAddressPcUrl(activity.getLongitude(), activity.getDimension(), activity.getName(), activityAddress);
		}
		mhGeneralAppResultDataFields.add(buildField("活动地点", activityAddress, "104"));
		// 活动地点链接（线下的活动有）
		mhGeneralAppResultDataFields.add(buildField("活动地点链接", activityAddressLink, "117"));
		// 主办方
		mhGeneralAppResultDataFields.add(buildField("主办方", activity.getOrganisers(), "105"));
		mhGeneralAppResultDataFields.add(buildField("活动对象", "", "108"));
		// 通过报名签到获取按钮列表
		JSONObject params = JSON.parseObject(data);
		Integer uid = params.getInteger("uid");
		mhGeneralAppResultDataFields.addAll(packageBtns(activity, activity.getSignId(), uid));
		return RestRespDTO.success(jsonObject);
	}

	/**封装按钮
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-09 18:39:37
	 * @param activity
	 * @param signId
	 * @param uid
	 * @return java.util.List<com.chaoxing.activity.dto.mh.MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO>
	*/
	private List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> packageBtns(Activity activity, Integer signId, Integer uid) {
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> result = Lists.newArrayList();
		Integer status = activity.getStatus();
		Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
		boolean activityEnded = Objects.equals(Activity.StatusEnum.ENDED, statusEnum);
		UserSignParticipationStatDTO userSignParticipationStat = signApiService.userParticipationStat(signId, uid);
		List<Integer> availableFlags = Lists.newArrayList(109, 111, 113, 115, 116, 117);
		if (userSignParticipationStat == null) {
			return result;
		}
		List<Integer> signInIds = userSignParticipationStat.getSignInIds();
		// 报名信息
		Integer signUpId = userSignParticipationStat.getSignUpId();
		if (signUpId != null) {
			if (userSignParticipationStat.getSignedUp()) {
				// 已报名
				if (CollectionUtils.isNotEmpty(signInIds)) {
					result.add(buildBtnField("去签到", getFlag(availableFlags), userSignParticipationStat.getSignInUrl(), "1"));
				}
				result.add(buildBtnField("报名信息", getFlag(availableFlags), userSignParticipationStat.getSignUpResultUrl(), "2"));
			} else if (userSignParticipationStat.getSignUpAudit()) {
				// 审核中
				result.add(buildBtnField("报名审核中", getFlag(availableFlags), "", "0"));
				result.add(buildBtnField("报名信息", getFlag(availableFlags), userSignParticipationStat.getSignUpResultUrl(), "2"));
			} else if (activityEnded && userSignParticipationStat.getSignUpEnded()) {
				// 活动和报名都结束的情况显示活动已结束
				result.add(buildBtnField("活动已结束", getFlag(availableFlags), "", "0"));
			} else if (userSignParticipationStat.getSignUpEnded()) {
				result.add(buildBtnField("报名已结束", getFlag(availableFlags), "", "0"));
			} else if (userSignParticipationStat.getSignUpNotStart()) {
				result.add(buildBtnField("报名未开始", getFlag(availableFlags), "", "0"));
			} else if (userSignParticipationStat.getNoPlaces()) {
				result.add(buildBtnField("名额已满", getFlag(availableFlags), "", "0"));
			} else {
				result.add(buildBtnField("报名参加", getFlag(availableFlags), userSignParticipationStat.getSignUpUrl(), "1"));
			}
		}else {
			if (CollectionUtils.isNotEmpty(signInIds)) {
				result.add(buildBtnField("去签到", getFlag(availableFlags), userSignParticipationStat.getSignInUrl(), "1"));
			}
		}
		// 是不是管理员
		if (activityValidationService.isCreator(activity, uid)) {
			MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO dataField = buildBtnField("管理", getFlag(availableFlags), activityQueryService.getActivityManageUrl(activity.getId()), "2");
			if (signUpId != null || CollectionUtils.isNotEmpty(signInIds)) {
				result.add(1, dataField);
			} else {
				result.add(dataField);
			}
		}
		return result;
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
	private MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO buildBtnField(String value, String flag, String url, String type) {
		return MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.value(value)
				.flag(flag)
				.orsUrl(url)
				.type(type)
				.build();
	}

}