package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.activity.VolunteerServiceDTO;
import com.chaoxing.activity.dto.manager.form.FormDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
	/** 表单每页数据限制 */
	private static final int DEFAULT_PAGE_SIZE_LIMIT = 80;
	/** 表单默认排序方式 */
	private static final String DEFAULT_ORDER_TYPE = "desc";
	/** 表单api域名 */
	private static final String FORM_API_DOMAIN = "http://m.oa.chaoxing.com";

	/** 获取机构下表单列表url */
	private static final String GET_ORG_FORMS_URL = FORM_API_DOMAIN + "/api/apps/forms/app/list?deptId=%d&datetime=%s&sign=%s&enc=%s";
	/** 获取表单字段信息url */
	private static final String GET_FORM_DETAIL_URL = FORM_API_DOMAIN + "/api/apps/forms/app/config/values?deptId=%d&formId=%d&datetime=%s&sign=%s&enc=%s";
	/** 获取表单数据url */
	private static final String LIST_FORM_DATA_URL = FORM_API_DOMAIN + "/api/apps/forms/user/records/list?deptId=%d&formId=%d&datetime=%s&orderType=%s&limit=%d&sign=%s&enc=%s&scrollId=%s&formUserId=%s";
	/** 填写表单url */
	private static final String FILL_FORM_URL = FORM_API_DOMAIN + "/api/apps/forms/user/save";
	/** 修改表单url */
	private static final String UPDATE_FORM_URL = FORM_API_DOMAIN + "/api/apps/forms/user/edit";
	/** 删除表单记录url */
	private static final String DELETE_FORM_RECORD_URL = FORM_API_DOMAIN + "/api/apps/forms/user/del?formId=%d&formUserId=%d&datetime=%s&sign=%s&enc=%s";

	/** 高级检索 */
	private static final String ADVANCED_SEARCH_URL = FORM_API_DOMAIN + "/api/apps/forms/user/advanced/search/list";

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
		List<FormDTO> forms;
		LocalDateTime now = LocalDateTime.now();
		String formatDateStr = now.format(DATE_TIME_FORMATTER);
		String enc = calListOrgFormEnc(fid, formatDateStr);
		String url = String.format(GET_ORG_FORMS_URL, fid, formatDateStr, SIGN, enc);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("dataList");
			int size = jsonArray.size();
			if (size > 0) {
				forms = JSON.parseArray(jsonArray.toJSONString(), FormDTO.class);
			} else {
				forms = Lists.newArrayList();
			}
			return forms;
		} else {
			String errorMessage = jsonObject.getString("msg");
			throw new BusinessException(errorMessage);
		}
	}

	private String calListOrgFormEnc(Integer fid, String formatDateStr) {
		String enc = "[datetime=" + formatDateStr + "][deptId=" + fid + "][sign=" + SIGN + "][" + KEY + "]";
		return DigestUtils.md5Hex(enc);
	}

	/**获取表单结构信息
	 * @Description
	 * @author wwb
	 * @Date 2021-03-09 13:00:37
	 * @param fid
	 * @param formId
	 * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormStructureDTO>
	 */
	public List<FormStructureDTO> getFormInfo(Integer fid, Integer formId) {
		JSONArray data = getFormInfoData(fid, formId);
		if (data.size() > 0) {
			return JSON.parseArray(data.toJSONString(), FormStructureDTO.class);
		} else {
			return null;
		}
	}

	/**获取表单结构信息
	* @Description
	* @author huxiaolong
	* @Date 2021-05-19 18:16:39
	* @param fid
	* @param formId
	* @return com.alibaba.fastjson.JSONArray
	*/
	public JSONArray getFormInfoData(Integer fid, Integer formId) {
		LocalDateTime now = LocalDateTime.now();
		String formatDateStr = now.format(DATE_TIME_FORMATTER);
		String enc = calGetOrgFormEnc(fid, formId, formatDateStr);
		String url = String.format(GET_FORM_DETAIL_URL, fid, formId, formatDateStr, SIGN, enc);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			return jsonObject.getJSONArray("data");
		} else {
			String errorMessage = jsonObject.getString("msg");
			log.error("根据fid:{},表单id:{} 获取表单信息error:{}", fid, formId, errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	private String calGetOrgFormEnc(Integer fid, Integer formId, String formatDateStr) {
		String enc = "[datetime=" + formatDateStr + "][deptId=" + fid + "][formId=" + formId + "][sign=" + SIGN + "][" + KEY + "]";
		return DigestUtils.md5Hex(enc);
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
		String enc = calFillFormEnc(uid, fid, formId, formatDateStr, data);
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

	private String calFillFormEnc(Integer uid, Integer fid, Integer formId, String formatDateStr, String data) {
		String enc = "[comptIdValues=" + data + "][datetime=" + formatDateStr + "][deptId=" + fid + "][formId=" + formId + "][sign=" + SIGN + "][uid=" + uid + "][" + KEY + "]";
		return DigestUtils.md5Hex(enc);
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
		// 先查看表单数据是否存在
		LocalDateTime now = LocalDateTime.now();
		String formatDateStr = now.format(DATE_TIME_FORMATTER);
		String enc = calUpdateFormEnc(formId, formUserId, formatDateStr, data);
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap();
		paramMap.add("datetime", formatDateStr);
		paramMap.add("sign", SIGN);
		paramMap.add("enc", enc);
		paramMap.add("formId", formId);
		paramMap.add("formUserId", formUserId);
		paramMap.add("comptIdValues", data);
		String result = restTemplate.postForObject(UPDATE_FORM_URL, paramMap, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String errorMessage = jsonObject.getString("msg");
			log.info("填写表单error:{}", errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	private String calUpdateFormEnc(Integer formId, Integer formUserId, String formatDateStr, String data) {
		String enc = "[comptIdValues=" + data + "][datetime=" + formatDateStr + "][formId=" + formId + "][formUserId=" + formUserId + "][sign=" + SIGN + "][" + KEY + "]";
		return DigestUtils.md5Hex(enc);
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
		String enc = calDeleteFormRecordEnc(formId, formUserId, formatDateStr);
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

	private String calDeleteFormRecordEnc(Integer formId, Integer formUserId, String formatDateStr) {
		String enc = "[datetime=" + formatDateStr + "][formId=" + formId + "][formUserId=" + formUserId + "][sign=" + SIGN + "][" + KEY + "]";
		return DigestUtils.md5Hex(enc);
	}
	/**获取表单数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-09 12:47:11
	 * @param fid
	 * @param formId
	 * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDTO>
	 */
	public List<FormDTO> listFormData(Integer fid, Integer formId) {
		return listFormData(fid, formId, "", null);
	}

	/**获取表单数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-10 16:53:43
	 * @param fid
	 * @param formId
	 * @param dataId
	 * @return com.chaoxing.secondclassroom.dto.manager.form.FormDTO
	 */
	public FormDTO getFormData(Integer fid, Integer formId, Integer dataId) {
		List<FormDTO> formData = listFormData(fid, formId, "", dataId);
		if (CollectionUtils.isNotEmpty(formData)) {
			return formData.get(0);
		}
		return null;
	}

	/**获取表单数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-09 12:33:50
	 * @param fid
	 * @param formId
	 * @param scrollId 游标id
	 * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDTO>
	 */
	private List<FormDTO> listFormData(Integer fid, Integer formId, String scrollId, Integer dataId) {
		List<FormDTO> forms = Lists.newArrayList();
		LocalDateTime now = LocalDateTime.now();
		String dateStr = now.format(DATE_TIME_FORMATTER);
		int limit = DEFAULT_PAGE_SIZE_LIMIT;
		String orderType = DEFAULT_ORDER_TYPE;
		String enc = calListFormDataEnc(fid, formId, dateStr, limit, orderType, dataId);
		String formUserId = "";
		if (dataId != null) {
			formUserId = String.valueOf(dataId);
		}
		String url = String.format(LIST_FORM_DATA_URL, fid, formId, dateStr, orderType, limit, SIGN, enc, scrollId, formUserId);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			jsonObject = jsonObject.getJSONObject("data");
			JSONArray data = jsonObject.getJSONArray("data");
			if (data.size() > 0) {
				List<FormDTO> subForms = JSON.parseArray(data.toJSONString(), FormDTO.class);
				// 判断数据有没有获取完
				if (subForms.size() == limit) {
					scrollId = jsonObject.getString("scrollId");
					subForms.addAll(listFormData(fid, formId, scrollId, dataId));
				}
				forms.addAll(subForms);
			}
			return forms;
		} else {
			String errorMessage = jsonObject.getString("msg");
			log.error("获取机构:{}的表单:{}数据error:{}, url:{}", fid, formId, errorMessage, url);
			throw new BusinessException(errorMessage);
		}
	}

	private String calListFormDataEnc(Integer fid, Integer formId, String dateStr, Integer limit, String orderType, Integer formUserId) {
		String formUserIdStr = "";
		if (formUserId != null) {
			formUserIdStr = String.valueOf(formUserId);
		}
		String enc = "[datetime=" + dateStr + "][deptId=" + fid + "][formId=" + formId + "][formUserId=" + formUserIdStr + "][limit=" + limit + "][orderType=" + orderType + "][sign=" + SIGN + "][" + KEY + "]";
		return DigestUtils.md5Hex(enc);
	}



	/**调用高级检索接口，分页查询用户的服务时长记录
	* @Description
	* @author huxiaolong
	* @Date 2021-05-19 15:24:38
	* @param page
	* @param fid
	* @param uid
	* @param formId
	* @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.activity.VolunteerServiceDTO>
	*/
	public Page<VolunteerServiceDTO> pageVolunteerRecord(Page<VolunteerServiceDTO> page, Integer fid, Integer uid, Integer formId) {
		// 创建encParamMap, 存储enc加密所需内容
		TreeMap<String, Object> encParamMap = new TreeMap<>();
		LocalDateTime now = LocalDateTime.now();
		String dateFormatStr = now.format(DATE_TIME_FORMATTER);
		encParamMap.put("deptId", fid);
		encParamMap.put("uids", uid);
		encParamMap.put("cpage", page.getCurrent());
		encParamMap.put("pageSize", page.getSize());
		encParamMap.put("formId", formId);
		encParamMap.put("datetime", dateFormatStr);
		encParamMap.put("sign", SIGN);

		String enc = calAdvanceSearchEnc(encParamMap);
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap();
		paramMap.setAll(encParamMap);
		paramMap.add("enc", enc);

		String result = restTemplate.postForObject(ADVANCED_SEARCH_URL, paramMap, String.class);
		JSONObject resultObj = JSON.parseObject(result);

		Boolean success = resultObj.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);

		if (success) {
			resultObj = resultObj.getJSONObject("data");
			JSONArray dataList = resultObj.getJSONArray("dataList");

			int totalRow = Integer.parseInt(resultObj.getString("totalRow"));
			int totalPage = Integer.parseInt(resultObj.getString("totalPage"));

			List<VolunteerServiceDTO> records = new ArrayList<>();
			if (dataList.size() > 0) {
				for (Object obj : dataList) {
					JSONObject item = (JSONObject) obj;
					VolunteerServiceDTO volunteerDTO = new VolunteerServiceDTO();
					volunteerDTO.setUid(Integer.valueOf(item.getString("uid")));
					volunteerDTO.setFormUserId(Integer.valueOf(item.getString("id")));

					JSONObject valueData = (JSONObject) item.get("formIdValueData");
					for (Map.Entry<String, Object> entry : valueData.entrySet()) {
						JSONObject itemJsonObj = JSON.parseObject(JSON.toJSONString(entry.getValue()));
						JSONArray groupValues = itemJsonObj.getJSONArray("groupValues");
						JSONArray values = (JSONArray) JSON.parseObject(JSON.toJSONString(groupValues.get(0))).getJSONArray("values").get(0);
						String val = "";
						if (!values.isEmpty()) {
							JSONObject value = JSON.parseObject(JSON.toJSONString(values.get(0)));
							val = value.getString("val");
						}
						if ("name".equals(entry.getKey())) {
							volunteerDTO.setName(val);
						} else if ("type".equals(entry.getKey())) {
							volunteerDTO.setType(val);
						}  else if ("department".equals(entry.getKey())) {
							volunteerDTO.setDepartment(val);
						} else if ("date".equals(entry.getKey())) {
							if (StringUtils.isNotBlank(val)) {
								volunteerDTO.setServiceDate(LocalDate.parse(val, DateUtils.DAY_DATE_TIME_FORMATTER));
							}
						} else if ("time_length".equals(entry.getKey())) {
							Long timeLength = StringUtils.isBlank(val)? 0L : Long.parseLong(val);
							volunteerDTO.setTimeLength(timeLength);
						} else if ("no".equals(entry.getKey())) {
							volunteerDTO.setNo(val);
						} else if ("level".equals(entry.getKey())) {
							volunteerDTO.setLevel(val);
						} else if ("17".equals(entry.getKey())) {
							volunteerDTO.setAffiliations(val);
						}

					}

					records.add(volunteerDTO);
				}
			}
			Page<VolunteerServiceDTO> resPage = new Page<>();
			resPage.setCurrent(page.getCurrent());
			resPage.setSize(page.getSize());
			resPage.setTotal(totalRow);
			resPage.setPages(totalPage);
			resPage.setRecords(records);
			return resPage;
		} else {
			String errorMessage = resultObj.getString("msg");
			log.error("获取用户:{}在机构:{}下的表单:{}数据error:{}, url:{}", uid, fid, formId, errorMessage, ADVANCED_SEARCH_URL);
			throw new BusinessException(errorMessage);
		}

	}

	private String calAdvanceSearchEnc(TreeMap<String, Object> encParamMap) {
		StringBuilder enc = new StringBuilder();
		for (Map.Entry<String, Object> entry : encParamMap.entrySet()) {
			enc.append("[").append(entry.getKey()).append("=")
					.append(entry.getValue()).append("]");
		}
		return DigestUtils.md5Hex(enc + "[" + KEY + "]");
	}
}