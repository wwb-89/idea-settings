package com.chaoxing.activity.dto.manager.wfwform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
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

}