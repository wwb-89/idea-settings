package com.chaoxing.activity.util.enums;

import lombok.Getter;

/**
 * @author wwb
 * @version ver 1.0
 * @className StatusCodeEnum
 * @description
 * @blame wwb
 * @date 2019-10-22 15:22:56
 */
@Getter
public enum StatusCodeEnum {

	/** 成功 */
	SUCCESS(true, 1, "成功"),
	/** 失败 */
	FAIL(false, 0, "失败"),
	/** 错误 */
	ERROR(false, -1, "错误");

	private boolean success;
	private int code;
	private String message;

	StatusCodeEnum(boolean success, int code, String message) {
		this.success = success;
		this.code = code;
		this.message = message;
	}

}
