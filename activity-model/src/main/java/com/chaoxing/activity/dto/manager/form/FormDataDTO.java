package com.chaoxing.activity.dto.manager.form;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**表单数据对象
 * @author wwb
 * @version ver 1.0
 * @className FormDataDTO
 * @description
 * @blame wwb
 * @date 2021-03-09 12:17:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDataDTO {

	private Integer id;
	private String label;
	private String compt;
	private List<JSONObject> values;

}