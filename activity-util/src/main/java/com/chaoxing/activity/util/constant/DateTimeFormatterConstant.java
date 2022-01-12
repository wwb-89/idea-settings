package com.chaoxing.activity.util.constant;

import java.time.format.DateTimeFormatter;

/**
 * @author wwb
 * @version ver 1.0
 * @className DateTimeFormatterConstant
 * @description
 * @blame wwb
 * @date 2020-11-19 19:49:29
 */
public class DateTimeFormatterConstant {

	private DateTimeFormatterConstant() {

	}

	public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static final DateTimeFormatter YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

}