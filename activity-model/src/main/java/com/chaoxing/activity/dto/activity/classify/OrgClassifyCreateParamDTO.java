package com.chaoxing.activity.dto.activity.classify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrgClassifyCreateParamDTO
 * @description
 * @blame wwb
 * @date 2021-07-19 15:52:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgClassifyCreateParamDTO {

	/** 分类名称 */
	private String name;
	/** 机构id */
	private Integer fid;

	public static OrgClassifyCreateParamDTO build(String name, Integer fid) {
		return OrgClassifyCreateParamDTO.builder()
				.name(name)
				.fid(fid)
				.build();
	}

}