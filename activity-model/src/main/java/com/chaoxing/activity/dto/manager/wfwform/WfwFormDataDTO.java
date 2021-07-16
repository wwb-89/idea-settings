package com.chaoxing.activity.dto.manager.wfwform;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**表单数据对象
 * @author wwb
 * @version ver 1.0
 * @className WfwFormDataDTO
 * @description
 * @blame wwb
 * @date 2021-03-09 12:17:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormDataDTO {

	/** value的key */
	private static final String VAL_KEY = "val";
	/** uid key */
	private static final String UID_KEY = "puid";

	private Integer id;
	private String label;
	private String compt;
	private String alias;
	private List<JSONObject> values;
	private List<WfwFormDataDTO> compts;

	/**获取字段的值
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-12 10:49:27
	 * @param fieldName
	 * @return java.lang.String
	*/
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