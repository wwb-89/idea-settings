package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className PassportUserDTO
 * @description
 * @blame wwb
 * @date 2020-11-12 19:29:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassportUserDTO {

	/** uid */
	private String uid;
	/** 用户名（登录名） */
	private String loginName;
	/** 真实姓名 */
	private String realName;
	/** 手机号 */
	private String mobile;
	/** 邮箱 */
	private String email;
	/** 所属机构列表 */
	private List<OrgDTO> affiliations;

}