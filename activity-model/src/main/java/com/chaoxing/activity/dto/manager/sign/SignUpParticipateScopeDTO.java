package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**报名参与范围对象
 * @author wwb
 * @version ver 1.0
 * @className SignUpParticipateScopeDTO
 * @description
 * @blame wwb
 * @date 2021-07-12 16:11:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpParticipateScopeDTO {

	/** 报名id */
	private Integer signUpId;
	/** 外资源部id */
	private Integer externalId;
	/** 外部资源父id */
	private Integer externalPid;
	/** 外部资源名称 */
	private String externalName;
	/** 是不是叶子结点 */
	private Boolean leaf;
	/** 组织架构类型 */
	private String groupType;

}