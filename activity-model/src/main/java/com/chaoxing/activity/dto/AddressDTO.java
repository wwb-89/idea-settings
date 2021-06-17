package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author wwb
 * @version ver 1.0
 * @className AddressDTO
 * @description
 * @blame wwb
 * @date 2021-06-15 01:40:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

	/** 地点 */
	private String address;
	/** 经度 */
	private BigDecimal lng;
	/** 纬度 */
	private BigDecimal lat;

}
