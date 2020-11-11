package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.module.WorkFormDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Optional;

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
	private static final String CREATE_URL = "http://api.reading.chaoxing.com/activity/create";

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
		JSONObject data = new JSONObject();
		data.put("activityName", workForm.getName());
		data.put("wfwfid", workForm.getWfwfid());
		data.put("uid", workForm.getUid());
		HttpEntity<String> httpEntity = new HttpEntity<>(data.toJSONString());
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


}