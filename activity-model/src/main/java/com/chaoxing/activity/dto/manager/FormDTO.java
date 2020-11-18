package com.chaoxing.activity.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 表单对象
 * @author wwb
 * @version ver 1.0
 * @className FormDTO
 * @description
 * @blame wwb
 * @date 2020-11-18 18:54:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDTO {

	/** 表单id */
	private String id;
	/** 表单名称 */
	private String name;

}