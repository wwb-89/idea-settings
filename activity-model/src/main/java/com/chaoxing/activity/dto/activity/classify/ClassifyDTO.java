package com.chaoxing.activity.dto.activity.classify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className ClassifyDTO
 * @description
 * @blame wwb
 * @date 2021-07-19 15:58:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassifyDTO {

	/** id */
	private Integer id;
	/** 分类名称 */
	private String name;

}
