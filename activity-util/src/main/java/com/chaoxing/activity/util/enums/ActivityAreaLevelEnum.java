package com.chaoxing.activity.util.enums;

import com.chaoxing.activity.util.exception.BusinessException;
import lombok.Getter;

import java.util.Objects;

/**活动区域层级
 * @author wwb
 * @version ver 1.0
 * @className ActivityAreaLevelEnum
 * @description
 * @blame wwb
 * @date 2020-11-23 09:15:24
 */
@Getter
public enum ActivityAreaLevelEnum {

	/** 省 */
	PROVINCE("省", "province"),
	CITY("市", "city"),
	COUNTRY("区县", "country");

	private final String name;
	private final String value;

	ActivityAreaLevelEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static ActivityAreaLevelEnum fromValue(String value) {
		ActivityAreaLevelEnum[] values = ActivityAreaLevelEnum.values();
		for (ActivityAreaLevelEnum activityAreaLevelEnum : values) {
			if (Objects.equals(activityAreaLevelEnum.getValue(), value)) {
				return activityAreaLevelEnum;
			}
		}
		throw new BusinessException("未知的活动区域层级");
	}

}
