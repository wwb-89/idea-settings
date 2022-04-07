package com.chaoxing.activity.util;

import lombok.Getter;

import java.time.LocalDateTime;

/**活动状态工具类
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatusUtils
 * @description
 * @blame wwb
 * @date 2022-04-07 16:25:25
 */
public class ActivityStatusUtils {

	private ActivityStatusUtils() {

	}

	@Getter
	public enum StatusEnum {

		/** 报名中 */
		SIGN_UP_ONGOING("报名中", "sign_up_ongoing"),
		ABOUT_TO_START("即将开始", "about_to_start"),
		ENDED("已结束", "ended"),
		ONGOING("进行中", "ongoing");

		private final String name;
		private final String value;

		StatusEnum(String name, String value) {
			this.name = name;
			this.value = value;
		}

	}

	/**计算活动的显示状态
	 * @Description
	 * @author wwb
	 * @Date 2022-04-07 16:37:47
	 * @param activityStartTime
	 * @param activityEndTime
	 * @param signUpStartTime
	 * @param signUpEndTime
	 * @return com.chaoxing.activity.util.ActivityStatusUtils.StatusEnum
	*/
	public static StatusEnum calActivityShowStatus(LocalDateTime activityStartTime, LocalDateTime activityEndTime, LocalDateTime signUpStartTime, LocalDateTime signUpEndTime) {
		LocalDateTime now = LocalDateTime.now();
		boolean signUpInProcess = false;
		if (signUpStartTime != null && signUpEndTime != null) {
			signUpInProcess = now.isAfter(signUpStartTime) && now.isBefore(signUpEndTime);
		}
		// 没有报名
		if (now.isBefore(activityStartTime)) {
			if (signUpInProcess) {
				return StatusEnum.SIGN_UP_ONGOING;
			}
			return StatusEnum.ABOUT_TO_START;
		}
		if (now.isAfter(activityEndTime)) {
			return StatusEnum.ENDED;
		}
		return StatusEnum.ONGOING;
	}

}