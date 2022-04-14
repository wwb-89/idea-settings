package com.chaoxing.activity.util.exception;

import lombok.Getter;

/**活动未发布
 * @author wwb
 * @version ver 1.0
 * @className ActivityUnReleasedException
 * @description
 * @blame wwb
 * @date 2021-06-09 10:16:50
 */
@Getter
public class ActivityUnReleasedException extends BusinessException {

	private Integer activityId;

	public ActivityUnReleasedException(Integer activityId) {
		super("活动已下架");
		this.activityId = activityId;
	}

}
