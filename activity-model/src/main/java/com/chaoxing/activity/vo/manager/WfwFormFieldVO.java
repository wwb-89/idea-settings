package com.chaoxing.activity.vo.manager;

import com.chaoxing.activity.dto.manager.form.FormFieldDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**微服务表单字段VO
 * @author wwb
 * @version ver 1.0
 * @className WfwFormFieldVO
 * @description
 * @blame wwb
 * @date 2021-07-08 17:57:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormFieldVO {

	/** 字段名称 */
	private String name;

	public static WfwFormFieldVO buildFromWfwFormFieldDTO(FormFieldDTO formField) {
		return WfwFormFieldVO.builder()
				.name(formField.getLabel())
				.build();
	}

}