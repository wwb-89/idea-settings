package com.chaoxing.activity.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**活动发布范围，报名角色
 * @author wwb
 * @version ver 1.0
 * @className ActivityScopeSignUpRoleDTO
 * @description 活动发布范围与报名指定的角色范围
 * @blame wwb
 * @date 2022-02-16 11:28:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityScopeSignUpRoleDTO {

	/** 活动发布范围机构id列表 */
	private List<Integer> fids;
	/** 活动报名指定的角色限制的角色id列表（多个报名则取并集） */
	private List<Integer> roleIds;

}
