package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.OrgFormConfigDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**第二课堂api
 * @author wwb
 * @version ver 1.0
 * @className SecondClassroomApiService
 * @description
 * @blame wwb
 * @date 2021-06-08 16:00:13
 */
@Slf4j
@Service
public class SecondClassroomApiService {

	/** 第二课堂api域名 */
	private static final String SECOND_CLASSROOM_API_DOMAIN = "http://hd.chaoxing.com/second_classroom";
	/** 获取机构配置的表单 */
	private static final String GET_ORG_FORM_CONFIG_URL = SECOND_CLASSROOM_API_DOMAIN + "/api/org/%d/form";
	/** 配置机构的表单 */
	private static final String ORG_FORM_CONFIG_URL = SECOND_CLASSROOM_API_DOMAIN + "/api/org/form/config";

	@Resource
	private RestTemplate restTemplate;

	/**结果处理
	 * @Description
	 * @author wwb
	 * @Date 2021-05-26 11:18:14
	 * @param jsonObject
	 * @param successCallback
	 * @param errorCallback
	 * @return T
	 */
	private <T> T resultHandle(JSONObject jsonObject, Supplier<T> successCallback, Consumer<String> errorCallback) {
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return successCallback.get();
		} else {
			errorCallback.accept(jsonObject.getString("message"));
			return null;
		}
	}

	/**获取机构表单配置
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-08 16:10:50
	 * @param fid
	 * @return com.chaoxing.activity.dto.OrgFormConfigDTO
	*/
	public OrgFormConfigDTO getOrgFormConfig(Integer fid) {
		String url = String.format(GET_ORG_FORM_CONFIG_URL, fid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		return resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data"), OrgFormConfigDTO.class), (message) -> {
			throw new BusinessException(message);
		});
	}

	/**配置机构表单
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-08 16:47:36
	 * @param orgFormConfig
	 * @return void
	*/
	public void configOrgForm(OrgFormConfigDTO orgFormConfig) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity(JSON.toJSONString(orgFormConfig), httpHeaders);
		String result = restTemplate.postForObject(ORG_FORM_CONFIG_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		resultHandle(jsonObject, () -> JSON.parseObject(jsonObject.getString("data")), (message) -> {
			throw new BusinessException(message);
		});
	}

}
