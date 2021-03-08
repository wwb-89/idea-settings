package com.chaoxing.activity.util.exception;

import lombok.Getter;

/**
 * @author wwb
 * @version ver 1.0
 * @className WxAccessException
 * @description
 * @blame wwb
 * @date 2021-03-05 16:40:50
 */
@Getter
public class WxAccessException extends BusinessException {

	public WxAccessException() {
		super("不支持微信端访问");
	}

}