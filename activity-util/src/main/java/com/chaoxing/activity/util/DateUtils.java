package com.chaoxing.activity.util;

import com.chaoxing.activity.util.constant.CommonConstant;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author wwb
 * @version ver 1.0
 * @className DateUtils
 * @description
 * @blame wwb
 * @date 2020-12-22 16:45:38
 */
public class DateUtils {

	private DateUtils() {

	}
	
	/**时间戳转换成时间
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-22 16:47:30
	 * @param timestamp
	 * @return java.time.LocalDateTime
	*/
	public static LocalDateTime timestamp2Date(long timestamp) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), CommonConstant.DEFAULT_ZONEOFFSET);
	}

	/**时间转换成时间戳
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-22 16:50:00
	 * @param time
	 * @return long
	*/
	public static long date2Timestamp(LocalDateTime time) {
		return time.toInstant(CommonConstant.DEFAULT_ZONEOFFSET).toEpochMilli();
	}

}