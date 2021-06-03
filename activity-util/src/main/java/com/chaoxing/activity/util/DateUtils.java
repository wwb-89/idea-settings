package com.chaoxing.activity.util;

import com.chaoxing.activity.util.constant.CommonConstant;
import org.apache.commons.compress.utils.Lists;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className DateUtils
 * @description
 * @blame wwb
 * @date 2020-12-22 16:45:38
 */
public class DateUtils {

	public static final DateTimeFormatter DAY_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter FULL_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static final DateTimeFormatter DATE_MINUTE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

	/**列出每一天
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-15 19:51:32
	 * @param startTime
	 * @param endTime
	 * @return java.util.List<java.lang.String>
	*/
	public static List<String> listEveryDay(LocalDateTime startTime, LocalDateTime endTime) {
		return listEveryDay(startTime.toLocalDate(), endTime.toLocalDate());
	}

	/**列出每一天
	* @Description
	* @author huxiaolong
	* @Date 2021-05-11 16:10:05
	* @param startDate
	* @param endDate
	* @return java.util.List<java.lang.String>
	*/
	public static List<String> listEveryDay(LocalDate startDate, LocalDate endDate) {
		List<String> result = Lists.newArrayList();
		while (endDate.compareTo(startDate) >= 0) {
			result.add(startDate.format(DAY_DATE_TIME_FORMATTER));
			startDate = startDate.plusDays(1);
		}
		return result;
	}

}