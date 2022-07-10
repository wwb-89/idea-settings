package com.chaoxing.activity.dto.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className PunchFormDTO
 * @description
 * @blame wwb
 * @date 2020-11-11 10:15:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PunchFormDTO {

	/** 名称 */
	private String name;
	/** 需要发表动态。0：否，1：是 */
	private Boolean needPubDynamic;
	/** 创建人id */
	private Integer createUid;

}