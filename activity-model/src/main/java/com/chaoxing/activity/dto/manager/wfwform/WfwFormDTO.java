package com.chaoxing.activity.dto.manager.wfwform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/** 表单对象
 * @author wwb
 * @version ver 1.0
 * @className WfwFormDTO
 * @description
 * @blame wwb
 * @date 2020-11-18 18:54:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormDTO {

	private Integer formId;
	private String appName;
	private Integer aprvStatusTypeId;
	private String formName;
	private Integer aprvStatus;
	private Integer formUserId;
	private String mobile;
	private String organize;
	private Integer uid;
	private String uname;
	private Integer fid;
	private Long inserttime;
	private Long updatetime;
	private List<WfwFormDataDTO> formData;

	private Integer version;
	private Integer anonymous;
	private Integer datasource;

	public String getFieldValue(String fieldName) {
		if (CollectionUtils.isEmpty(formData)) {
			return null;
		}
		for (WfwFormDataDTO formDatum : formData) {
			String fieldValue = formDatum.getFieldValue(fieldName);
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
		for (WfwFormDataDTO formDatum : formData) {
			Integer uid = formDatum.getFieldUid(fieldName);
			if (uid != null) {
				return uid;
			}
		}
		return null;
	}

	public static List<WfwFormDTO> buildFromAdvanceResult(JSONArray resJsonArray) {
		List<WfwFormDTO> result = Lists.newArrayList();
		resJsonArray.forEach(v -> {
			JSONObject formItem = (JSONObject) v;
			WfwFormDTO wfwForm = JSON.parseObject(formItem.toJSONString(), WfwFormDTO.class);
			wfwForm.setFid(formItem.getInteger("deptId"));
			wfwForm.setFormUserId(formItem.getInteger("id"));
			JSONArray formIdValueData = JSON.parseArray(JSON.toJSONString(formItem.getJSONObject("formIdValueData").values()));
			List<WfwFormDataDTO> wfwFormDataList = Lists.newArrayList();
			formIdValueData.forEach(v1 -> {
				JSONObject formDataItem = (JSONObject) v1;
				WfwFormDataDTO wfwFormData = JSON.parseObject(formDataItem.toJSONString(), WfwFormDataDTO.class);
				String valuesJson = JSON.parseObject(JSON.toJSONString(formDataItem.getJSONArray("groupValues").get(0))).getJSONArray("values").toJSONString();
				JSONArray valuesArray = JSONArray.parseArray(valuesJson).getJSONArray(0);
				List<JSONObject> resItemList = Lists.newArrayList();
				valuesArray.forEach(v2 -> {
					JSONObject o = (JSONObject) v2;
					resItemList.add(o);
				});
				wfwFormData.setValues(resItemList);
				wfwFormDataList.add(wfwFormData);
			});
			wfwForm.setFormData(wfwFormDataList);
			result.add(wfwForm);
		});
		return result;

	}


}