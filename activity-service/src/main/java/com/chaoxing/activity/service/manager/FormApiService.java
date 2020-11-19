package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.FormDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**表单api服务
 * @author wwb
 * @version ver 1.0
 * @className FormApiService
 * @description
 * @blame wwb
 * @date 2020-11-18 18:50:06
 */
@Slf4j
@Service
public class FormApiService {

	/** 日期格式化 */
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");
	/** sign */
	private static final String SIGN = "appsFormsData_hdpt";
	/** key */
	private static final String KEY = "fCkw1Jq&oAHxYKmwsK";

	/** 获取机构下表单列表url */
	private static final String GET_ORG_FORMS_URL = "http://m.oa.chaoxing.com/api/apps/forms/app/list?deptId=%d&datetime=%s&sign=%s&enc=%s";

	@Resource
	private RestTemplate restTemplate;

	/**获取机构下的表单列表
	 * @Description
	 * @author wwb
	 * @Date 2020-11-18 19:12:15
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.dto.manager.FormDTO>
	*/
	public List<FormDTO> listOrgForm(Integer fid) {
		List<FormDTO> forms = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();
		String formatDateStr = now.format(DATE_TIME_FORMATTER);
		String enc = getEnc(fid,formatDateStr);
		String url = String.format(GET_ORG_FORMS_URL, fid, formatDateStr, SIGN, enc);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("dataList");
			int size = jsonArray.size();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					JSONObject data = jsonArray.getJSONObject(i);
					FormDTO form = FormDTO.builder()
							.id(data.getString("id"))
							.name(data.getString("name"))
							.build();
					forms.add(form);
				}
			}
			return forms;
		} else {
			String errorMessage = jsonObject.getString("msg");
			throw new BusinessException(errorMessage);
		}
	}

	private String getEnc(Integer fid, String formatDateStr) {
		StringBuilder clearTextStringBuilder = new StringBuilder();
		clearTextStringBuilder.append("[datetime=");
		clearTextStringBuilder.append(formatDateStr);
		clearTextStringBuilder.append("][deptId=");
		clearTextStringBuilder.append(fid);
		clearTextStringBuilder.append("][sign=");
		clearTextStringBuilder.append(SIGN);
		clearTextStringBuilder.append("][");
		clearTextStringBuilder.append(KEY);
		clearTextStringBuilder.append("]");
		return DigestUtils.md5Hex(clearTextStringBuilder.toString());
	}

}