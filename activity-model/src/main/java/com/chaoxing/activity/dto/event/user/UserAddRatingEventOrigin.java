package com.chaoxing.activity.dto.event.user;

import com.chaoxing.activity.dto.event.AbstractEventOrigin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**用户新增评价事件源
 * @author wwb
 * @version ver 1.0
 * @className UserAddRatingEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 14:56:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddRatingEventOrigin extends AbstractEventOrigin {

	private Integer activityId;
	private Integer uid;
	/** 事件发生的事件（时间戳） */
	private Long timestamp;

}