package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.module.PunchFormDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Optional;

/**打卡服务
 * @author wwb
 * @version ver 1.0
 * @className PunchApiService
 * @description
 * @blame wwb
 * @date 2020-11-11 10:34:13
 */
@Slf4j
@Service
public class PunchApiService {

	/** 创建打卡的url */
	private static final String PUNCH_CREATE_URL = "https://appcd.chaoxing.com/punch/api/punch/create";

	@Resource
	private RestTemplate restTemplate;

	/**
	 * @Description
	 * @author wwb
	 * @Date 2019-10-25 14:45:21
	 * @param punchForm
	 * @return java.lang.Integer 创建的打卡的id
	 */
	public Integer create(PunchFormDTO punchForm) {

		String punchName = punchForm.getName();
		Integer createUid = punchForm.getCreateUid();

		punchName = Optional.ofNullable(punchName).orElseThrow(() -> new BusinessException("打卡名称不能为空"));
		createUid = Optional.ofNullable(createUid).orElseThrow(() -> new BusinessException("创建人不能为空"));

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("name", punchName);
		params.add("needPubDynamic", punchForm.getNeedPubDynamic());
		params.add("uid", createUid);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
		String result = restTemplate.postForObject(PUNCH_CREATE_URL, request, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		if (jsonObject.getBooleanValue("success")) {
			return jsonObject.getInteger("data");
		} else {
			throw new BusinessException(jsonObject.getString("message"));
		}
	}

}