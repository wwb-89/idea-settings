package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.sign.SignParticipationDTO;
import com.chaoxing.activity.dto.module.SignAddEditDTO;
import com.chaoxing.activity.dto.sign.SignActivityManageIndexDTO;
import com.chaoxing.activity.util.RestTemplateUtils;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**报名签到服务
 * @author wwb
 * @version ver 1.0
 * @className SignApiService
 * @description
 * @blame wwb
 * @date 2020-11-11 10:35:53
 */
@Slf4j
@Service
public class SignApiService {

	private static final String DOMAIN = "http://api.qd.reading.chaoxing.com";
	/** 创建签到报名的地址 */
	private static final String CREATE_URL = DOMAIN + "/sign/new";
	/** 修改签到报名的地址 */
	private static final String UPDATE_URL = DOMAIN + "/sign/update";
	/** 获取签到报名信息的地址 */
	private static final String DETAIL_URL = DOMAIN + "/sign/%d/detail";
	/** 参与情况 */
	private static final String PARTICIPATION_URL = DOMAIN + "/sign/%d/participation";
	/** 统计报名签到在活动管理首页需要的信息 */
	private static final String STAT_SIGN_ACTIVITY_MANAGE_INDEX_URL = DOMAIN + "/sign/%d/stat/activity-index";

	@Resource
	private RestTemplate restTemplate;

	/**创建报名签到
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 14:26:33
	 * @param signAddEdit
	 * @param request
	 * @return java.lang.Integer 签到报名id
	*/
	public Integer create(SignAddEditDTO signAddEdit, HttpServletRequest request) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		List<String> cookies = RestTemplateUtils.getCookies(request);
		httpHeaders.put("Cookie", cookies);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signAddEdit), httpHeaders);
		String result = restTemplate.postForObject(CREATE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return jsonObject.getInteger("data");
		} else {
			String errorMessage = jsonObject.getString("message");
			log.error("创建报名报名:{}失败:{}", JSON.toJSONString(signAddEdit), errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**更新报名签到
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 17:58:39
	 * @param signAddEdit
	 * @return void
	*/
	public void update(SignAddEditDTO signAddEdit, HttpServletRequest request) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		List<String> cookies = RestTemplateUtils.getCookies(request);
		httpHeaders.put("Cookie", cookies);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signAddEdit), httpHeaders);
		String result = restTemplate.postForObject(UPDATE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String errorMessage = jsonObject.getString("message");
			log.error("修改签到报名:{}失败:{}", JSON.toJSONString(signAddEdit), errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**获取签到报名信息
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-19 13:15:47
	 * @param signId
	 * @return com.chaoxing.activity.dto.module.SignAddEditDTO
	*/
	public SignAddEditDTO getById(Integer signId) {
		String url = String.format(DETAIL_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			String data = jsonObject.getString("data");
			SignAddEditDTO signAddEdit = JSON.parseObject(data, SignAddEditDTO.class);
			return signAddEdit;
		} else {
			String errorMessage = jsonObject.getString("message");
			log.error("根据签到报名id:{}查询签到报名失败:{}", signId, errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**查询签到活动的参与情况
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 20:15:35
	 * @param signActivityId
	 * @return com.chaoxing.activity.dto.manager.SignParticipationDTO
	*/
	public SignParticipationDTO getSignParticipation(Integer signActivityId) {
		Integer limitNum = 0;
		Integer signedNum = 0;
		if (signActivityId != null) {
			String url = String.format(PARTICIPATION_URL, signActivityId);
			String result = restTemplate.getForObject(url, String.class);
			JSONObject jsonObject = JSON.parseObject(result);
			Boolean success = jsonObject.getBoolean("success");
			success = Optional.ofNullable(success).orElse(Boolean.FALSE);
			if (success) {
				JSONObject data = jsonObject.getJSONObject("data");
				limitNum = data.getInteger("limitNum");
				signedNum = data.getInteger("signedNum");
			}
		}
		return SignParticipationDTO.builder()
				.limitNum(limitNum)
				.signedNum(signedNum)
				.build();
	}

	/**统计报名签到在活动管理首页需要的信息
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-24 11:25:45
	 * @param signId
	 * @return com.chaoxing.activity.dto.sign.SignActivityManageIndexDTO
	*/
	public SignActivityManageIndexDTO statSignActivityManageIndex(Integer signId) {
		if (signId != null) {
			String url = String.format(STAT_SIGN_ACTIVITY_MANAGE_INDEX_URL, signId);
			try {
				String result = restTemplate.getForObject(url, String.class);
				JSONObject jsonObject = JSON.parseObject(result);
				Boolean success = jsonObject.getBoolean("success");
				success = Optional.ofNullable(success).orElse(Boolean.FALSE);
				if (success) {
					return JSON.parseObject(jsonObject.getString("data"), SignActivityManageIndexDTO.class);
				} else {
					String errorMessage = jsonObject.getString("message");
					log.error("根据报名签到id:{}统计报名签到在活动管理首页需要的信息error:{}", signId, errorMessage);
				}
			} catch (RestClientException e) {
				e.printStackTrace();
				log.error("根据报名签到id:{}统计报名签到在活动管理首页需要的信息error:{}", signId, e.getMessage());
			}
		}
		return SignActivityManageIndexDTO.builder()
				.signId(signId)
				.signUpExist(Boolean.FALSE)
				.signUpId(null)
				.signInExist(Boolean.FALSE)
				.signUpNum(0)
				.build();
	}

}