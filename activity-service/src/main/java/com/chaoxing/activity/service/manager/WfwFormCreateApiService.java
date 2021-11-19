package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateResultDTO;
import com.chaoxing.activity.model.SignUpFillInfoType;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**微服务表单创建服务
 * @author wwb
 * @version ver 1.0
 * @className WfwFormCreateApiService
 * @description
 * @blame wwb
 * @date 2021-07-09 10:59:34
 */
@Slf4j
@Service
public class WfwFormCreateApiService {

	private static final String SIGN = "deptManager_hdcp";
	private static final String KEY = "SObtv7P3d$UVuBkTjg";
	/** 日期格式化 */
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");

	private static final String CREATE_URL = DomainConstant.WFW_FORM_API_DOMAIN + "/api/manager/third/user/login/apps/create";
	/** 表单后台地址 */
	private static final String FORM_ADMIN_URL = DomainConstant.WFW_FORM_API_DOMAIN + "/api/manager/third/user/login/apps/manager?fid=%d&uid=%d&datetime=%s&sign=%s&formId=%d&formType=%d&enc=%s";
	/** 接口创建表单url */
	private static final String CREATE_FORM_URL = DomainConstant.WFW_FORM_API_DOMAIN + "/api/apps/forms/app/thirdcreate";

	@Resource
	private RestTemplate restTemplate;

	/**构建表单创建地址
	* @Description
	* @author huxiaolong
	* @Date 2021-08-17 17:49:15
	* @param fid
	* @param uid
	* @param templateType
	* @return java.lang.String
	*/
	public String buildCreateFormUrl(Integer fid, Integer uid, Integer formId, String templateType) {
		SignUpFillInfoType.WfwFormTemplateEnum wfwFormTemplateEnum = SignUpFillInfoType.WfwFormTemplateEnum.fromValue(templateType);
		if (wfwFormTemplateEnum == null) {
			throw new BusinessException("模板类型：" + templateType + "不存在!");
		}
		Map<String, Object> params = Maps.newTreeMap();
		params.put("fid", fid);
		params.put("uid", uid);
		if (formId != null) {
			params.put("formId", formId);
		}
		LocalDateTime now = LocalDateTime.now();
		params.put("datetime", now.format(DATE_TIME_FORMATTER));
		params.put("sign", wfwFormTemplateEnum.getSign());
		params.put("isCopy", 0);
		params.put("formType", 2);
		String enc = getEnc(params, wfwFormTemplateEnum.getKey());
		params.put("enc", enc);
		// 封装url
		StringBuilder url = new StringBuilder(CREATE_URL + "?");
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		return url.toString();
	}

	/**获取表单管理地址
	 * @Description
	 * @author wwb
	 * @Date 2021-08-18 15:07:48
	 * @param formId
	 * @param fid
	 * @param uid
	 * @return java.lang.String
	 */
	public String getFormAdminUrl(Integer formId, Integer fid, Integer uid) {
		Map<String, Object> encParamMap = Maps.newTreeMap();
		String dateTimeStr = LocalDateTime.now().format(DATE_TIME_FORMATTER);
		Integer formType = 2;
		encParamMap.put("fid", fid);
		encParamMap.put("uid", uid);
		encParamMap.put("datetime", dateTimeStr);
		encParamMap.put("sign", SIGN);
		encParamMap.put("formId", formId);
		encParamMap.put("formType", formType);
		String enc = getEnc(encParamMap, KEY);
		return String.format(FORM_ADMIN_URL, fid, uid, dateTimeStr, SIGN, formId, formType, enc);
	}

	private String getEnc(Map<String, Object> params, String key) {
		StringBuilder endBuilder = new StringBuilder();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			endBuilder.append("[");
			endBuilder.append(entry.getKey());
			endBuilder.append("=");
			endBuilder.append(entry.getValue());
			endBuilder.append("]");
		}
		endBuilder.append("[");
		endBuilder.append(key);
		endBuilder.append("]");
		return DigestUtils.md5Hex(endBuilder.toString());
	}

	/**创建万能表单
	 * @Description
	 * @author wwb
	 * @Date 2021-11-18 17:21:24
	 * @param wfwFormCreateParam
	 * @return com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateResultDTO
	 */
	public WfwFormCreateResultDTO create(WfwFormCreateParamDTO wfwFormCreateParam) {
		Map<String, Object> params = new TreeMap<>();
		params.put("formId", wfwFormCreateParam.getFormId());
		params.put("originalFid", wfwFormCreateParam.getOriginalFid());
		params.put("uid", wfwFormCreateParam.getUid());
		params.put("fid", wfwFormCreateParam.getFid());
		params.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		params.put("sign", SIGN);
		params.put("enc", getEnc(params));
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap();
		paramMap.setAll(params);
		String result = restTemplate.postForObject(CREATE_FORM_URL, paramMap, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return JSON.parseObject(jsonObject.getString("data"), WfwFormCreateResultDTO.class);
		} else {
			String message = jsonObject.getString("msg");
			log.error("根据参数:{} 创建万能表单error:{}", JSON.toJSONString(wfwFormCreateParam), message);
			throw new BusinessException(message);
		}
	}

	private String getEnc(Map<String, Object> encParamMap) {
		StringBuilder enc = new StringBuilder();
		for (Map.Entry<String, Object> entry : encParamMap.entrySet()) {
			enc.append("[").append(entry.getKey()).append("=")
					.append(entry.getValue()).append("]");
		}
		return DigestUtils.md5Hex(enc + "[" + KEY + "]");
	}

}