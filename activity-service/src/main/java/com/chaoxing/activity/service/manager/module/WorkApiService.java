package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.module.WorkFormDTO;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**作品征集服务
 * @author wwb
 * @version ver 1.0
 * @className WorkApiService
 * @description
 * @blame wwb
 * @date 2020-11-11 10:19:32
 */
@Slf4j
@Service
public class WorkApiService {

	/** 创建作品征集地址 */
	private static final String CREATE_URL = "http://api.reading.chaoxing.com/activity/engine/create";
	/** 清空活动发布范围 */
	private static final String CLEAR_ACTIVITY_PARTICIPATE_SCOPE_URL = "http://api.reading.chaoxing.com/cache/activity/clear/participate-fid";
	/** 统计活动提交作品数量url */
	private static final String ACTIVITY_SUBMITED_WORK_NUM_URL = "http://api.reading.chaoxing.com/activity/stat/submited-work-num";

	@Resource
	private RestTemplate restTemplate;

	/**创建作品征集活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 10:34:22
	 * @param workForm
	 * @return java.lang.Integer
	*/
	public Integer create(WorkFormDTO workForm) {
		MultiValueMap<String, Object> map= new LinkedMultiValueMap();
		map.add("activityName", workForm.getName());
		map.add("wfwfid", workForm.getWfwfid());
		map.add("uid", workForm.getUid());
		map.add("startTime", workForm.getStartTime());
		map.add("endTime", workForm.getEndTime());
		map.add("edition", "v2");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(map, httpHeaders);
		String result = restTemplate.postForObject(CREATE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			Integer workActivityId = jsonObject.getInteger("data");
			return workActivityId;
		} else {
			String message = jsonObject.getString("message");
			log.error("创建作品征集error:{}", message);
			throw new BusinessException(message);
		}
	}

	/**创建一个默认的作品征集
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-05 16:37:51
	 * @param uid
	 * @param fid
	 * @return java.lang.Integer
	*/
	public Integer createDefault(Integer uid, Integer fid) {
		WorkFormDTO workFormDto = WorkFormDTO.buildDefault(uid, fid);
		return create(workFormDto);
	}

	/**清空活动发布范围缓存
	 * @Description
	 * @author wwb
	 * @Date 2020-09-16 14:42:59
	 * @param activityIds
	 * @return void
	 */
	public void clearActivityParticipateScopeCache(List<Integer> activityIds) {
		if (CollectionUtils.isEmpty(activityIds)) {
			return;
		}
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("ids", String.join(CommonConstant.DEFAULT_SEPARATOR, activityIds.stream().map(v -> String.valueOf(v)).collect(Collectors.toList())));
		String result = restTemplate.postForObject(CLEAR_ACTIVITY_PARTICIPATE_SCOPE_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		boolean success = jsonObject.getBooleanValue("success");
		if (!success) {
			String errorMessage = jsonObject.getString("message");
			if (StringUtils.isBlank(errorMessage)) {
				errorMessage = "刷新作品征集活动参与fid缓存失败";
			}
			throw new BusinessException(errorMessage);
		}
	}

	/**统计活动提交作品量
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-13 19:53:30
	 * @param activityIds
	 * @return java.lang.Integer
	*/
	public Integer countActivitySubmitWorkNum(List<Integer> activityIds) {
		if (CollectionUtils.isNotEmpty(activityIds)) {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(activityIds), httpHeaders);
			String result = restTemplate.postForObject(ACTIVITY_SUBMITED_WORK_NUM_URL, httpEntity, String.class);
			JSONObject jsonObject = JSON.parseObject(result);
			Boolean success = jsonObject.getBoolean("success");
			success = Optional.ofNullable(success).orElse(Boolean.FALSE);
			if (success) {
				return jsonObject.getInteger("data");
			}
		}
		return 0;
	}

}