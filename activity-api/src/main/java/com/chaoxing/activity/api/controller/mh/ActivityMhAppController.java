package com.chaoxing.activity.api.controller.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.manager.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
import com.chaoxing.activity.dto.query.MhActivityCalendarQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityDetail;
import com.chaoxing.activity.service.activity.ActivityCoverUrlSyncService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.DateFormatConstant;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**门户应用api接口
 * @author wwb
 * @version ver 1.0
 * @className ActivityMhAppController
 * @description
 * @blame wwb
 * @date 2020-11-24 17:35:03
 */
@Slf4j
@RestController
@RequestMapping("mh")
@CrossOrigin
public class ActivityMhAppController {

	/** 签到按钮地址 */
	private static final String QD_BTN_URL = "http://api.qd.reading.chaoxing.com/sign/%d/btn?activityId=%s";
	private static final Integer RECOMMEND_ACTIVITY_PAGE_SIZE = 4;

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityCoverUrlSyncService activityCoverService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;

	@Resource
	private RestTemplate restTemplate;

	/**活动封面
	 * @Description
	 * @author wwb
	 * @Date 2020-11-24 19:02:11
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/{activityId}/cover")
	public RestRespDTO activityCover(@PathVariable Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		String coverUrl = activityCoverService.getCoverUrl(activity);
		JSONObject jsonObject = new JSONObject();
		MhGeneralAppResultDataDTO mhGeneralAppResultDataDTO = new MhGeneralAppResultDataDTO();
		mhGeneralAppResultDataDTO.setType(3);
		mhGeneralAppResultDataDTO.setOrsUrl("");
		mhGeneralAppResultDataDTO.setPop(0);
		mhGeneralAppResultDataDTO.setPopUrl("");
		jsonObject.put("results", Lists.newArrayList(mhGeneralAppResultDataDTO));
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = Lists.newArrayList();
		mhGeneralAppResultDataDTO.setFields(mhGeneralAppResultDataFields);
		MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO mhGeneralAppResultDataField = MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.value(coverUrl)
				.flag("0")
				.build();
		mhGeneralAppResultDataFields.add(mhGeneralAppResultDataField);
		return RestRespDTO.success(jsonObject);
	}
	/**活动信息
	 * @Description
	 * @author wwb
	 * @Date 2020-11-24 19:02:58
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/{activityId}/info")
	public RestRespDTO activityInfo(@PathVariable Integer activityId) {
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
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("活动名称")
				.value(activity.getName())
				.flag("1")
				.build());
		// 开始时间
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("活动时间")
				.value(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getStartTime()))
				.flag("100")
				.build());
		// 结束时间
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("时间")
				.value(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getEndTime()))
				.flag("101")
				.build());
		// 主办单位
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("主办单位")
				.value(activity.getOrganisers())
				.flag("102")
				.build());
		// 主办地点
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("活动地点")
				.value(Optional.ofNullable(activity.getAddress()).orElse("") + Optional.ofNullable(activity.getDetailAddress()).orElse(""))
				.flag("103")
				.build());
		// 报名、签到人数
		SignStatDTO signStat = signApiService.getSignParticipation(activity.getSignId());
		if (signStat != null && CollectionUtils.isNotEmpty(signStat.getSignUpIds())) {
			StringBuilder signUpTimeStringBuilder = new StringBuilder();
			signUpTimeStringBuilder.append(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(signStat.getSignUpStartTime()));
			signUpTimeStringBuilder.append(" ~ ");
			signUpTimeStringBuilder.append(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(signStat.getSignUpEndTime()));
			// 报名时间
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.key("报名时间")
					.value(signUpTimeStringBuilder.toString())
					.flag("105")
					.build());
			StringBuilder signPepleNumDescribe = new StringBuilder();
			Integer limitNum = signStat.getLimitNum();
			Integer participateNum = signStat.getSignedUpNum();
			if (participateNum.compareTo(0) > 0 || limitNum.intValue() > 0) {
				signPepleNumDescribe.append(participateNum);
				if (limitNum.intValue() > 0) {
					signPepleNumDescribe.append("/");
					signPepleNumDescribe.append(limitNum);
				}
			}
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.key("报名人数")
					.value(signPepleNumDescribe.toString())
					.flag("104")
					.build());
		}
		return RestRespDTO.success(jsonObject);
	}

	/**报名签到按钮
	 * @Description
	 * @author wwb
	 * @Date 2020-11-26 17:43:41
	 * @param activityId
	 * @param data
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/{activityId}/sign/btn")
	public RestRespDTO signInUp(@PathVariable Integer activityId, @RequestBody String data) {
		Activity activity = activityQueryService.getById(activityId);
		// 请求签到报名
		String url = String.format(QD_BTN_URL, activity.getSignId(), activityId);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(data, httpHeaders);
		String result = restTemplate.postForObject(url, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(false);
		if (success) {
			return RestRespDTO.success(jsonObject.getJSONObject("data"));
		} else {
			return RestRespDTO.error(jsonObject.getString("message"));
		}
	}

	/**推荐活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-24 21:42:19
	 * @param activityId
	 * @param data
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/{activityId}/recommend")
	public RestRespDTO recommendActivity(@PathVariable Integer activityId, @RequestBody String data) {
		JSONObject jsonObject = JSON.parseObject(data);
		Integer pageNum = Optional.ofNullable(jsonObject.getInteger("page")).orElse(CommonConstant.DEFAULT_PAGE_NUM);
		Integer pageSize = Optional.ofNullable(jsonObject.getInteger("pageSize")).orElse(RECOMMEND_ACTIVITY_PAGE_SIZE);
		Activity activity = activityQueryService.getById(activityId);
		Integer createFid = activity.getCreateFid();
		// 查询机构下的活动列表
		Page<Activity> page = new Page(pageNum, pageSize);
		page = activityQueryService.listOrgParticipatedOrCreated(page, createFid);
		JSONObject result = new JSONObject();
		result.put("curPage", pageNum);
		result.put("totalPages", page.getPages());
		result.put("totalRecords", page.getTotal());
		List<MhGeneralAppResultDataDTO> mhGeneralAppResultDatas = page2MhGeneralAppResultData(page, (record) -> {
			List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = new ArrayList<>();
			// 封面
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(activityCoverService.getCoverUrl(record))
					.flag("0")
					.build());
			// 活动名称
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(record.getName())
					.flag("1")
					.build());
			// 活动时间
			LocalDateTime startTime = record.getStartTime();
			LocalDateTime endTime = record.getEndTime();
			StringBuilder timeStringBuilder = new StringBuilder();
			timeStringBuilder.append(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(startTime));
			timeStringBuilder.append(" ～ ");
			timeStringBuilder.append(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(endTime));
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(timeStringBuilder.toString())
					.flag("6")
					.build());
			return mhGeneralAppResultDataFields;
		});
		result.put("results", mhGeneralAppResultDatas);
		return RestRespDTO.success(result);
	}

	private List<MhGeneralAppResultDataDTO> page2MhGeneralAppResultData(Page<Activity> page, Function<Activity, List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO>> function) {
		List<MhGeneralAppResultDataDTO> mhGeneralAppResultDatas = new ArrayList<>();
		List<Activity> records = page.getRecords();
		if (CollectionUtils.isNotEmpty(records)) {
			for (Activity record : records) {
				MhGeneralAppResultDataDTO mhGeneralAppResultData = new MhGeneralAppResultDataDTO();
				mhGeneralAppResultData.setType(3);
				mhGeneralAppResultData.setOrsUrl(record.getPreviewUrl());
				mhGeneralAppResultData.setPop(0);
				mhGeneralAppResultData.setPopUrl("");
				mhGeneralAppResultData.setFields(function.apply(record));
				mhGeneralAppResultDatas.add(mhGeneralAppResultData);
			}
		}
		return mhGeneralAppResultDatas;
	}

	/**活动日历
	 * @Description areaCode为空走通用流程，不为空走定制流程
	 * @author wwb
	 * @Date 2020-12-03 15:40:03
	 * @param areaCode
	 * @param data
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/calendar")
	public RestRespDTO activityCalendar(String areaCode, @RequestParam(defaultValue = "0") Integer strict, String activityFlag, @RequestBody String data) throws ParseException {
		JSONObject jsonObject = JSON.parseObject(data);
		if (StringUtils.isBlank(areaCode)) {
			areaCode = jsonObject.getString("areaCode");
		}
		// 现在不取areaCode的数据
		areaCode = null;
		// 一个活动是否需要多条数据
		Boolean multiBoolean = jsonObject.getBoolean("multi");
		// 为了兼容原来的样式需要为true
		multiBoolean = Optional.ofNullable(multiBoolean).orElse(true);
		final boolean multi = multiBoolean;
		// 获取参数
		Integer wfwfid = jsonObject.getInteger("wfwfid");
		Optional.ofNullable(wfwfid).orElseThrow(() -> new BusinessException("wfwfid不能为空"));
		List<Integer> fids = Lists.newArrayList();
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures = Lists.newArrayList();
		try {
			if (StringUtils.isNotBlank(areaCode)) {
				wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByCode(areaCode);
			} else {
				wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByFid(wfwfid);
			}
		} catch (Exception e) {
			log.error("活动日历error: {}", e.getMessage());
		}
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwRegionalArchitectureDTO::getFid).collect(Collectors.toList());
			fids.addAll(subFids);
		} else {
			fids.add(wfwfid);
		}
		Integer classifyId = null;
		JSONArray classifies = jsonObject.getJSONArray("classifies");
		if (classifies != null && classifies.size() > 0) {
			JSONObject classify = classifies.getJSONObject(0);
			if (classify != null) {
				classifyId = classify.getInteger("id");
			}
		}
		Integer pageNum = Optional.ofNullable(jsonObject.getInteger("page")).orElse(CommonConstant.DEFAULT_PAGE_NUM);
		Integer pageSize = Optional.ofNullable(jsonObject.getInteger("pageSize")).orElse(CommonConstant.DEFAULT_PAGE_SIZE);
		Page page = new Page(pageNum, pageSize);
		MhActivityCalendarQueryDTO mhActivityCalendarQuery = MhActivityCalendarQueryDTO.builder()
				.fids(fids)
				.topFid(wfwfid)
				.strict(strict)
				.activityFlag(activityFlag)
				.classifyId(classifyId)
				.build();
		String year = jsonObject.getString("year");
		String month = jsonObject.getString("month");
		String date = jsonObject.getString("date");
		if (StringUtils.isNotBlank(year) && StringUtils.isBlank(date)) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, Integer.parseInt(year));
			calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
			mhActivityCalendarQuery.setStartDate(calMonthStartTime(calendar));
			mhActivityCalendarQuery.setEndDate(calMonthEndTime(calendar));
		}
		if (StringUtils.isNotBlank(date)) {
			mhActivityCalendarQuery.setDate(date);
		}
		page = activityQueryService.listActivityCalendar(page, mhActivityCalendarQuery, multiBoolean);
		JSONObject result = new JSONObject();
		result.put("curPage", pageNum);
		result.put("totalPages", page.getPages());
		result.put("totalRecords", page.getTotal());
		List<MhGeneralAppResultDataDTO> mhGeneralAppResultDatas = page2MhGeneralAppResultData(page, (record) -> {
			List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = Lists.newArrayList();
			// 封面
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(activityCoverService.getCoverUrl(record))
					.flag("0")
					.build());
			// 活动名称
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(record.getName())
					.flag("1")
					.build());
			// 活动分类
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(record.getActivityClassifyName())
					.flag("2")
					.build());
			// 作者
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(record.getCreateOrgName())
					.flag("3")
					.build());
			//
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value("")
					.flag("4")
					.build());
			//
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value("")
					.flag("5")
					.build());
			// 活动开始时间
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getStartTime()))
					.flag("6")
					.build());
			// 地点
			if (multi) {
				mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
						.value(Optional.ofNullable(record.getAddress()).orElse("") + Optional.ofNullable(record.getDetailAddress()).orElse(""))
						.flag("100")
						.build());
			}
			// 活动结束时间
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getEndTime()))
					.flag("101")
					.build());
			// 活动状态130
			Integer status = record.getStatus();
			Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
			if (statusEnum != null) {
				String statusDescription = statusEnum.getName();
				if (Objects.equals(Activity.StatusEnum.RELEASED, statusEnum)) {
					statusDescription = "未开始";
				}
				mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
						.value(statusDescription)
						.flag("130")
						.build());
			}
			return mhGeneralAppResultDataFields;
		});
		result.put("results", mhGeneralAppResultDatas);
		return RestRespDTO.success(result);
	}

	private String calMonthStartTime(Calendar calendar) {
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date time = calendar.getTime();
		return DateFormatConstant.YYYYMMDD.format(time);
	}

	private String calMonthEndTime(Calendar calendar) {
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date time = calendar.getTime();
		return DateFormatConstant.YYYYMMDD.format(time);
	}

	/**活动地址(门户地图使用)
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-10 18:12:51
	 * @param pageId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/address")
	public RestRespDTO activityAddress(Integer pageId) {
		Activity activity = activityQueryService.getByPageId(pageId);
		// 没有经纬度则设置一个默认的
		BigDecimal longitude = Optional.ofNullable(activity.getLongitude()).orElse(CommonConstant.DEFAULT_LONGITUDE);
		BigDecimal dimension = Optional.ofNullable(activity.getDimension()).orElse(CommonConstant.DEFAULT_DIMENSION);
		activity.setLongitude(longitude);
		activity.setDimension(dimension);
		return RestRespDTO.success(activity);
	}

	/**获取活动简介
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-18 14:46:28
	 * @param websiteId
	 * @param pageId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/introduction")
	public RestRespDTO activityIntroduction(Integer websiteId, Integer pageId) {
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