package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**模块
 * @author wwb
 * @version ver 1.0
 * @className ModuleDTO
 * @description
 * @blame wwb
 * @date 2020-11-11 10:49:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDTO {

	/** 名称 */
	private String name;
	/** 值 */
	private String value;

}