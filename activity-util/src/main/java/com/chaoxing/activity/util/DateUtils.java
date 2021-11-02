package com.chaoxing.activity.util;

import com.chaoxing.activity.util.constant.CommonConstant;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

	public static final String DATE_MINUTE_STRING = "yyyy-MM-dd HH:mm";
	public static final String FULL_DATE_MINUTE_STRING = "yyyy-MM-dd HH:mm:ss";
	public static final DateTimeFormatter DAY_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
	public static final DateTimeFormatter FULL_TIME_FORMATTER = DateTimeFormatter.ofPattern(FULL_DATE_MINUTE_STRING);
	public static final DateTimeFormatter DATE_MINUTE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
	public static final DateTimeFormatter MONTH_DAY_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM.dd HH:mm");

	public static final String WAVE_SEPARATOR = "~";

	public static final String MIDDLE_LINE_SEPARATOR = "-";

	private DateUtils() {

	}
	
	/**时间戳转换成时间
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-22 16:47:30
	 * @param timestamp
	 * @return java.time.LocalDateTime
	*/
	public static LocalDateTime timestamp2Date(Long timestamp) {
		if (timestamp == null) {
			return null;
		}
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), CommonConstant.DEFAULT_ZONEOFFSET);
	}
	/**开始时间戳转换为localDateTime
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-02 15:30:42
	 * @param timestamp
	 * @return java.time.LocalDateTime
	 */
	public static LocalDateTime startTimestamp2Time(Long timestamp) {
		if (timestamp == null) {
			return null;
		}
		String timeFormatStr = timestamp2Format(timestamp, DATE_MINUTE_STRING) + ":00";
		return LocalDateTime.parse(timeFormatStr, DateTimeFormatter.ofPattern(FULL_DATE_MINUTE_STRING));
	}

	/**开始时间戳转换为localDateTime
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-02 15:30:51
	 * @param timestamp
	 * @return java.time.LocalDateTime
	 */
	public static LocalDateTime endTimestamp2Time(Long timestamp) {
		if (timestamp == null) {
			return null;
		}
		String timeFormatStr = timestamp2Format(timestamp, DATE_MINUTE_STRING) + ":59";
		return LocalDateTime.parse(timeFormatStr, DateTimeFormatter.ofPattern(FULL_DATE_MINUTE_STRING));
	}

	/**时间戳格式化字符串
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-02 15:31:04
	 * @param timestamp
	 * @param format
	 * @return java.lang.String
	 */
	private static String timestamp2Format(Long timestamp, String format) {
		if (timestamp == null) {
			return null;
		}
		Date date = new Date(timestamp);
		SimpleDateFormat sf = new SimpleDateFormat(format);
		return sf.format(date);
	}

	/**时间转换成时间戳
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-22 16:50:00
	 * @param time
	 * @return long
	*/
	public static Long date2Timestamp(LocalDateTime time) {
		if (time == null) {
			return null;
		}
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

	/**活动时间范围时间范围
	 * @Description 同年的只显示月日
	 * @author wwb
	 * @Date 2021-06-04 10:46:40
	 * @param startTime 开始时间
	 * @param endTime 结束书剑
	 * @return java.lang.String
	*/
	public static String activityTimeScope(LocalDateTime startTime, LocalDateTime endTime) {
		return activityTimeScope(startTime, endTime, WAVE_SEPARATOR);
	}

	public static String activityTimeScope(LocalDateTime startTime, LocalDateTime endTime, String separator) {
		String activityTimeScope = "";
		LocalDateTime now = LocalDateTime.now();
		int year = now.getYear();
		int startYear = startTime == null ? year : startTime.getYear();
		int endYear = endTime == null ? year : endTime.getYear();
		boolean isTheSameYear = year == startYear && year == endYear;
		DateTimeFormatter dateTimeFormatter;
		if (isTheSameYear) {
			dateTimeFormatter = MONTH_DAY_TIME_FORMATTER;
		} else {
			dateTimeFormatter = DATE_MINUTE_TIME_FORMATTER;
		}
		if (startTime != null) {
			activityTimeScope = startTime.format(dateTimeFormatter);
		}
		if (StringUtils.isNotBlank(activityTimeScope) && endTime != null) {
			activityTimeScope += " " + separator + " ";
		}
		if (endTime != null) {
			activityTimeScope += endTime.format(dateTimeFormatter);
		}
		return activityTimeScope;
	}

}