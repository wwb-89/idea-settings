package com.chaoxing.activity.util.enums;

import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**活动查询时间区间枚举
 * @author wwb
 * @version ver 1.0
 * @className ActivityQueryDateScopeEnum
 * @description
 * @blame wwb
 * @date 2020-11-13 11:00:37
 */
@Getter
public enum ActivityQueryDateScopeEnum {

	/** 浙图 */
	ALL("全部", ""),
	NEARLY_A_MONTH("近一个月", "nearly_a_month"),
	NEARLY_THREE_MONTH("近三个月", "nearly_three_month"),
	NEARLY_SIX_MONTH("近半年", "nearly_six_month"),
	NEARLY_A_YEAR("近一年", "nearly_a_year"),
	EARLIER("更早", "earlier"),

	/** 通用 */
	TODAY("今天", "today"),
	TOMORROW("明天", "tomorrow"),
	WEEKEND("周末", "weekend"),
	NEARLY_A_WEEK("最近一周", "nearly_a_week"),
	SPECIFIED("选择日期", "specified");

	private final String name;
	private final String value;

	ActivityQueryDateScopeEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static ActivityQueryDateScopeEnum fromValue(String value) {
		ActivityQueryDateScopeEnum[] values = ActivityQueryDateScopeEnum.values();
		for (ActivityQueryDateScopeEnum activityQueryDateEnum : values) {
			if (Objects.equals(activityQueryDateEnum.getValue(), value)) {
				return activityQueryDateEnum;
			}
		}
		throw new BusinessException("未知的时间");
	}

	public static List<ActivityQueryDateScopeEnum> listZjLib() {
		return Lists.newArrayList(ALL, NEARLY_A_MONTH, NEARLY_THREE_MONTH, NEARLY_SIX_MONTH, NEARLY_A_YEAR, EARLIER);
	}

	public static List<ActivityQueryDateScopeEnum> listUniversal() {
		return Lists.newArrayList(ALL, TODAY, TOMORROW, WEEKEND, NEARLY_A_WEEK, SPECIFIED);
	}

}