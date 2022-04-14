package com.chaoxing.activity.dto.manager.wfw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**微服务角色对象
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
	/** 角色 */
	private Integer role;
	/** 角色名称 */
	private String name;
	/** 是否是组 */
	private Boolean group;
	/** 所属组id */
	private Integer roleGroupId;

}