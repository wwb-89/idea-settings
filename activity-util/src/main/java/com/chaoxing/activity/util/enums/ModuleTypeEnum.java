package com.chaoxing.activity.util.enums;

import com.chaoxing.activity.util.exception.BusinessException;
import lombok.Getter;

import java.util.Objects;

/**模块类型枚举
 * @author wwb
 * @version ver 1.0
 * @className ModuleTypeEnum
 * @description
 * @blame wwb
 * @date 2020-11-11 10:38:38
 */
@Getter
public enum ModuleTypeEnum {

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

	ModuleTypeEnum(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public static ModuleTypeEnum fromValue(String value) {
		ModuleTypeEnum[] values = ModuleTypeEnum.values();
		for (ModuleTypeEnum moduleTypeEnum : values) {
			if (Objects.equals(moduleTypeEnum.getValue(), value)) {
				return moduleTypeEnum;
			}
		}
		throw new BusinessException("未知的模块类型");
	}

}