package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.activity.VolunteerServiceDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormFieldDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormFilterItemDTO;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**微服务表单api服务
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
	private static final String GET_ORG_FORMS_URL = FORM_API_DOMAIN + "/api/apps/forms/app/list";
	/** 获取表单字段信息url */
	private static final String GET_FORM_DETAIL_URL = FORM_API_DOMAIN + "/api/apps/forms/app/config/values";
	/** 获取表单数据url，第一次同步数据用， 接口每次调用会生成一个游标， 游标总数是有限的， 频率过高不适合使用 */
	private static final String LIST_FORM_DATA_URL = FORM_API_DOMAIN + "/api/apps/forms/user/records/list";
	/** 填写表单url */
	private static final String FILL_FORM_URL = FORM_API_DOMAIN + "/api/apps/forms/user/save";
	/** 修改表单url */
	private static final String UPDATE_FORM_URL = FORM_API_DOMAIN + "/api/apps/forms/user/edit";
	/** 删除表单记录url */
	private static final String DELETE_FORM_RECORD_URL = FORM_API_DOMAIN + "/api/apps/forms/user/del";

	/** 高级检索， 查询所有数据推荐 */
	private static final String ADVANCED_SEARCH_URL = FORM_API_DOMAIN + "/api/apps/forms/user/advanced/search/list";

	private static final String SEARCH_BY_FORM_USER_IDS_URL = FORM_API_DOMAIN + "/api/apps/forms/user/data/list";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

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

	/**获取表单结构信息
	 * @Description
	 * @author wwb
	 * @Date 2021-03-09 13:00:37
	 * @param fid
	 * @param formId
	 * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormFieldDTO>
	 */
	public List<WfwFormFieldDTO> listFormField(Integer fid, Integer formId) {
		JSONArray data = getFormInfoData(fid, formId);
		if (data.size() > 0) {
			return JSON.parseArray(data.toJSONString(), WfwFormFieldDTO.class);
		} else {
			return Lists.newArrayList();
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
		Map<String, Object> params = Maps.newTreeMap();
		params.put("deptId", fid);
		params.put("formId", formId);
		params.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		params.put("sign", SIGN);
		params.put("enc", getEnc(params));
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap();
		paramMap.setAll(params);
		String result = restTemplate.postForObject(GET_FORM_DETAIL_URL, paramMap, String.class);
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
	public Integer fillFormData(Integer fid, Integer formId, Integer uid, String data) {
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
			log.info("填写表单error:{}", errorMessage);
			throw new BusinessException(errorMessage);
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
	public void updateFormData(Integer formId, Integer formUserId, String data) {
		// 先查看表单数据是否存在
		TreeMap<String, Object> paramMap = Maps.newTreeMap();
		paramMap.put("formId", formId);
		paramMap.put("formUserId", formUserId);
		paramMap.put("comptIdValues", data);
		paramMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		paramMap.put("sign", SIGN);
		paramMap.put("enc", getEnc(paramMap));
		MultiValueMap<String, Object> params = new LinkedMultiValueMap();
		params.setAll(paramMap);
		String result = restTemplate.postForObject(UPDATE_FORM_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String errorMessage = jsonObject.getString("msg");
			log.info("填写表单error:{}", errorMessage);
			throw new BusinessException(errorMessage);
		}
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
		TreeMap<String, Object> paramMap = Maps.newTreeMap();
		paramMap.put("formId", formId);
		paramMap.put("formUserId", formUserId);
		paramMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		paramMap.put("sign", SIGN);
		paramMap.put("enc", getEnc(paramMap));
		MultiValueMap<String, Object> params = new LinkedMultiValueMap();
		params.setAll(paramMap);
		String result = restTemplate.postForObject(DELETE_FORM_RECORD_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (!success) {
			String errorMessage = jsonObject.getString("msg");
			log.error("删除表单:{} 的记录:{} error:{}", formId, formUserId, errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**获取表单数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-09 12:47:11
	 * @param fid
	 * @param formId
	 * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDTO>
	 */
	public List<WfwFormDTO> listFormData(Integer fid, Integer formId) {
		return listFormData(fid, formId, "", null);
	}

	public Page<WfwFormDTO> wfwFormDataPageAdvanceSearch(Page<WfwFormDTO> page, Integer fid, Integer formId, String searchStr) {
		if (page == null) {
			page = new Page<>(1, DEFAULT_PAGE_SIZE_LIMIT);
		}
		// 创建encParamMap, 存储enc加密所需内容
		TreeMap<String, Object> encParamMap = new TreeMap<>();
		encParamMap.put("deptId", fid);
		encParamMap.put("formId", formId);
		encParamMap.put("cpage", page.getCurrent());
		encParamMap.put("pageSize", page.getSize());
		encParamMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		encParamMap.put("orderType", DEFAULT_ORDER_TYPE);
		encParamMap.put("sign", SIGN);
		String enc = getEnc(encParamMap);
		encParamMap.put("enc", enc);
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap();
		paramMap.setAll(encParamMap);
		paramMap.add("searchStr", Optional.ofNullable(searchStr).orElse(""));

		String result = restTemplate.postForObject(ADVANCED_SEARCH_URL, paramMap, String.class);
		JSONObject resultObj = JSON.parseObject(result);

		Boolean success = resultObj.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);

		if (success) {
			JSONArray dataList = resultObj.getJSONObject("data").getJSONArray("dataList");
			int totalRow = Integer.parseInt(resultObj.getString("totalRow"));
			int totalPage = Integer.parseInt(resultObj.getString("totalPage"));
			List<WfwFormDTO> records = WfwFormDTO.buildFromAdvanceResult(dataList);
			page.setTotal(totalRow);
			page.setPages(totalPage);
			page.setRecords(records);
			return page;
		} else {
			String errorMessage = resultObj.getString("msg");
			log.error("获取在机构:{}下的表单:{}数据error:{}, url:{}", fid, formId, errorMessage, ADVANCED_SEARCH_URL);
			throw new BusinessException(errorMessage);
		}

	}

	/**通过高级检索获取表单全部数据
	* @Description
	* @author huxiaolong
	* @Date 2021-09-02 14:22:42
	* @param fid
	* @param formId
	* @return java.util.List<com.chaoxing.activity.dto.manager.wfwform.WfwFormDTO>
	*/
	public List<WfwFormDTO> listAllFormDataByAdvanceSearch(Integer fid, Integer formId) {
		List<WfwFormDTO> result = Lists.newArrayList();
		Page<WfwFormDTO> page = new Page<>(1, DEFAULT_PAGE_SIZE_LIMIT);
		page = wfwFormDataPageAdvanceSearch(page, fid, formId, null);
		result.addAll(page.getRecords());
		long pages = page.getPages();
		int maxQueryTime = 100;
		while (page.getCurrent() < pages && maxQueryTime-- > 0) {
			page.setCurrent(page.getCurrent() + 1);
			page = wfwFormDataPageAdvanceSearch(page, fid, formId, null);
			pages = page.getPages();
			result.addAll(page.getRecords());
		}
		return result;
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
		List<WfwFormDTO> wfwFormData = listFormData(fid, formId);
		TreeSet<String> fieldValueSet = Sets.newTreeSet();
		if (CollectionUtils.isNotEmpty(wfwFormData)) {
			for (WfwFormDTO wfwFormDatum : wfwFormData) {
				String fieldValue = wfwFormDatum.getFieldValue(fieldName);
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
		List<WfwFormDTO> wfwFormData = listFormData(fid, formId);
		TreeSet<Integer> fieldValueSet = Sets.newTreeSet();
		if (CollectionUtils.isNotEmpty(wfwFormData)) {
			for (WfwFormDTO wfwFormDatum : wfwFormData) {
				Integer uid = wfwFormDatum.getFieldUid(fieldName);
				if (uid != null) {
					fieldValueSet.add(uid);
				}
			}
		}
		return new ArrayList<>(fieldValueSet);
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
	public WfwFormDTO getFormData(Integer fid, Integer formId, Integer dataId) {
		List<WfwFormDTO> formData = listFormDataByFormUserIds(fid, formId, Optional.ofNullable(dataId).map(String::valueOf).orElse(null));
		if (CollectionUtils.isNotEmpty(formData)) {
			return formData.get(0);
		}
		return null;
	}

	/**根据formUserIds查询数据
	* @Description
	* @author huxiaolong
	* @Date 2021-09-02 14:20:49
	* @param fid
	* @param formId
	* @param formUserIds
	* @return java.util.List<com.chaoxing.activity.dto.manager.wfwform.WfwFormDTO>
	*/
	public List<WfwFormDTO> listFormDataByFormUserIds(Integer fid, Integer formId, String formUserIds) {
		List<WfwFormDTO> forms = Lists.newArrayList();
		TreeMap<String, Object> paramsMap = Maps.newTreeMap();
		paramsMap.put("deptId", fid);
		paramsMap.put("formId", formId);
		paramsMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		paramsMap.put("sign", SIGN);
		paramsMap.put("formUserIds", Optional.ofNullable(formUserIds).orElse(""));
		String enc = getEnc(paramsMap);
		paramsMap.put("enc", enc);
		MultiValueMap<String, Object> params = new LinkedMultiValueMap();
		params.setAll(paramsMap);
		String result = restTemplate.postForObject(SEARCH_BY_FORM_USER_IDS_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			JSONArray data = jsonObject.getJSONObject("data").getJSONArray("formUserList");
			if (data.size() > 0) {
				List<WfwFormDTO> subForms = JSON.parseArray(data.toJSONString(), WfwFormDTO.class);
				forms.addAll(subForms);
			}
			return forms;
		} else {
			String errorMessage = jsonObject.getString("msg");
			log.error("根据url: {}, 参数: {}, 获取机构表单数据error: {}", LIST_FORM_DATA_URL, JSON.toJSONString(paramsMap));
			throw new BusinessException(errorMessage);
		}
	}

	/**获取表单数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-09 12:33:50
	 * @param fid
	 * @param formId
	 * @param scrollId 游标id
	 * @param dataId 表单中记录id
	 * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDTO>
	 */
	private List<WfwFormDTO> listFormData(Integer fid, Integer formId, String scrollId, Integer dataId) {
		List<WfwFormDTO> forms = Lists.newArrayList();
		Integer limit = DEFAULT_PAGE_SIZE_LIMIT;
		TreeMap<String, Object> paramsMap = Maps.newTreeMap();
		paramsMap.put("deptId", fid);
		paramsMap.put("formId", formId);
		paramsMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
		paramsMap.put("orderType", DEFAULT_ORDER_TYPE);
		paramsMap.put("limit", limit);
		paramsMap.put("sign", SIGN);
		paramsMap.put("formUserId", Optional.ofNullable(dataId).map(String::valueOf).orElse(""));
		String enc = getEnc(paramsMap);
		paramsMap.put("enc", enc);
		paramsMap.put("scrollId", Optional.ofNullable(scrollId).orElse(""));
		MultiValueMap<String, Object> params = new LinkedMultiValueMap();
		params.setAll(paramsMap);
		String result = restTemplate.postForObject(LIST_FORM_DATA_URL, params, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean success = jsonObject.getBoolean("success");
		success = Optional.ofNullable(success).orElse(Boolean.FALSE);
		if (success) {
			jsonObject = jsonObject.getJSONObject("data");
			JSONArray data = jsonObject.getJSONArray("data");
			if (data.size() > 0) {
				List<WfwFormDTO> subForms = JSON.parseArray(data.toJSONString(), WfwFormDTO.class);
				// 判断数据有没有获取完
				if (subForms.size() == limit) {
					// 满一页
					scrollId = jsonObject.getString("scrollId");
					subForms.addAll(listFormData(fid, formId, scrollId, dataId));
				}
				forms.addAll(subForms);
			}
			return forms;
		} else {
			String errorMessage = jsonObject.getString("msg");
			log.error("根据url: {}, 参数: {}, 获取机构表单数据error: {}", LIST_FORM_DATA_URL, JSON.toJSONString(paramsMap));
			throw new BusinessException(errorMessage);
		}
	}

	/**调用高级检索接口，分页查询用户的服务时长记录
	* @Description
	* @author huxiaolong
	* @Date 2021-05-19 15:24:38
	* @param page
	* @param fid
	* @param uid
	* @param formId
	* @param serviceType
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.activity.VolunteerServiceDTO>
	*/
	public Page<VolunteerServiceDTO> pageVolunteerRecord(Page<VolunteerServiceDTO> page, Integer fid, Integer uid, Integer formId, String serviceType) {
		// 创建encParamMap, 存储enc加密所需内容
		TreeMap<String, Object> encParamMap = new TreeMap<>();
		LocalDateTime now = LocalDateTime.now();
		String dateFormatStr = now.format(DATE_TIME_FORMATTER);
		encParamMap.put("deptId", fid);
		encParamMap.put("cpage", page.getCurrent());
		encParamMap.put("pageSize", page.getSize());
		encParamMap.put("formId", formId);
		encParamMap.put("datetime", dateFormatStr);
		encParamMap.put("sign", SIGN);
		String enc = getEnc(encParamMap);
		encParamMap.put("enc", enc);
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap();
		paramMap.setAll(encParamMap);
		List<List<WfwFormFilterItemDTO>> filterItems = Lists.newArrayList();
		WfwFormFilterItemDTO uidFilterItem = WfwFormFilterItemDTO.builder().alias("user").compt("belonger").val(JSON.toJSONString(Collections.singleton(uid))).express("===").build();
		filterItems.add(Lists.newArrayList(uidFilterItem));
		if (StringUtils.isNotBlank(serviceType)) {
			WfwFormFilterItemDTO searchTypeFilterItem = WfwFormFilterItemDTO.builder().alias("type").compt("selectbox").val(JSON.toJSONString(Collections.singleton(serviceType))).express("match").build();
			filterItems.add(Lists.newArrayList(searchTypeFilterItem));
		}
		String searchStr = buildSearchStr(filterItems);
		paramMap.add("searchStr", searchStr);

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
						String alias = itemJsonObj.getString("alias");
						if (!values.isEmpty()) {
							JSONObject value = JSON.parseObject(JSON.toJSONString(values.get(0)));
							val = value.getString("val");
						}
						if ("name".equals(alias)) {
							volunteerDTO.setName(val);
						} else if ("type".equals(alias)) {
							volunteerDTO.setType(val);
						}  else if ("department".equals(alias)) {
							volunteerDTO.setDepartment(val);
						} else if ("date".equals(alias)) {
							volunteerDTO.setServiceDate(val);
						} else if ("time_length".equals(alias)) {
							Long timeLength = StringUtils.isBlank(val)? 0L : Long.parseLong(val);
							volunteerDTO.setTimeLength(timeLength);
						} else if ("no".equals(alias)) {
							volunteerDTO.setNo(val);
						} else if ("level".equals(alias)) {
							volunteerDTO.setLevel(val);
						}
					}
					records.add(volunteerDTO);
				}
			}
			page.setTotal(totalRow);
			page.setPages(totalPage);
			page.setRecords(records);
			return page;
		} else {
			String errorMessage = resultObj.getString("msg");
			log.error("获取用户:{}在机构:{}下的表单:{}数据error:{}, url:{}", uid, fid, formId, errorMessage, ADVANCED_SEARCH_URL);
			throw new BusinessException(errorMessage);
		}

	}

	/**构建搜索查询条件字符串
	* @Description
	* @author huxiaolong
	* @Date 2021-05-20 11:18:52
	* @param filters
	* @return java.lang.String
	*/
	private String buildSearchStr(List<List<WfwFormFilterItemDTO>> filters) {
		Map<String, Object> searchMap = Maps.newHashMap();
		searchMap.put("model", 0);
		searchMap.put("filters", filters);
		return JSONObject.toJSONString(searchMap);
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