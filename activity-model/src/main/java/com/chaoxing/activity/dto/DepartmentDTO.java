package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**部门对象
 * @author wwb
 * @version ver 1.0
 * @className DepartmentDTO
 * @description
 * @blame wwb
 * @date 2021-08-31 14:19:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

	/** 部门id */
	private Integer id;
	/** 部门名称 */
	private String name;

}