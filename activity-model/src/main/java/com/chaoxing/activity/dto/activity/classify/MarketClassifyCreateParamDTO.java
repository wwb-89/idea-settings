package com.chaoxing.activity.dto.activity.classify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className MarketClassifyCreateParamDTO
 * @description
 * @blame wwb
 * @date 2021-07-19 15:55:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketClassifyCreateParamDTO {

	/** 分类名称 */
	private String name;
	/** 活动市场id */
	private Integer marketId;

}