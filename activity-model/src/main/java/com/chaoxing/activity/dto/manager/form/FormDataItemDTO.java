package com.chaoxing.activity.dto.manager.form;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**表单数据项
 * @author wwb
 * @version ver 1.0
 * @className FormDataItemDTO
 * @description
 * @blame wwb
 * @date 2021-08-30 11:06:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDataItemDTO {

	/** value的key */
	private static final String VAL_KEY = "val";
	/** uid key */
	private static final String UID_KEY = "puid";


	/** 字段id */
	private Integer id;
	/** 字段类型 */
	private String compt;
	/** 字段名称 */
	private String label;
	/** 字段别名 */
	private String alias;
	/** 值 */
	private List<JSONObject> values;


	public String getFieldValue(String fieldName) {
		if (CollectionUtils.isEmpty(values)) {
			return null;
		}
		JSONObject jsonObject = values.get(0);
		String value = jsonObject.getString(VAL_KEY);
		if (StringUtils.isBlank(fieldName) || Objects.equals(label, fieldName)) {
			return value;
		}
		return null;
	}

	public Integer getFieldUid(String fieldName) {
		if (CollectionUtils.isEmpty(values)) {
			return null;
		}
		JSONObject jsonObject = values.get(0);
		Integer uid = jsonObject.getInteger(UID_KEY);
		if (StringUtils.isBlank(fieldName) || Objects.equals(label, fieldName)) {
			return uid;
		}
		return null;
	}

}