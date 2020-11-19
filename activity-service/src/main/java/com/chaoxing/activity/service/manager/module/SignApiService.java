package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.module.SignFormDTO;
import com.chaoxing.activity.util.RestTemplateUtils;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

	private static final String DOMAIN = "http://sign.chaoxing.com";
	/** 创建签到报名的地址 */
	private static final String CREATE_URL = DOMAIN + "/activity/create";
	/** 修改签到报名的地址 */
	private static final String UPDATE_URL = DOMAIN + "/activity/update";
	/** 获取签到报名信息的地址 */
	private static final String DETAIL_URL = DOMAIN + "/activity/%d/detail";

	@Resource
	private RestTemplate restTemplate;

	/**创建报名签到
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 14:26:33
	 * @param signForm
	 * @param request
	 * @return java.lang.Integer 签到报名id
	*/
	public Integer create(SignFormDTO signForm, HttpServletRequest request) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		List<String> cookies = RestTemplateUtils.getCookies(request);
		httpHeaders.put("Cookie", cookies);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signForm), httpHeaders);
		String result = restTemplate.postForObject(CREATE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return jsonObject.getInteger("data");
		} else {
			String errorMessage = jsonObject.getString("message");
			log.error("创建报名报名:{}失败:{}", JSON.toJSONString(signForm), errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**更新报名签到
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 17:58:39
	 * @param signForm
	 * @return void
	*/
	public void update(SignFormDTO signForm, HttpServletRequest request) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		List<String> cookies = RestTemplateUtils.getCookies(request);
		httpHeaders.put("Cookie", cookies);
		HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(signForm), httpHeaders);
		String result = restTemplate.postForObject(UPDATE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String errorMessage = jsonObject.getString("message");
			log.error("修改签到报名:{}失败:{}", JSON.toJSONString(signForm), errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**获取签到报名信息
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-19 13:15:47
	 * @param signId
	 * @return com.chaoxing.activity.dto.module.SignFormDTO
	*/
	public SignFormDTO getById(Integer signId) {
		String url = String.format(DETAIL_URL, signId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			String data = jsonObject.getString("data");
			SignFormDTO signForm = JSON.parseObject(data, SignFormDTO.class);
			LocalDateTime signUpStartTime = signForm.getSignUpStartTime();
			LocalDateTime signUpEndTime = signForm.getSignUpEndTime();
			LocalDateTime signInStartTime = signForm.getSignInStartTime();
			LocalDateTime signInEndTime = signForm.getSignInEndTime();
			signForm.setSignUpStartTimeStr(signUpStartTime == null ? "" : DateTimeFormatterConstant.YYYY_MM_DD_HH_MM_SS.format(signUpStartTime));
			signForm.setSignUpEndTimeStr(signUpEndTime == null ? "" : DateTimeFormatterConstant.YYYY_MM_DD_HH_MM_SS.format(signUpEndTime));
			signForm.setSignInStartTimeStr(signInStartTime == null ? "" : DateTimeFormatterConstant.YYYY_MM_DD_HH_MM_SS.format(signInStartTime));
			signForm.setSignInEndTimeStr(signInEndTime == null ? "" : DateTimeFormatterConstant.YYYY_MM_DD_HH_MM_SS.format(signInEndTime));
			return signForm;
		} else {
			String errorMessage = jsonObject.getString("message");
			log.error("根据签到报名id:{}查询签到报名失败:{}", signId, errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

}