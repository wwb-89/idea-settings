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

	/** 签到 */
	SIGNED_IN("签到", "signed_in", "每次签到获得", true, true),
	RATING("评价", "rating", "提交评价获得", false, false),
	PUBLISH("发帖", "publish", "每次发帖获得", true, true),
	REPLY("回帖", "reply", "每次回帖获得", true, true),
	WORK("提交作品", "work", "每次提交获得", true, true),
	PERFORMANCE("表现", "performance", "上限", false, false);

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