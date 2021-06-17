package com.chaoxing.activity.dto.manager.uc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**用户组织架构对象
 * @author wwb
 * @version ver 1.0
 * @className UserOrganizationalStructureDTO
 * @description
 * @blame wwb
 * @date 2021-05-24 11:15:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganizationalStructureDTO {

	private String fid;
	private List<String> group4Name;
	/** 角色id，多个角色id之间以逗号隔开 */
	private String role;
	private Integer schoolType;
	private List<String> group3Name;
	private String userid;
	private List<String> group5Name;
	private String dxfid;
	private Integer isDeleted;
	private String areaName;
	private String alias;
	private Integer personid;
	private List<String> group2Name;
	private Integer isCertify;
	private String aliasName;
	private Long createtime;
	private Long currLoginTime;
	private Integer facestatus;
	private List<Integer> group4;
	private List<Integer> group3;
	private List<String> group1Name;
	private List<Integer> group5;
	private List<Integer> group2;
	private String realname;
	private List<Integer> group1;
	private Integer areaId;
	private String phone;
	private String username;
	private Integer isLeaveSchool;
	
}