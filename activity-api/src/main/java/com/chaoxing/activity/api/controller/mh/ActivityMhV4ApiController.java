package com.chaoxing.activity.api.controller.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.ActivityStatusUtils;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**门户数据源第四版
 * @author wwb
 * @version ver 1.0
 * @className ActivityMhV4ApiController
 * @description
 * @blame wwb
 * @date 2022-04-07 14:55:34
 */
@RestController
@RequestMapping("mh/v4")
@CrossOrigin
public class ActivityMhV4ApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private SignApiService signApiService;

	@RequestMapping("activity/brief/info")
	public RestRespDTO briefInfo(@RequestBody String data) {
		JSONObject params = JSON.parseObject(data);
		Integer websiteId = params.getInteger("websiteId");
		// 根据websiteId查询活动id
		Activity activity = activityQueryService.getByWebsiteId(websiteId);
		activityQueryService.fillTagNames(Lists.newArrayList(activity));
		MhGeneralAppResultDataDTO mainFields = MhGeneralAppResultDataDTO.buildDefault();
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
		mainFields.setFields(fields);
		JSONObject jsonObject = new JSONObject();
		if (activity == null) {
			jsonObject.put("results", mainFields);
			return RestRespDTO.success(jsonObject);
		}
		SignStatDTO signStat = signApiService.getSignParticipation(activity.getSignId());
		// 封面
		buildEmptyField(0, fields);
		// 状态
		buildStatusValue(activity, signStat, fields);
		// 标题
		buildField(3, "", activity.getName(), "", fields);
		// 标签
		buildField(4, "", getTagValue(activity), "", fields);
		// 数值
		buildEmptyField(5, fields);
		// 活动时间范围字符串
		buildField(6, "", getActivityTimeScope(activity), "", fields);
		// 地点
		buildAddress(activity, fields);
		// 报名人数
		buildSignUpNum(signStat, fields);
		jsonObject.put("results", Lists.newArrayList(mainFields));
		return RestRespDTO.success(jsonObject);
	}

	private void buildStatusValue(Activity activity, SignStatDTO signStat, List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields) {
		ActivityStatusUtils.StatusEnum status = ActivityStatusUtils.calActivityShowStatus(activity.getStartTime(), activity.getEndTime(), signStat.getSignUpStartTime(), signStat.getSignUpEndTime());
		String statusValue = status.getName();
		String statusColorCss = "";
		switch (status) {
			case SIGN_UP_ONGOING:
				statusColorCss = "linear-gradient(270deg, #24BB1D 0%, #48DF4E 100%);";
				break;
			case ABOUT_TO_START:
				statusColorCss = "linear-gradient(96.68deg, rgba(255, 183, 99, 0.9) 1.14%, rgba(255, 139, 32, 0.9) 98.13%);";
				break;
			case ONGOING:
				statusColorCss = "linear-gradient(90deg, #02C4FF 0%, #43A3FF 100%);";
				break;
			case ENDED:
				statusColorCss = "linear-gradient(90deg, #ACB8BF 4.35%, #7F8E98 100%);";
				break;
			default:

		}
		// 状态
		buildField(1, "", statusValue, "", fields);
		// 按钮颜色
		buildField(2, "", statusColorCss, "", fields);
	}

	private void buildAddress(Activity activity, List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields) {
		String address = "";
		String addressUrl = "";
		BigDecimal longitude = activity.getLongitude();
		BigDecimal dimension = activity.getDimension();
		if (longitude != null && dimension != null) {
			address = Optional.ofNullable(activity.getAddress()).filter(StringUtils::isNotBlank).orElse("");
			address += Optional.ofNullable(activity.getDetailAddress()).filter(StringUtils::isNotBlank).orElse("");
			addressUrl = DomainConstant.API + "/redirect/activity/" + activity.getId() + "/address";
		}
		buildField(100, "", address, addressUrl, fields);
	}

	private void buildSignUpNum(SignStatDTO signStat, List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields) {
		// 报名参与情况
		String signedUpNumDescribe = "";
		String signUpListUrl = "";
		if (signStat != null) {
			if (CollectionUtils.isNotEmpty(signStat.getSignUpIds())) {
				signedUpNumDescribe = String.valueOf(signStat.getSignedUpNum());
				if (signStat.getLimitNum() != null && signStat.getLimitNum() > 0) {
					signedUpNumDescribe += "/" + signStat.getLimitNum();
				}
			}
			// 开启了报名名单公开则显示报名人数链接
			Boolean publicList = Optional.ofNullable(signStat.getPublicList()).orElse(false);
			if (publicList) {
				signUpListUrl = signApiService.getSignUpListUrl(signStat.getSignUpIds().get(0));
			}
		}
		// 已报名人数
		buildField(101, "", signedUpNumDescribe, signUpListUrl, fields);
	}

	private String getTagValue(Activity activity) {
		List<String> tagNames = activity.getTagNames();
		StringBuilder tagValue = new StringBuilder();
		for (String tagName : tagNames) {
			tagValue.append("<span class='info-label'>");
			tagValue.append(tagName);
			tagValue.append("</span>");
		}
		return tagValue.toString();
	}

	private String getActivityTimeScope(Activity activity) {
		StringBuilder timeScope = new StringBuilder();
		LocalDateTime startTime = activity.getStartTime();
		LocalDateTime endTime = activity.getEndTime();
		timeScope.append(startTime.format(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM));
		timeScope.append(" ~ ");
		if (endTime.toLocalDate().toEpochDay() - startTime.toLocalDate().toEpochDay() == 0) {
			// 同一天
			timeScope.append(endTime.format(DateTimeFormatterConstant.HH_MM));
			// 星期几
			timeScope.append(" ");
			timeScope.append(DateUtils.calDayOfWeek(startTime));
		} else {
			timeScope.append(endTime.format(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM));
		}
		return timeScope.toString();
	}

	private void buildEmptyField(Integer flag, List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields) {
		buildField(flag, "", "", "", fields);
	}

	private void buildField(Integer flag, String key, String value, String osrUrl, List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields) {
		fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key(key)
				.flag(String.valueOf(flag))
				.value(value)
				.orsUrl(osrUrl)
				.type("3")
				.build());
	}

}