package com.chaoxing.activity.util.enums;

import lombok.Getter;

import java.util.Objects;

/**用户行为枚举
 * @author wwb
 * @version ver 1.0
 * @className UserActionEnum
 * @description
 * @blame wwb
 * @date 2021-06-16 11:28:18
 */
@Getter
public enum UserActionEnum {

	/** 报名 */
	SIGNED_UP("报名", "signed_up", "报名成功获得", UserActionTypeEnum.SIGN_UP, true, true),
	CANCEL_SIGNED_UP("取消报名", "cancel_signed_up", "取消报名扣除", UserActionTypeEnum.SIGN_UP, true, true),
	NOT_SIGNED_UP("未报名", "not_signed_up", "未报名扣除", UserActionTypeEnum.SIGN_UP, true, true),
	SIGNED_IN("签到", "signed_in", "每次签到获得", UserActionTypeEnum.SIGN_IN, true, true),
	CANCEL_SIGNED_IN("取消签到", "cancel_signed_in", "取消签到扣除", UserActionTypeEnum.SIGN_IN, true, true),
	NOT_SIGNED_IN("未签", "not_signed_in", "每次未签扣除", UserActionTypeEnum.SIGN_IN, true, true),
	LEAVE_SIGNED_IN("请假", "leave_signed_in", "每次请假扣除", UserActionTypeEnum.SIGN_IN, true, true),

	SIGNED_OUT("签退", "signed_out", "每次签退获得", UserActionTypeEnum.SIGN_OUT, true, true),
	CANCEL_SIGNED_OUT("取消签退", "cancel_signed_out", "取消签退扣除", UserActionTypeEnum.SIGN_OUT, true, true),
	NOT_SIGNED_OUT("未签", "not_signed_out", "每次未签扣除", UserActionTypeEnum.SIGN_OUT, true, true),
	LEAVE_SIGNED_OUT("请假", "leave_signed_out", "每次请假扣除", UserActionTypeEnum.SIGN_OUT, true, true),


	RATING("评价", "rating", "提交评价获得", UserActionTypeEnum.RATING, false, false),
	NOT_RATING("未评价", "not_rating", "未评价扣除", UserActionTypeEnum.RATING, false, false),
	DELETE_RATING("删除评价", "delete_rating", "删除评价扣除", UserActionTypeEnum.RATING, false, false),
	PUBLISH_TOPIC("发帖", "publish_topic", "每次发帖获得", UserActionTypeEnum.DISCUSS, true, true),
	DELETE_TOPIC("删帖", "delete_topic", "删帖帖子扣除", UserActionTypeEnum.DISCUSS, true, true),
	NOT_PUBLISH_TOPIC("未发帖", "not_publish_topic", "未发帖扣除", UserActionTypeEnum.DISCUSS, true, true),
	REPLY_TOPIC("回帖", "reply_topic", "每次回帖获得", UserActionTypeEnum.DISCUSS, true, true),
	DELETE_REPLY("删除回复", "delete_reply", "删除回复扣除", UserActionTypeEnum.DISCUSS, true, true),
	SUBMIT_WORK("提交作品", "submit_work", "每次提交获得", UserActionTypeEnum.WORK, true, true),
	NOT_SUBMIT_WORK("未提交作品", "not_submit_work", "未提交扣除", UserActionTypeEnum.WORK, true, true),
	DELETE_WORK("删除作品", "delete_work", "删除作品扣除", UserActionTypeEnum.WORK, true, true),
	PERFORMANCE("表现", "performance", "上限", UserActionTypeEnum.PERFORMANCE, false, false),
	DELETE_PERFORMANCE("删除表现", "delete_performance", "上限", UserActionTypeEnum.PERFORMANCE, false, false);

	private final String name;
	private final String value;
	private final String description;
	private final UserActionTypeEnum userActionType;
	private final Boolean multiple;
	private final Boolean enableUpperLimit;

	UserActionEnum(String name, String value, String description, UserActionTypeEnum userActionType, Boolean multiple, Boolean enableUpperLimit) {
		this.name = name;
		this.value = value;
		this.description = description;
		this.userActionType = userActionType;
		this.multiple = multiple;
		this.enableUpperLimit = enableUpperLimit;
	}

	public static UserActionEnum fromValue(String value) {
		UserActionEnum[] values = UserActionEnum.values();
		for (UserActionEnum userActionEnum : values) {
			if (Objects.equals(userActionEnum.getValue(), value)) {
				return userActionEnum;
			}
		}
		return null;
	}

}