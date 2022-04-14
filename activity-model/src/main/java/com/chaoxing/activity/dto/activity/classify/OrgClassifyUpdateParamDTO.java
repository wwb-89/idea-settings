package com.chaoxing.activity.dto.activity.classify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrgClassifyUpdateParamDTO
 * @description
 * @blame wwb
 * @date 2021-07-19 15:53:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgClassifyUpdateParamDTO {

	/** 活动分类id */
	private Integer classifyId;
	/** 分类名称 */
	private String name;
	/** 机构id */
	private Integer fid;

}