package com.chaoxing.activity.util.enums;

import lombok.Getter;

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
	SIGNED_UP("报名", "signed_up", "报名成功获得", true, true),
	CANCEL_SIGNED_UP("取消报名", "cancel_signed_up", "取消报名扣除", true, true),
	NOT_SIGNED_UP("未报名", "not_signed_up", "未报名扣除", true, true),
	SIGNED_IN("签到", "signed_in", "每次签到获得", true, true),
	CANCEL_SIGNED_IN("取消签到", "cancel_signed_in", "取消签到扣除", true, true),
	NOT_SIGNED_IN("未签", "not_signed_in", "每次未签扣除", true, true),
	LEAVE("请假", "leave", "每次请假扣除", true, true),
	RATING("评价", "rating", "提交评价获得", false, false),
	NOT_RATING("未评价", "not_rating", "未评价扣除", false, false),
	DELETE_RATING("删除评价", "delete_rating", "删除评价扣除", false, false),
	PUBLISH_TOPIC("发帖", "publish_topic", "每次发帖获得", true, true),
	NOT_PUBLISH_TOPIC("未发帖", "not_publish_topic", "未发帖扣除", true, true),
	REPLY_TOPIC("回帖", "reply_topic", "每次回帖获得", true, true),
	DELETE_TOPIC("删帖", "delete_topic", "删帖帖子扣除", true, true),
	DELETE_REPLY("删除回复", "delete_reply", "删除回复扣除", true, true),
	SUBMIT_WORK("提交作品", "submit_work", "每次提交获得", true, true),
	NOT_SUBMIT_WORK("未提交作品", "not_submit_work", "未提交扣除", true, true),
	DELETE_WORK("删除作品", "delete_work", "删除作品扣除", true, true),
	PERFORMANCE("表现", "performance", "上限", false, false),
	DELETE_PERFORMANCE("删除表现", "delete_performance", "上限", false, false);

	private String name;
	private String value;
	private String description;
	private Boolean multiple;
	private Boolean enableUpperLimit;

	UserActionEnum(String name, String value, String description, Boolean multiple, Boolean enableUpperLimit) {
		this.name = name;
		this.value = value;
		this.description = description;
		this.multiple = multiple;
		this.enableUpperLimit = enableUpperLimit;
	}

}