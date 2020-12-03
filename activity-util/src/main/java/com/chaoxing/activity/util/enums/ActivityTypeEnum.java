package com.chaoxing.activity.util.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityTypeEnum
 * @description
 * @blame wwb
 * @date 2020-11-13 17:43:42
 */
@Getter
public enum ActivityTypeEnum {

	/** 线上活动 */
	ONLINE("线上活动", "online"),
	OFFLINE("线下活动", "offline");

	private String name;
	private String value;

	ActivityTypeEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static ActivityTypeEnum fromValue(String value) {
		ActivityTypeEnum[] values = ActivityTypeEnum.values();
		for (ActivityTypeEnum activityTypeEnum : values) {
			if (Objects.equals(activityTypeEnum.getValue(), value)) {
				return activityTypeEnum;
			}
		}
		return null;
	}

}