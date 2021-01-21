package com.chaoxing.activity.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动封面url同步队列实体
 * @author wwb
 * @version ver 1.0
 * @className ActivityCoverUrlSyncDTO
 * @description
 * @blame wwb
 * @date 2021-01-20 10:45:59
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCoverUrlSyncDTO {
	
	/** 活动id */
	private Integer activityId;
	/** 云盘id */
	private String cloudId;
	
}
