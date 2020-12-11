package com.chaoxing.activity.api.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.SignParticipationDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.dto.query.MhActivityCalendarQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**门户应用api接口
 * @author wwb
 * @version ver 1.0
 * @className ActivityMhAppApiController
 * @description
 * @blame wwb
 * @date 2020-11-24 17:35:03
 */
@RestController
@RequestMapping("mh")
@CrossOrigin
public class ActivityMhAppApiController {

	/** 签到按钮地址 */
	private static final String QD_BTN_URL = "http://api.qd.reading.chaoxing.com/activity/%d/btn";

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private CloudApiService cloudApiService;
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
		String coverCloudId = activity.getCoverCloudId();
		String coverUrl = cloudApiService.getCloudImgUrl(coverCloudId);
		JSONObject jsonObject = new JSONObject();
		MhGeneralAppResultDataDTO mhGeneralAppResultDataDTO = new MhGeneralAppResultDataDTO();
		mhGeneralAppResultDataDTO.setType(3);
		mhGeneralAppResultDataDTO.setOrsUrl("");
		mhGeneralAppResultDataDTO.setPop(0);
		mhGeneralAppResultDataDTO.setPopUrl("");
		jsonObject.put("results", new ArrayList(){{add(mhGeneralAppResultDataDTO);}});
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = new ArrayList<>();
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
		jsonObject.put("results", new ArrayList(){{add(mhGeneralAppResultDataDTO);}});
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = new ArrayList<>();
		mhGeneralAppResultDataDTO.setFields(mhGeneralAppResultDataFields);
		// 活动名称
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("活动名称")
				.value(activity.getName())
				.flag("1")
				.build());
		// 开始时间
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("时间")
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
				.value(activity.getCreateOrgName())
				.flag("102")
				.build());
		// 主办地点
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("活动地点")
				.value(activity.getAddress())
				.flag("103")
				.build());
		// 报名、签到人数
		SignParticipationDTO signParticipation = signApiService.getSignParticipation(activity.getSignId());
		StringBuilder signPepleNumDescribe = new StringBuilder();
		Integer limitNum = signParticipation.getLimitNum();
		Integer signedNum = signParticipation.getSignedNum();
		signedNum = Optional.ofNullable(signedNum).orElse(0);
		if (signedNum.compareTo(0) > 0) {
			signPepleNumDescribe.append(signedNum);
			if (limitNum != null && limitNum.intValue() > 0) {
				signPepleNumDescribe.append("/");
				signPepleNumDescribe.append(limitNum);
			}
		}
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("参与人数")
				.value(signPepleNumDescribe.toString())
				.flag("104")
				.build());
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
		Boolean enableSign = activity.getEnableSign();
		if (enableSign) {
			// 请求签到报名
			String url = String.format(QD_BTN_URL, activity.getSignId());
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
		} else {
			// 直接返回信息给门户，返回空数据
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("results", new ArrayList<>());
			return RestRespDTO.success(jsonObject);
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
		Integer pageSize = Optional.ofNullable(jsonObject.getInteger("pageSize")).orElse(CommonConstant.DEFAULT_PAGE_SIZE);
		Activity activity = activityQueryService.getById(activityId);
		Integer createFid = activity.getCreateFid();
		// 查询机构下的活动列表
		Page<Activity> page = new Page(pageNum, pageSize);
		page = activityQueryService.listCreated(page, createFid);
		JSONObject result = new JSONObject();
		result.put("curPage", pageNum);
		result.put("totalPages", page.getPages());
		result.put("totalRecords", page.getTotal());
		List<MhGeneralAppResultDataDTO> mhGeneralAppResultDatas = page2MhGeneralAppResultData(page, (record) -> {
			List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = new ArrayList<>();
			// 封面
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(cloudApiService.getCloudImgUrl(record.getCoverCloudId()))
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
	public RestRespDTO activityCalendar(String areaCode, @RequestBody String data) {
		JSONObject jsonObject = JSON.parseObject(data);
		// 获取参数
		Integer wfwfid = jsonObject.getInteger("wfwfid");
		Optional.ofNullable(wfwfid).orElseThrow(() -> new BusinessException("wfwfid不能为空"));
		List<Integer> fids = new ArrayList<>();
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures;
		if (StringUtils.isNotBlank(areaCode)) {
			wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByCode(areaCode);
		} else {
			wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByFid(wfwfid);
		}
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwRegionalArchitectureDTO::getFid).collect(Collectors.toList());
			fids.addAll(subFids);
		} else {
			fids.add(wfwfid);
		}
		Integer pageNum = Optional.ofNullable(jsonObject.getInteger("page")).orElse(CommonConstant.DEFAULT_PAGE_NUM);
		Integer pageSize = Optional.ofNullable(jsonObject.getInteger("pageSize")).orElse(CommonConstant.DEFAULT_PAGE_SIZE);
		Page page = new Page(pageNum, pageSize);
		MhActivityCalendarQueryDTO mhActivityCalendarQuery = MhActivityCalendarQueryDTO.builder()
				.fids(fids)
				.topFid(wfwfid)
				.build();
		page = activityQueryService.listActivityCalendarParticipate(page, mhActivityCalendarQuery);
		JSONObject result = new JSONObject();
		result.put("curPage", pageNum);
		result.put("totalPages", page.getPages());
		result.put("totalRecords", page.getTotal());
		List<MhGeneralAppResultDataDTO> mhGeneralAppResultDatas = page2MhGeneralAppResultData(page, (record) -> {
			List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = new ArrayList<>();
			// 封面
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(cloudApiService.getCloudImgUrl(record.getCoverCloudId()))
					.flag("0")
					.build());
			// 活动名称
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(record.getName())
					.flag("1")
					.build());
			// 作者
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(record.getCreateOrgName())
					.flag("3")
					.build());
			// 地点
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(record.getAddress())
					.flag("100")
					.build());
			// 活动时间
			mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
					.value(DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(record.getStartTime()))
					.flag("6")
					.build());
			return mhGeneralAppResultDataFields;
		});
		result.put("results", mhGeneralAppResultDatas);
		return RestRespDTO.success(result);
	}

	/**活动地址
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-10 18:12:51
	 * @param pageId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/address")
	public RestRespDTO activityAddress(Integer pageId) {
		Activity activity = activityQueryService.getByPageId(pageId);
		return RestRespDTO.success(activity);
	}

}