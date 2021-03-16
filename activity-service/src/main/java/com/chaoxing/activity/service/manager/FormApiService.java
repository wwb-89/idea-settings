package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.form.FormDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
	/** 获取表单字段信息url */
	private static final String GET_FORM_DETAIL_URL = "http://m.oa.chaoxing.com/api/apps/forms/app/config/values?deptId=%d&formId=%d&datetime=%s&sign=%s&enc=%s";
	/** 填写表单url */
	private static final String FILL_FORM_URL = "http://m.oa.chaoxing.com/api/apps/forms/user/save";
	/** 删除表单记录url */
	private static final String DELETE_FORM_RECORD_URL = "http://m.oa.chaoxing.com/api/apps/forms/user/del?formId=%d&formUserId=%d&datetime=%s&sign=%s&enc=%s";


	@Resource(name = "restTemplateProxy")
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
		String enc = getOrgFormEnc(fid, formatDateStr);
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
							.formId(data.getInteger("id"))
							.formName(data.getString("name"))
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

	private String getOrgFormEnc(Integer fid, String formatDateStr) {
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

	/**获取表单信息
	 * @Description
	 * @author wwb
	 * @Date 2021-02-06 18:48:10
	 * @param fid
	 * @param formId
	 * @return java.lang.String
	 */
	public String getFormInfo(Integer fid, Integer formId) {
		LocalDateTime now = LocalDateTime.now();
		String formatDateStr = now.format(DATE_TIME_FORMATTER);
		String enc = getOrgFormInfo(fid, formId, formatDateStr);
		String url = String.format(GET_FORM_DETAIL_URL, fid, formId, formatDateStr, SIGN, enc);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return jsonObject.getString("data");
		}
		String errorMessage = jsonObject.getString("msg");
		log.error("根据fid:{},表单id:{} 获取表单信息error:{}", fid, formId, errorMessage);
		throw new BusinessException(errorMessage);
	}

	private String getOrgFormInfo(Integer fid, Integer formId, String formatDateStr) {
		StringBuilder clearTextStringBuilder = new StringBuilder();
		clearTextStringBuilder.append("[datetime=");
		clearTextStringBuilder.append(formatDateStr);
		clearTextStringBuilder.append("][deptId=");
		clearTextStringBuilder.append(fid);
		clearTextStringBuilder.append("][formId=");
		clearTextStringBuilder.append(formId);
		clearTextStringBuilder.append("][sign=");
		clearTextStringBuilder.append(SIGN);
		clearTextStringBuilder.append("][");
		clearTextStringBuilder.append(KEY);
		clearTextStringBuilder.append("]");
		return DigestUtils.md5Hex(clearTextStringBuilder.toString());
	}

	/**填写表单
	 * @Description
	 * @author wwb
	 * @Date 2021-02-06 18:55:12
	 * @param fid
	 * @param formId
	 * @param uid
	 * @param data
	 * @return java.lang.Integer
	 */
	public Integer fillForm(Integer fid, Integer formId, Integer uid, String data) {
		LocalDateTime now = LocalDateTime.now();
		String formatDateStr = now.format(DATE_TIME_FORMATTER);
		String enc = getFillFormEnc(uid, fid, formId, formatDateStr, data);
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap();
		paramMap.add("datetime", formatDateStr);
		paramMap.add("sign", SIGN);
		paramMap.add("enc", enc);
		paramMap.add("deptId", fid);
		paramMap.add("uid", uid);
		paramMap.add("formId", formId);
		paramMap.add("comptIdValues", data);
		String result = restTemplate.postForObject(FILL_FORM_URL, paramMap, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			jsonObject = jsonObject.getJSONObject("data");
			return jsonObject.getInteger("formUserId");
		} else {
			String errorMessage = jsonObject.getString("msg");
			log.info("填写表单error:{}", errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	private String getFillFormEnc(Integer uid, Integer fid, Integer formId, String formatDateStr, String data) {
		StringBuilder clearTextStringBuilder = new StringBuilder();
		clearTextStringBuilder.append("[comptIdValues=");
		clearTextStringBuilder.append(data);
		clearTextStringBuilder.append("][datetime=");
		clearTextStringBuilder.append(formatDateStr);
		clearTextStringBuilder.append("][deptId=");
		clearTextStringBuilder.append(fid);
		clearTextStringBuilder.append("][formId=");
		clearTextStringBuilder.append(formId);
		clearTextStringBuilder.append("][sign=");
		clearTextStringBuilder.append(SIGN);
		clearTextStringBuilder.append("][uid=");
		clearTextStringBuilder.append(uid);
		clearTextStringBuilder.append("][");
		clearTextStringBuilder.append(KEY);
		clearTextStringBuilder.append("]");
		return DigestUtils.md5Hex(clearTextStringBuilder.toString());
	}

	/**删除表单记录
	 * @Description
	 * @author wwb
	 * @Date 2021-02-06 19:03:25
	 * @param formId
	 * @param formUserId
	 * @return void
	 */
	public void deleteFormRecord(Integer formId, Integer formUserId) {
		LocalDateTime now = LocalDateTime.now();
		String formatDateStr = now.format(DATE_TIME_FORMATTER);
		String enc = getDeleteFormRecordEnc(formId, formUserId, formatDateStr);
		String url = String.format(DELETE_FORM_RECORD_URL, formId, formUserId, formatDateStr, SIGN, enc);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String errorMessage = jsonObject.getString("msg");
			log.error("删除表单:{} 的记录:{} error:{}", formId, formUserId, errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	private String getDeleteFormRecordEnc(Integer formId, Integer formUserId, String formatDateStr) {
		StringBuilder clearTextStringBuilder = new StringBuilder();
		clearTextStringBuilder.append("[datetime=");
		clearTextStringBuilder.append(formatDateStr);
		clearTextStringBuilder.append("][formId=");
		clearTextStringBuilder.append(formId);
		clearTextStringBuilder.append("][formUserId=");
		clearTextStringBuilder.append(formUserId);
		clearTextStringBuilder.append("][sign=");
		clearTextStringBuilder.append(SIGN);
		clearTextStringBuilder.append("][");
		clearTextStringBuilder.append(KEY);
		clearTextStringBuilder.append("]");
		return DigestUtils.md5Hex(clearTextStringBuilder.toString());
	}

}