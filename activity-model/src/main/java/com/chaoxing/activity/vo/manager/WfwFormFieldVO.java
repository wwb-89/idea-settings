package com.chaoxing.activity.vo.manager;

import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.util.WfwFormUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

	/** 字段组件 */
	private String compt;

	/** 若为下拉列表，需要提供选项列表 */
	private List<String> options;

	public static WfwFormFieldVO buildFromWfwFormFieldDTO(FormStructureDTO formField) {
		return WfwFormFieldVO.builder()
				.name(formField.getLabel())
				.compt(formField.getCompt())
				.options(WfwFormUtils.getOptionsFormStructure(formField))
				.build();
	}

}