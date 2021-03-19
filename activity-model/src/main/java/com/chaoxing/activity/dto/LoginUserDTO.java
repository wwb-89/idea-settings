package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className LoginUserDTO
 * @description
 * @blame wwb
 * @date 2020-11-10 15:37:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDTO {

	/** uid */
	private Integer uid;
	/** fid */
	private Integer fid;
	/** 真实姓名 */
	private String realName;
	/** 机构名 */
	private String orgName;
	/** 登录名 */
	private String loginName;
	/** 手机号 */
	private String mobile;

	/** 所属机构列表 */
	private List<OrgDTO> affiliations;

}