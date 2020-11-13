package com.chaoxing.activity.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className WfwRoleDTO
 * @description
 * @blame wwb
 * @date 2020-11-12 19:53:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwRoleDTO {

	/** 角色id */
	private Integer id;
	/** 角色名称 */
	private String name;

}