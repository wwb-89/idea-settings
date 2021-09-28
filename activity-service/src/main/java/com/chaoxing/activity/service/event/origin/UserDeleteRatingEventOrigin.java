package com.chaoxing.activity.service.event.origin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**用户删除评价事件源
 * @author wwb
 * @version ver 1.0
 * @className UserDeleteRatingEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 14:57:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeleteRatingEventOrigin {

	private Integer activityId;
	private Integer uid;
	private Long timestamp;

}