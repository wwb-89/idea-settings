package com.chaoxing.activity.util.exception;

import lombok.Getter;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityNotExistException
 * @description
 * @blame wwb
 * @date 2021-03-08 18:17:47
 */
@Getter
public class ActivityNotExistException extends BusinessException{

	private Integer activityId;

	public ActivityNotExistException(Integer activityId) {
		super("活动不存在");
		this.activityId = activityId;
	}

}
