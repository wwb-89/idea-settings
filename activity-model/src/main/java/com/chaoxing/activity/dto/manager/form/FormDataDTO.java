package com.chaoxing.activity.dto.manager.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.chaoxing.activity.dto.DepartmentDTO;
import com.chaoxing.activity.util.FormUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**表单数据对象
 * @author wwb
 * @version ver 1.0
 * @className FormDataDTO
 * @description
 * @blame wwb
 * @date 2021-08-30 11:10:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDataDTO {

	private Integer fid;
	private Integer formId;
	private String formName;
	private String appName;
	private Integer formUserId;
	private Integer uid;
	private String uname;
	/** ==== 活动表单审核相关字段 ==== */
	private Integer aprvStatusTypeId;
	private Integer aprvStatus;
	private String mobile;
	private String organize;
	/** ==== 活动表单审核相关字段 ==== */
	@JSONField(name = "inserttime")
	private Long insertTime;
	@JSONField(name = "updatetime")
	private Long updateTime;
	private List<FormDataItemDTO> formData;

	public static FormDataDTO buildFromAdvanceSearchResult(JSONObject formDataJsonObject) {
		FormDataDTO formDataDto = new FormDataDTO();
		formDataDto.setFid(formDataJsonObject.getInteger("deptId"));
		formDataDto.setFormId(formDataJsonObject.getInteger("formId"));
		formDataDto.setFormUserId(formDataJsonObject.getInteger("id"));
		formDataDto.setUid(formDataJsonObject.getInteger("uid"));
		formDataDto.setUname(formDataJsonObject.getString("uname"));
		formDataDto.setInsertTime(formDataJsonObject.getLong("inserttime"));
		formDataDto.setUpdateTime(formDataJsonObject.getLong("updatetime"));
		List<FormDataItemDTO> formData = Lists.newArrayList();
		formDataDto.setFormData(formData);
		JSONObject item = formDataJsonObject.getJSONObject("formIdValueData");
		Set<Map.Entry<String, Object>> entries = item.entrySet();
		for (Map.Entry<String, Object> entry : entries) {
			String key = entry.getKey();
			JSONObject jsonObject = item.getJSONObject(key);
			List<JSONObject> values = Lists.newArrayList();
			JSONArray groupValues = jsonObject.getJSONArray("groupValues");
			if (groupValues.size() > 0) {
				JSONArray valuesJsonArray = groupValues.getJSONObject(0).getJSONArray("values");
				if (valuesJsonArray.size() > 0) {
					valuesJsonArray = valuesJsonArray.getJSONArray(0);
					for (int i = 0; i < valuesJsonArray.size(); i++) {
						values.add(valuesJsonArray.getJSONObject(i));
					}
				}
			}
			formData.add(FormDataItemDTO.builder()
					.id(jsonObject.getInteger("id"))
					.alias(jsonObject.getString("alias"))
					.compt(jsonObject.getString("compt"))
					.values(values)
					.build());
		}

		return formDataDto;
	}

	public static List<FormDataDTO> buildFromAdvanceSearchResult(JSONArray formDataJsonArray) {
		List<FormDataDTO> formDataDtos = Lists.newArrayList();
		if (formDataJsonArray != null) {
			for (int i = 0; i < formDataJsonArray.size(); i++) {
				JSONObject jsonObject = formDataJsonArray.getJSONObject(i);
				FormDataDTO formDataDto = buildFromAdvanceSearchResult(jsonObject);
				if (formDataDto != null) {
					formDataDtos.add(formDataDto);
				}
			}
		}
		return formDataDtos;
	}

	public String getStringValue(String alias) {
		return FormUtils.getValue(this, alias);
	}

	public Integer getIntegerValue(String alias) {
		String value = FormUtils.getValue(this, alias);
		if (StringUtils.isNotBlank(value)) {
			return Integer.parseInt(value);
		}
		return null;
	}

	public Boolean getBooleanValue(String alias) {
		String value = FormUtils.getValue(this, alias);
		if (StringUtils.isNotBlank(value)) {
			return Boolean.parseBoolean(value);
		}
		return false;
	}

	public LocalDateTime getTimeAliasValue(String alias) {
		return FormUtils.getTime(this, alias);
	}

	public Long getLongValue(String alias) {
		String value = FormUtils.getValue(this, alias);
		return StringUtils.isNotBlank(value) ? Long.parseLong(value) : 0L;
	}

	public FormUserDTO getUserAliasValue(String alias) {
		return FormUtils.getUser(this, alias);
	}

	public DepartmentDTO getDepartmentAliasValue(String alias) {
		return FormUtils.getDepartment(this, alias);
	}

	public FormImageDTO getImage(String alias) {
		return FormUtils.getImage(this, alias);
	}

	public String getValueByAlias(String aliasName) {
		if (CollectionUtils.isEmpty(formData)) {
			return null;
		}
		for (FormDataItemDTO formDatum : formData) {
			String fieldValue = formDatum.getValueByAlias(aliasName);
			if (StringUtils.isNotBlank(fieldValue)) {
				return fieldValue;
			}
		}
		return null;
	}

	public Integer getFieldUid(String fieldName) {
		if (CollectionUtils.isEmpty(formData)) {
			return null;
		}
		for (FormDataItemDTO formDatum : formData) {
			Integer uid = formDatum.getFieldUid(fieldName);
			if (uid != null) {
				return uid;
			}
		}
		return null;
	}

	public Integer getUidByAlias(String alias) {
		if (CollectionUtils.isEmpty(formData)) {
			return null;
		}
		for (FormDataItemDTO formDatum : formData) {
			Integer uid = formDatum.getAliasUid(alias);
			if (uid != null) {
				return uid;
			}
		}
		return null;
	}


	/**在表单记录中，根据用户别名alias，查询出用户uid对应的表单记录
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-05 11:31:19
	 * @param wfwFormData
	 * @param fieldAlias
	 * @param uid
	 * @return java.util.List<com.chaoxing.activity.dto.manager.form.FormDataDTO>
	 */
	public static List<FormDataDTO> listUserFormData(List<FormDataDTO> wfwFormData, String fieldAlias, Integer uid) {
		List<FormDataDTO> userFormData = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(wfwFormData)) {
			// 根据fieldName找到字段id
			if (StringUtils.isNotBlank(fieldAlias)) {
				for (FormDataDTO wfwFormDatum : wfwFormData) {
					Integer userId = wfwFormDatum.getUidByAlias(fieldAlias);
					if (Objects.equals(uid, userId)) {
						userFormData.add(wfwFormDatum);
					}
				}
			}
		}
		return userFormData;
	}

}