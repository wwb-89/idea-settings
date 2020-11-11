package com.chaoxing.activity.util.enums;

import lombok.Getter;

/**模块
 * @author wwb
 * @version ver 1.0
 * @className ActivityModuleEnum
 * @description
 * @blame wwb
 * @date 2020-11-11 10:38:38
 */
@Getter
public enum ModuleEnum {

	/** 作品征集 */
	WORK("作品征集", "work"),
	/** 星阅读 */
	STAR("星阅读", "star"),
	/** 打卡 */
	PUNCH("打卡", "punch"),
	/** 测评 */
	EVALUATION("测评", "evaluation"),
	/** 听评课 */
	TPK("听评课", "tpk"),
	/** 小组 */
	GROUP("小组", "group"),
	/** 超链接 */
	LINK("超链接", "link");

	private String name;
	private String value;

	ModuleEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}



}