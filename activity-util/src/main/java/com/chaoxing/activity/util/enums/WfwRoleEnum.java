package com.chaoxing.activity.util.enums;

import com.chaoxing.activity.util.exception.BusinessException;
import lombok.Getter;

/**
 * @author wwb
 * @version ver 1.0
 * @className WfwRoleEnum
 * @description
 * @blame wwb
 * @date 2020-11-12 20:04:45
 */
@Getter
public enum WfwRoleEnum {

	// 学生
	STUDENT("学生", 3),
	TEACHER("教师", 1),
	MANAGER("管理员", 7);


	private String name;
	private Integer value;

	WfwRoleEnum(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	public static WfwRoleEnum fromValue(Integer value) {
		WfwRoleEnum[] values = WfwRoleEnum.values();
		for (WfwRoleEnum roleEnum : values) {
			if (roleEnum.getValue().equals(value)) {
				return roleEnum;
			}
		}
		throw new BusinessException("未知的角色id:" + value);
	}

}