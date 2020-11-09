package com.chaoxing.activity.util.exception;

import lombok.Getter;

/**
 * @author wwb
 * @version ver 1.0
 * @className BusinessException
 * @description
 * @blame wwb
 * @date 2020-11-09 11:06:39
 */
@Getter
public class BusinessException extends RuntimeException {

	public BusinessException(String message) {
		super(message);
	}

}