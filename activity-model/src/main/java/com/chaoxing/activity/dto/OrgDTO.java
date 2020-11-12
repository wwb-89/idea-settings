package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrgDTO
 * @description
 * @blame wwb
 * @date 2020-11-12 19:30:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgDTO {

	/** 机构id */
	private Integer fid;
	/** 机构名称 */
	private String name;

}