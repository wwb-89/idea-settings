package com.chaoxing.activity.dto.manager.wfwform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**表单字段对象
 * @author wwb
 * @version ver 1.0
 * @className WfwFormFieldDTO
 * @description
 * @blame wwb
 * @date 2021-03-09 12:28:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormFieldDTO {

	/** 字段id */
	private Integer id;
	/** 字段名称 */
	private String label;
	/** 别名 */
	private String alias;
	private String compt;
	private List<WfwFormFieldDTO> compts;

}