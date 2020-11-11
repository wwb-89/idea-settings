package com.chaoxing.activity.util.enums;

import lombok.Getter;

/**通用状态
 * @author wwb
 * @version ver 1.0
 * @className ModelGeneralStatusEnum
 * @description
 * @blame wwb
 * @date 2020-11-10 17:06:49
 */
@Getter
public enum ModelGeneralStatusEnum {

	/** 有效 */
	EFFECTIVE("有效", 1),
	/** 无效 */
	INVALID("无效", 0);

	private String name;
	private Integer value;

	ModelGeneralStatusEnum(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

}
