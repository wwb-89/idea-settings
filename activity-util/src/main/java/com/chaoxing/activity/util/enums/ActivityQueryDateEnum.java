package com.chaoxing.activity.util.enums;

import com.chaoxing.activity.util.exception.BusinessException;
import lombok.Getter;

import java.util.Objects;

/**活动查询时间枚举
 * @author wwb
 * @version ver 1.0
 * @className ActivityQueryDateEnum
 * @description
 * @blame wwb
 * @date 2020-11-13 11:00:37
 */
@Getter
public enum ActivityQueryDateEnum {

	/** 全部 */
	ALL("全部", ""),
	NEARLY_A_MONTH("近一个月", "nearly_a_month"),
	NEARLY_THREE_MONTH("近三个月", "nearly_three_month"),
	NEARLY_SIX_MONTH("近半年", "nearly_six_month"),
	NEARLY_A_YEAR("近一年", "nearly_a_year"),
	EARLIER("更早", "earlier");

	private String name;
	private String value;

	ActivityQueryDateEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static ActivityQueryDateEnum fromValue(String value) {
		ActivityQueryDateEnum[] values = ActivityQueryDateEnum.values();
		for (ActivityQueryDateEnum activityQueryDateEnum : values) {
			if (Objects.equals(activityQueryDateEnum.getValue(), value)) {
				return activityQueryDateEnum;
			}
		}
		throw new BusinessException("未知的时间");
	}

}