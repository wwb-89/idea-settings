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

	/** 活动相关 */
	ACTIVITY_ABOUT_END("活动即将结束", "activity_about_end"),
	ACTIVITY_ABOUT_START("活动即将开始", "activity_about_start"),
	ACTIVITY_ADDRESS_TIME_CHANGE("活动地址时间改变", "activity_address_time_change"),
	ACTIVITY_CANCEL_RELEASE("活动取消发布", "activity_cancel_release"),
	ACTIVITY_CHANGE("活动改变", "activity_change"),
	ACTIVITY_COVER_CHANGE("活动封面改变", "activity_cover_change"),
	ACTIVITY_DELETED("活动删除", "activity_deleted"),
	ACTIVITY_END("活动结束", "activity_end"),
	ACTIVITY_END_TIME_REACH("活动结束时间要到了", "activity_end_time_reach"),
	ACTIVITY_INTEGRAL_CHANGE("活动积分改变", "activity_integral_change"),
	ACTIVITY_NAME_CHANGE("活动名称改变", "activity_name_change"),
	ACTIVITY_NAME_TIME_CHANGE("活动名称时间改变", "activity_name_time_change"),
	ACTIVITY_RELEASE("活动发布", "activity_release"),
	ACTIVITY_START_TIME_REACH("活动开始时间要到了", "activity_start_time_reach"),
	ACTIVITY_TIME_CHANGE("活动时间改变", "activity_time_change"),
	ACTIVITY_WEB_TEMPLATE_CHANGE("活动门户模版改变", "activity_web_template_change"),
	/** 报名签到相关 */
	SIGN_CHANGE("报名签到改变", "sign_change"),
	SIGN_IN_ADD("新增签到", "sign_in_add"),
	SIGN_IN_DELETED("删除签到", "sign_in_deleted"),
	SIGN_UP_ADD("新增报名", "sign_up_add"),
	SIGN_UP_DELETED("删除报名", "sign_up_deleted"),
	/** 用户相关 */
	USER_ADD_RATING("用户新增评价", "user_add_rating"),
	USER_CANCEL_SIGN_IN("用户取消签到", "user_cancel_sign_in"),
	USER_CANCEL_SIGN_OUT("用户取消签退", "user_cancel_sign_out"),
	USER_CANCEL_SIGN_UP("用户取消报名", "user_cancel_sign_up"),
	USER_DELETE_RATING("用户删除评价", "user_delete_rating"),
	USER_LEAVE_SIGN_IN("用户签到请假", "user_leave_sign_in"),
	USER_LEAVE_SIGN_OUT("用户签退请假", "user_leave_sign_out"),
	USER_SIGNED_IN("用户签到", "user_signed_in"),
	USER_SIGNED_OUT("用户签退", "user_signed_out"),
	USER_SIGNED_UP("用户报名", "user_signed_up");
	

	private final String name;
	private final String code;

	EventEnum(String name, String code) {
		this.name = name;
		this.code = code;
	}

}