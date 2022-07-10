package com.chaoxing.activity.dto.activity.classify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className MarketClassifyUpdateParamDTO
 * @description
 * @blame wwb
 * @date 2021-07-19 15:55:59
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketClassifyUpdateParamDTO {

	/** 活动分类id */
	private Integer classifyId;
	/** 分类名称 */
	private String name;
	/** 活动市场id */
	private Integer marketId;

}