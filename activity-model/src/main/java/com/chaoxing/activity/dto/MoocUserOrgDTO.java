package com.chaoxing.activity.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className MoocUserOrgDTO
 * @description
 * @blame wwb
 * @date 2020-11-12 14:15:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoocUserOrgDTO {

	/** 角色id */
	@JSONField(name = "roleids")
	private String roleIds;
	/** 登录名 */
	@JSONField(name = "loginname")
	private String loginName;
	/** 手机号 */
	@JSONField(name = "phone")
	private String phone;
	/** fid */
	@JSONField(name = "schoolid")
	private Integer fid;
	/** 用户uid */
	@JSONField(name = "id")
	private Integer id;
	/** 用户真实姓名 */
	@JSONField(name = "username")
	private String realName;

}