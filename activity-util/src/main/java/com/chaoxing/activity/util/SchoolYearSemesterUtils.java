package com.chaoxing.activity.util;

import java.time.LocalDateTime;

/**学期学年工具类
 * @author wwb
 * @version ver 1.0
 * @className SemesterSchoolYearUtils
 * @description
 * @blame wwb
 * @date 2021-03-11 17:23:26
 */
public class SchoolYearSemesterUtils {

	/** 一年中的第一个月 */
	private static final int FIRST_MONTH = 1;
	/** 一年中的最后一个月 */
	private static final int LAST_MONTH = 12;
	/** 上学期分割月 */
	private static final int LAST_SEMESTER_SPLIT_MONTH = 3;
	/** 下学期分割月 */
	private static final int NEXT_SEMESTER_SPLIT_MONTH = 9;
	/** 学期分割日 */
	private static final int SEMESTER_SPLIT_DAY = 1;

	private static final int INIT_ZERO = 0;

	/** 学年 */
	private static final String SCHOOL_YEAR = "学年";
	/** 上学期 */
	private static final String LAST_SEMESTER = "上学期";
	/** 下学期 */
	private static final String NEXT_SEMESTER = "下学期";

	private SchoolYearSemesterUtils() {
		
	}

	/**当前时间的学期开始时间
	* @Description 
	* @author huxiaolong
	* @Date 2021-05-31 16:24:06
	* @param 
	* @return java.time.LocalDateTime
	*/
	public static LocalDateTime currentSemesterStartTime() {
		LocalDateTime current = LocalDateTime.now();
		int year = current.getYear();
		int monthValue = current.getMonthValue();
		int dayOfMonth = current.getDayOfMonth();
		// 1 - 9 月， 属于上一学年，当前年-1
		if (monthValue < NEXT_SEMESTER_SPLIT_MONTH) {
			year -= 1;
		}
		// 3 - 9 月属于下一学期
		if (monthValue >= LAST_SEMESTER_SPLIT_MONTH && monthValue < NEXT_SEMESTER_SPLIT_MONTH) {
			// 下学期
			return LocalDateTime.of(year, LAST_SEMESTER_SPLIT_MONTH, SEMESTER_SPLIT_DAY, INIT_ZERO, INIT_ZERO, INIT_ZERO);
		} else {
			// 上学期
			return LocalDateTime.of(year, NEXT_SEMESTER_SPLIT_MONTH, SEMESTER_SPLIT_DAY, INIT_ZERO, INIT_ZERO, INIT_ZERO);
		}
	}
}