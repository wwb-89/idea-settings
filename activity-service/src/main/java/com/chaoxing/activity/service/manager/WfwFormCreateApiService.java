package com.chaoxing.activity.service.manager;

import com.chaoxing.activity.model.SignUpFillInfoType;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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

	private static final String CREATE_URL = "http://m.oa.chaoxing.com/api/manager/third/user/login/apps/create";

	@Resource
	private RestTemplate restTemplate;

	/**构建表单创建地址
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-05 14:11:21
	 * @param fid
	 * @param uid
	 * @return java.lang.String
	*/
	public String buildCreateFormUrl(Integer fid, Integer uid) {
		Map<String, Object> params = Maps.newTreeMap();
		params.put("fid", fid);
		params.put("uid", uid);
		LocalDateTime now = LocalDateTime.now();
		params.put("datetime", now.format(DATE_TIME_FORMATTER));
		params.put("sign", SIGN);
		params.put("isCopy", 0);
		params.put("formType", 2);
		String enc = getEnc(params, KEY);
		params.put("enc", enc);
		// 封装url
		String url = CREATE_URL + "?";
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			url += entry.getKey() + "=" + entry.getValue() + "&";
		}
		return url;
	}

	/**构建表单创建地址
	* @Description
	* @author huxiaolong
	* @Date 2021-08-17 17:49:15
	* @param fid
	* @param uid
	* @param templateType
	* @return java.lang.String
	*/
	public String buildCreateFormUrl(Integer fid, Integer uid, String templateType) {
		SignUpFillInfoType.WfwFormTemplateEnum wfwFormTemplateEnum = SignUpFillInfoType.WfwFormTemplateEnum.fromValue(templateType);
		if (wfwFormTemplateEnum == null) {
			throw new BusinessException("模板类型：" + templateType + "不存在!");
		}
		Map<String, Object> params = Maps.newTreeMap();
		params.put("fid", fid);
		params.put("uid", uid);
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

}