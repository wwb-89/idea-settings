package com.chaoxing.activity.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动组件值对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityComponentValueDTO
 * @description
 * @blame wwb
 * @date 2021-07-13 15:30:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityComponentValueDTO {

	/** 主键 */
	private Integer id;
	/** 活动id */
	private Integer activityId;
	/** 模版组件id */
	private Integer templateComponentId;
	/** 模版id */
	private Integer templateId;
	/** 组件id */
	private Integer componentId;
	/** 值 */
	private String value;
	/** 云盘id集合 */
	private String cloudIds;
	/** 模板组件名称 */
	private String templateComponentName;

}
