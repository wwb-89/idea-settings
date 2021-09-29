package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.module.WorkFormDTO;
import com.chaoxing.activity.dto.work.WorkBtnDTO;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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

	/** 作品征集接口域名 */
	private static final String DOMAIN = "http://api.reading.chaoxing.com";
	/** 创建作品征集地址 */
	private static final String CREATE_URL = DOMAIN + "/activity/engine/create";
	/** 清空活动发布范围 */
	private static final String CLEAR_ACTIVITY_PARTICIPATE_SCOPE_URL = DOMAIN + "/cache/activity/clear/participate-fid";
	/** 统计活动提交作品数量url */
	private static final String ACTIVITY_SUBMITED_WORK_NUM_URL = DOMAIN + "/activity/stat/submited-work-num";
	/** 更新作品征集信息url */
	private static final String UPDATE_WORK_URL = DOMAIN + "/activity/update";
	/** 作品征集按钮yrl（鄂尔多斯定制） */
	private static final String ERDOS_WORK_BTN_URL = DOMAIN + "/activity/user/permission?activityId=%d&uid=%s&fid=%d";
	private static final String WORK_BTN_URL = DOMAIN + "/activity/user/custom/permission?activityId=%d&uid=%s&fid=%d";

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
			String message = jsonObject.getString("message");
			if (StringUtils.isBlank(message)) {
				message = "刷新作品征集活动参与fid缓存失败";
			}
			throw new BusinessException(message);
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

	/**更新作品征集信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-09-13 15:39:33
	 * @param workId
	 * @param name
	 * @param startTime
	 * @param endTime
	 * @param uid
	 * @return void
	*/
	public void updateWorkInfo(Integer workId, String name, LocalDateTime startTime, LocalDateTime endTime, Integer uid) {
		if (workId == null) {
			return;
		}
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("activityId", workId);
		params.add("uid", uid);
		params.add("name", name);
		params.add("startTime", DateUtils.date2Timestamp(startTime));
		params.add("endTime", DateUtils.date2Timestamp(endTime));
		String result = restTemplate.postForObject(UPDATE_WORK_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String message = jsonObject.getString("message");
			log.info("根据作品征集id:{} 更新作品征集信息error:{}", workId, message);
			throw new BusinessException(message);
		}
	}

	/**查询作品征集的按钮列表（鄂尔多斯定制）
	 * @Description 
	 * @author wwb
	 * @Date 2021-09-17 17:14:39
	 * @param workId
	 * @param uid
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.dto.work.WorkBtnDTO>
	*/
	public List<WorkBtnDTO> listErdosBtns(Integer workId, Integer uid, Integer fid) {
		return listBtns(workId, uid, fid, ERDOS_WORK_BTN_URL);
	}

	/**查询作品征集的按钮列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-09-23 09:58:57
	 * @param workId
	 * @param uid
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.dto.work.WorkBtnDTO>
	*/
	public List<WorkBtnDTO> listBtns(Integer workId, Integer uid, Integer fid) {
		return listBtns(workId, uid, fid, WORK_BTN_URL);
	}

	/**查询作品征集的按钮列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-09-23 10:00:11
	 * @param workId
	 * @param uid
	 * @param fid
	 * @param url
	 * @return java.util.List<com.chaoxing.activity.dto.work.WorkBtnDTO>
	*/
	private List<WorkBtnDTO> listBtns(Integer workId, Integer uid, Integer fid, String url) {
		url = String.format(url, workId, Optional.ofNullable(uid).map(String::valueOf).orElse(""), fid);
		String result = restTemplate.postForObject(url, null, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		if (Objects.equals(true, jsonObject.getBoolean("success"))) {
			return JSON.parseArray(jsonObject.getString("data"), WorkBtnDTO.class);
		} else {
			return Lists.newArrayList();
		}
	}

}