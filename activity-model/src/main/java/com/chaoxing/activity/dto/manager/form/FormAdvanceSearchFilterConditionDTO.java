package com.chaoxing.activity.dto.manager.form;

import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**表单高级查询过滤条件对象
 * @author wwb
 * @version ver 1.0
 * @className FormAdvanceSearchFilterConditionDTO
 * @description
 * @blame wwb
 * @date 2021-08-30 19:24:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormAdvanceSearchFilterConditionDTO {

	public static final DateTimeFormatter DATE_TIME_RANGE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	/** 或且标记， 0-且， 1-或 */
	private Integer model;
	private List<List<Filter>> filters;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Filter {

		private Integer id;
		private String alias;
		private String compt;
		private String express;
		private Object val;
		private List<Range> range;

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		public static class Range {

			private String express;
			private String val;

		}

		@Getter
		public enum ExpressEnum {

			EQ("等于", "==="),
			NE("不等于", "!=="),
			IS_NULL("为空", "null"),
			NOT_NULL("不为空", "notNull"),
			LIKE("包含", "like"),
			NOT_LIKE("不包含", "notLike"),
			GT("大于", ">"),
			GE("大于等于", ">="),
			LT("小于", "<"),
			LE("小于等于", "<="),
			RANGE("选择范围", "><"),
			MATCH("等于任意一个", "match"),
			NOT_MATCH("不等于任意一个", "notMatch"),
			ALL_MATCH("同时包含", "allMatch");

			private final String name;
			private final String value;

			ExpressEnum(String name, String value) {
				this.name = name;
				this.value = value;
			}
		}
	}

	@Getter
	public enum ModelEnum {

		/** 且 */
		AND("且", 0),
		OR("且", 1);

		private final String name;
		private final Integer value;

		ModelEnum(String name, Integer value) {
			this.name = name;
			this.value = value;
		}

	}

}