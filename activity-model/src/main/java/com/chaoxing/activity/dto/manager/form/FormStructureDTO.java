package com.chaoxing.activity.dto.manager.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
 * @date 2021-09-01 09:54:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormStructureDTO {

	/** 字段id */
	private Integer id;
	/** 字段别名 */
	private String alias;
	/** 字段类型 */
	private String compt;
	/** 字段名称 */
	private String label;
	/** 字段预设属性对象 */
	private JSONObject field;
	/** 字段格式 */
	private JSONArray values;
	/** 嵌套表格 */
	private List<FormStructureDTO> compts;

}