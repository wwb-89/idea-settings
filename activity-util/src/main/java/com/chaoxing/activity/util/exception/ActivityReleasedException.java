package com.chaoxing.activity.util.exception;

import lombok.Getter;

/**活动已发布
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleasedException
 * @description
 * @blame wwb
 * @date 2021-06-09 10:16:50
 */
@Getter
public class ActivityReleasedException extends BusinessException {

	private Integer activityId;

	public ActivityReleasedException(Integer activityId) {
		super("活动已发布");
		this.activityId = activityId;
	}

}
