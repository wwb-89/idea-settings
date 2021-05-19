package com.chaoxing.activity.dto.manager.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**表单结构对象
 * @author wwb
 * @version ver 1.0
 * @className FormStructureDTO
 * @description
 * @blame wwb
 * @date 2021-03-09 12:28:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormStructureDTO {

	/** 字段id */
	private Integer id;
	/** 字段名称 */
	private String label;
	/** 别名 */
	private String alias;
	private String compt;
	private List<FormStructureDTO> compts;

}