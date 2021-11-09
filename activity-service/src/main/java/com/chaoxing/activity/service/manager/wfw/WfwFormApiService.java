package com.chaoxing.activity.service.manager.wfw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.form.FormAdvanceSearchFilterConditionDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import com.chaoxing.activity.vo.manager.WfwFormVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**万能表单api服务
 * @author wwb
 * @version ver 1.0
 * @className FormApiService
 * @description
 * @blame wwb
 * @date 2020-11-18 18:50:06
 */
@Slf4j
@Service
public class WfwFormApiService {

	/** 日期格式化 */
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");
	/** 全日期格式化 */
	public static final DateTimeFormatter DATA_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	/** sign */
	private static final String SIGN = "appsFormsData_hdpt";
	/** key */
	private static final String KEY = "fCkw1Jq&oAHxYKmwsK";
	/** 表单每页数据限制 */
	private static final int MAX_PAGE_SIZE_LIMIT = 100;
	private static final int MAX_DELETE_SIZE_LIMIT = 100;

	/** 表单api域名 */
	private static final String FORM_API_DOMAIN = "https://m.oa.chaoxing.com";
	/** 获取机构下表单列表url */
	private static final String GET_ORG_FORMS_URL = FORM_API_DOMAIN + "/api/apps/forms/app/list";
	/** 获取表单字段信息url */
	private static final String GET_FORM_STRUCTURE_URL = FORM_API_DOMAIN + "/api/apps/forms/app/config/values";
	/** 获取表单指定数据url */
	private static final String LIST_FORM_SPECIFIED_DATA_URL = FORM_API_DOMAIN + "/api/apps/forms/user/data/list";
	/** 填写表单url */
	private static final String FILL_FORM_URL = FORM_API_DOMAIN + "/api/apps/forms/user/save";
	/** 修改表单url */
	private static final String UPDATE_FORM_URL = FORM_API_DOMAIN + "/api/apps/forms/user/edit";
	/** 删除表单记录url */
	private static final String DELETE_FORM_RECORD_URL = FORM_API_DOMAIN + "/api/apps/forms/user/del";
	/** 高级检索 */
	private static final String ADVANCED_SEARCH_URL = FORM_API_DOMAIN + "/api/apps/forms/user/advanced/search/list";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	private String getEnc(Map<String, Object> encParamMap) {
		StringBuilder enc = new StringBuilder();
		for (Map.Entry<String, Object> entry : encParamMap.entrySet()) {
			enc.append("[").append(entry.getKey()).append("=")
					.append(entry.getValue()).append("]");
		}
		return DigestUtils.md5Hex(enc + "[" + KEY + "]");
	}

	/**查询表单记录
	 * @Description
	 * @author wwb
	 * @Date 2021-08-30 11:04:08
	 * @param formUserIds
	 * @param formId
	 * @param fid
	 * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO>
	 */
	public List<FormDataDTO> listFormRecord(List<Integer> formUserIds, Integer formId, Integer fid) {
		if (CollectionUtils.isEmpty(formUserIds)) {
			return Lists.newArrayList();
		}
		TreeMap<String, Object> paramsMap = Maps.newTreeMap();
		paramsMap.put("deptId", fid);
		paramsMap.put("formId", formId);
		paramsMap.put("formUserIds", String.join(",", Optional.of(formUserIds).orElse(Lists.newArrayList()).stream().map(String::valueOf).collect(Collectors.toList())));
		paramsMap.put("sign", SIGN);
		paramsMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		paramsMap.put("enc", getEnc(paramsMap));
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.setAll(paramsMap);
		String result = restTemplate.postForObject(LIST_FORM_SPECIFIED_DATA_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		if (jsonObject.getBoolean("success")) {
			JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("formUserList");
			if (jsonArray != null) {
				return JSON.parseArray(jsonArray.toJSONString(), FormDataDTO.class);
			} else {
				return Lists.newArrayList();
			}
		} else {
			String errorMessage = jsonObject.getString("msg");
			throw new BusinessException(errorMessage);
		}
	}

	/**获取表单记录
	 * @Description
	 * @author wwb
	 * @Date 2021-08-30 11:35:11
	 * @param formUserId
	 * @param formId
	 * @param fid
	 * @return com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO
	 */
	public FormDataDTO getFormRecord(@NotNull Integer formUserId, Integer formId, Integer fid) {
		List<Integer> formUserIds = Lists.newArrayList();
		formUserIds.add(formUserId);
		List<FormDataDTO> formDataDtos = listFormRecord(formUserIds, formId, fid);
		return Optional.ofNullable(formDataDtos).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
	}

	/**查询表单下的所有数据
	 * @Description
	 * @author wwb
	 * @Date 2021-08-30 16:08:14
	 * @param formId
	 * @param fid
	 * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO>
	 */
	public List<FormDataDTO> listFormRecord(Integer formId, Integer fid) {
		FormAdvanceSearchFilterConditionDTO formAdvanceSearchFilterConditionDto = FormAdvanceSearchFilterConditionDTO.builder()
				.model(FormAdvanceSearchFilterConditionDTO.ModelEnum.AND.getValue())
				.filters(Lists.newArrayList())
				.build();
		return advancedSearchAll(formAdvanceSearchFilterConditionDto, formId, fid);
	}

	/**高级检索表单所有数据
	 * @Description
	 * @author wwb
	 * @Date 2021-08-30 22:43:35
	 * @param formAdvanceSearchFilterConditionDto
	 * @param formId
	 * @param fid
	 * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO>
	 */
	public List<FormDataDTO> advancedSearchAll(FormAdvanceSearchFilterConditionDTO formAdvanceSearchFilterConditionDto, Integer formId, Integer fid) {
		return advancedSearchAll(formAdvanceSearchFilterConditionDto, formId, fid, 1, MAX_PAGE_SIZE_LIMIT);
	}

	/**高级检索表单数据
	 * @Description
	 * @author wwb
	 * @Date 2021-08-30 22:43:47
	 * @param formAdvanceSearchFilterConditionDto
	 * @param formId
	 * @param fid
	 * @param pageNum
	 * @param pageSize
	 * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO>
	 */
	public List<FormDataDTO> advancedSearchAll(FormAdvanceSearchFilterConditionDTO formAdvanceSearchFilterConditionDto, Integer formId, Integer fid, Integer pageNum, Integer pageSize) {
		TreeMap<String, Object> paramsMap = Maps.newTreeMap();
		paramsMap.put("deptId", fid);
		paramsMap.put("formId", formId);
		paramsMap.put("cpage", pageNum);
		paramsMap.put("pageSize", pageSize);
		paramsMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		paramsMap.put("sign", SIGN);
		paramsMap.put("enc", getEnc(paramsMap));
		paramsMap.put("searchStr", JSON.toJSONString(formAdvanceSearchFilterConditionDto));
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.setAll(paramsMap);
		String result = restTemplate.postForObject(ADVANCED_SEARCH_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		if (jsonObject.getBoolean("success")) {
			List<FormDataDTO> formDataDtos = Lists.newArrayList();
			JSONObject data = jsonObject.getJSONObject("data");
			JSONArray jsonArray = data.getJSONArray("dataList");
			formDataDtos.addAll(FormDataDTO.buildFromAdvanceSearchResult(jsonArray));
			Integer totalPage = data.getInteger("totalPage");
			if (totalPage > pageNum) {
				formDataDtos.addAll(advancedSearchAll(formAdvanceSearchFilterConditionDto, formId, fid, ++pageNum, pageSize));
			}
			return formDataDtos;
		} else {
			String errorMessage = jsonObject.getString("msg");
			throw new BusinessException(errorMessage);
		}
	}

	/**删除活动记录
	 * @Description
	 * @author wwb
	 * @Date 2021-09-02 16:56:50
	 * @param formUserId
	 * @param formId
	 * @return void
	 */
	public void deleteFormRecord(Integer formUserId, Integer formId) {
		if (formUserId == null) {
			return;
		}
		List<Integer> formUserIds = Lists.newArrayList();
		formUserIds.add(formUserId);
		deleteFormRecord(formUserIds, formId);
	}
	/**删除表单记录
	 * @Description
	 * @author wwb
	 * @Date 2021-08-31 19:35:17
	 * @param formUserIds
	 * @param formId
	 * @return void
	 */
	public void deleteFormRecord(List<Integer> formUserIds, Integer formId) {
		if (CollectionUtils.isEmpty(formUserIds)) {
			return;
		}
		List<List<Integer>> partition = Lists.partition(formUserIds, MAX_DELETE_SIZE_LIMIT);
		for (List<Integer> integers : partition) {
			TreeMap<String, Object> paramsMap = Maps.newTreeMap();
			paramsMap.put("formId", formId);
			paramsMap.put("formUserId", String.join(",", integers.stream().map(String::valueOf).collect(Collectors.toList())));
			paramsMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
			paramsMap.put("sign", SIGN);
			paramsMap.put("enc", getEnc(paramsMap));
			MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
			params.setAll(paramsMap);
			String result = restTemplate.postForObject(DELETE_FORM_RECORD_URL, params, String.class);
			JSONObject jsonObject = JSON.parseObject(result);
			if (Objects.equals(jsonObject.getBoolean("success"), true)) {

			} else {
				String message = jsonObject.getString("msg");
				throw new BusinessException(message);
			}
		}
	}

	/**填写表单
	 * @Description
	 * @author wwb
	 * @Date 2021-08-31 19:45:37
	 * @param formId
	 * @param fid
	 * @param uid
	 * @param data
	 * @return java.lang.Integer
	 */
	public Integer fillForm(Integer formId, Integer fid, Integer uid, String data) {
		TreeMap<String, Object> paramMap = Maps.newTreeMap();
		paramMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		paramMap.put("deptId", fid);
		paramMap.put("sign", SIGN);
		paramMap.put("formId", formId);
		paramMap.put("uid", uid);
		paramMap.put("comptIdValues", data);
		paramMap.put("enc", getEnc(paramMap));
		MultiValueMap<String, Object> params = new LinkedMultiValueMap();
		params.setAll(paramMap);
		String result = restTemplate.postForObject(FILL_FORM_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			jsonObject = jsonObject.getJSONObject("data");
			return jsonObject.getInteger("formUserId");
		} else {
			String errorMessage = jsonObject.getString("msg");
			log.error("根据参数:{}, 填写表单error:{}", JSON.toJSONString(paramMap), errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**获取活动结构
	 * @Description
	 * @author wwb
	 * @Date 2021-09-01 09:58:58
	 * @param formId
	 * @param fid
	 * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormStructureDTO>
	 */
	public List<FormStructureDTO> getFormStructure(Integer formId, Integer fid) {
		TreeMap<String, Object> paramMap = Maps.newTreeMap();
		paramMap.put("formId", formId);
		paramMap.put("deptId", fid);
		paramMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		paramMap.put("sign", SIGN);
		paramMap.put("enc", getEnc(paramMap));
		MultiValueMap<String, Object> params = new LinkedMultiValueMap();
		params.setAll(paramMap);
		String result = restTemplate.postForObject(GET_FORM_STRUCTURE_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		if (Objects.equals(jsonObject.getBoolean("success"), true)) {
			return JSON.parseArray(jsonObject.getString("data"), FormStructureDTO.class);
		} else {
			throw new BusinessException("获取表单:" + formId + "的结构失败");
		}
	}

	/**更新表单数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-24 12:39:30
	 * @param formId
	 * @param formUserId
	 * @param data
	 * @return void
	*/
	public void updateForm(Integer formId, Integer formUserId, String data) {
		if (StringUtils.isBlank(data)) {
			log.error("根据参数formId:{}, formUserId:{}, data:{} 更新万能表单error: 更新的数据不能为空", formId, formUserId, data);
			return;
		}
		TreeMap<String, Object> paramsMap = Maps.newTreeMap();
		paramsMap.put("formId", formId);
		paramsMap.put("formUserId", formUserId);
		paramsMap.put("comptIdValues", data);
		paramsMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		paramsMap.put("sign", SIGN);
		paramsMap.put("enc", getEnc(paramsMap));
		MultiValueMap<String, Object> params = new LinkedMultiValueMap();
		params.setAll(paramsMap);
		String result = restTemplate.postForObject(UPDATE_FORM_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String errorMessage = jsonObject.getString("msg");
			log.error("根据参数:{}更新表单数据error:{}", JSON.toJSONString(paramsMap), errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**添加简单的用户数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-19 19:16:42
	 * @param fid
	 * @param formId
	 * @param uid
	 * @param userName
	 * @return void
	*/
	public void addSimpleUserInfo(Integer fid, Integer formId, Integer uid, String userName) {
		JSONArray data = new JSONArray();
		JSONObject user = new JSONObject();
		user.put("alias", "user");
		user.put("compt", "editinput");
		user.put("val", userName);
		data.add(user);
		fillForm(formId, fid, uid, data.toJSONString());
	}

	/**获取机构下的表单列表
	 * @Description
	 * @author wwb
	 * @Date 2021-07-08 17:42:43
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.vo.manager.WfwFormVO>
	 */
	public List<WfwFormVO> listOrgForm(Integer fid) {
		Map<String, Object> params = new TreeMap<>();
		params.put("deptId", fid);
		params.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		params.put("sign", SIGN);
		params.put("enc", getEnc(params));
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap();
		paramMap.setAll(params);
		String result = restTemplate.postForObject(GET_ORG_FORMS_URL, paramMap, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return JSON.parseArray(jsonObject.getJSONObject("data").getString("dataList"), WfwFormVO.class);
		} else {
			String errorMessage = jsonObject.getString("msg");
			throw new BusinessException(errorMessage);
		}
	}

	/**查询表单某个字段的值列表
	 * @Description
	 * @author wwb
	 * @Date 2021-07-12 10:41:39
	 * @param fid
	 * @param formId
	 * @param fieldName
	 * @return java.util.List<java.lang.String>
	 */
	public List<String> listFormFieldValue(Integer fid, Integer formId, String fieldName) {
		List<FormDataDTO> wfwFormData = listFormRecord(formId, fid);
		TreeSet<String> fieldValueSet = Sets.newTreeSet();
		if (CollectionUtils.isNotEmpty(wfwFormData)) {
			for (FormDataDTO formDataDTO : wfwFormData) {
				String fieldValue = formDataDTO.getFieldValue(fieldName);
				if (StringUtils.isNotBlank(fieldValue)) {
					fieldValueSet.add(fieldValue);
				}
			}
		}
		return new ArrayList<>(fieldValueSet);
	}

	/**查询表单数据中某个字段的uid列表
	 * @Description
	 * @author wwb
	 * @Date 2021-07-16 10:45:56
	 * @param fid
	 * @param formId
	 * @param fieldName
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listFormFieldUid(Integer fid, Integer formId, String fieldName) {
		List<FormDataDTO> wfwFormData = listFormRecord(formId, fid);
		TreeSet<Integer> fieldValueSet = Sets.newTreeSet();
		if (CollectionUtils.isNotEmpty(wfwFormData)) {
			// 查询表单结构
			List<FormStructureDTO> formStructure = getFormStructure(formId, fid);
			// 根据fieldName找到字段id
			String fieldAlias = Optional.ofNullable(formStructure).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(v.getLabel(), fieldName)).findFirst().map(FormStructureDTO::getAlias).orElse(null);
			if (StringUtils.isNotBlank(fieldAlias)) {
				for (FormDataDTO wfwFormDatum : wfwFormData) {
					Integer uid = wfwFormDatum.getUidByAlias(fieldAlias);
					if (uid != null) {
						fieldValueSet.add(uid);
					}
				}
			}
		}
		return new ArrayList<>(fieldValueSet);
	}
}