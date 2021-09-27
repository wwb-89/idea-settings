package com.chaoxing.activity.util.enums;

import lombok.Getter;

/**事件枚举
 * @author wwb
 * @version ver 1.0
 * @className EventEnum
 * @description 所有的事件源的类型
 * 比如：
 * 活动新增
 * 活动发布
 * 活动下架
 * 活动删除
 * 活动即将开始
 * 活动开始
 * 活动即将结束
 * 活动结束
 * 活动修改
 * 活动名称改变
 * 活动时间改变
 * 活动地点改变
 * 开启报名
 * 关闭报名
 * 报名即将开始
 * 报名即将结束
 * 新增签到
 * 删除签到
 * 用户成功报名
 * 用户成功取消报名
 * 用户成功签到
 * 用户成功取消签到（管理员改为未签）
 * 用户成功请假签到
 * 用户成功签退
 * 用户成功取消签退（管理员改为未签）
 * 用户成功签退请假
 * 用户成功添加评价
 * 用户成功删除评价
 * @blame wwb
 * @date 2021-09-24 19:35:05
 */
@Getter
public enum EventEnum {

	/** 新增活动 */
	ADD_ACTIVITY("新增活动", "add_activity"),
	RELEASE_ACTIVITY("发布活动", "release_activity"),
	CANCEL_RELEASE_ACTIVITY("取消发布活动", "cancel_release_activity"),
	DELETE_ACTIVITY("删除活动", "delete_activity"),
	ACTIVITY_ABOUT_START("活动即将开始", "activity_about_start"),
	ACTIVITY_START("活动开始", "activity_start"),
	ACTIVITY_ABOUT_END("活动即将结束", "activity_about_end"),
	ACTIVITY_END("活动结束", "activity_end"),
	ACTIVITY_CHANGE("活动修改", "activity_change"),
	ACTIVITY_NAME_CHANGE("活动名称改变", "activity_name_changed"),
	ACTIVITY_TIME_CHANGE("活动时间改变", "activity_time_change"),
	ACTIVITY_ADDRESS_CHANGE("活动地点改变", "activity_address_change"),
	OPEN_SIGN_UP("开启报名", "open_sign_up"),
	CLOSE_SIGN_UP("关闭报名", "close_sign_up"),
	SIGN_UP_ABOUT_START("报名即将开始", "sign_up_about_start"),
	SIGN_UP_ABOUT_END("报名即将结束", "sign_up_about_end"),
	ADD_SIGN_IN("新增签到", "add_sign_in"),
	DELETE_SIGN_IN("删除签到", "delete_sign_in"),
	USER_SIGNED_UP("用户成功报名", "user_signed_up"),
	USER_CANCEL_SIGN_UP("用户取消报名", "user_cancel_sign_up"),
	USER_SIGNED_IN("用户已签到", "user_signed_in"),
	USER_CANCEL_SIGN_IN("用户取消签到（未签）", "user_cancel_sign_in"),
	USER_LEAVE_SIGN_IN("用户请假签退", "user_leave_sign_in"),
	USER_SIGNED_OUT("用户已签退", "user_signed_out"),
	USER_CANCEL_SIGN_OUT("用户取消签退（未签）", "user_cancel_sign_out"),
	USER_LEAVE_SIGN_OUT("用户请假签退", "user_leave_sign_out"),
	USER_ADD_RATING("用户新增评价", "user_add_rating"),
	USER_DELETE_RATING("用户删除评价", "user_delete_rating");

	private final String name;
	private final String code;

	EventEnum(String name, String code) {
		this.name = name;
		this.code = code;
	}

}