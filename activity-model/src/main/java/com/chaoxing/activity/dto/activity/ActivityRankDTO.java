package com.chaoxing.activity.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动排行对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityRankDTO
 * @description
 * @blame wwb
 * @date 2022-01-19 16:29:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRankDTO {

	/** 活动id */
	private Integer id;
	/** 活动名称 */
	private String name;
	/** 排行字段数量 */
	private Integer num;

}