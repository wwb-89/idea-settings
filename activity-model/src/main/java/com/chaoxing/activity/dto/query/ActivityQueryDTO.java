package com.chaoxing.activity.dto.query;

import com.chaoxing.activity.util.enums.ActivityQueryDateScopeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**活动查询对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityQueryDTO
 * @description
 * @blame wwb
 * @date 2020-11-13 10:57:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityQueryDTO {

	/** 查询关键字 */
	private String sw;
	/** 活动市场id */
	private Integer marketId;
	/** 置顶的fid（查询的结果该机构的数据靠前） */
	private Integer topFid;
	/** 活动分类id（与活动分类名称互斥） */
	private Integer activityClassifyId;
	/** 活动分类名称（与活动分类id互斥） */
	private String activityClassifyName;
	/** 活动级别分类 */
	private String levelType;
	/** 用户班级id */
	private Integer userClassId;
	/** 时间区间 ActivityQueryDateScopeEnum */
	private String dateScope;
	/** 指定日期 */
	private String date;
	/** 状态 */
	private Integer status;
	/** 活动标示 */
	private String flag;
	/** 状态列表 */
	private List<Integer> statusList;
	/** 区域码 */
	private String areaCode;
	/** 活动类型 */
	private String activityType;
	/** flag的查询范围，0：默认，1：所有 */
	private Integer scope;
	/** 是否只查询能报名的 */
	private Boolean signUpAble;
	/** 标签 */
	private List<String> tags;

	// 非页面传递参数
	/** 标签id列表（根据tags查询数据库来获取） */
	private List<Integer> tagIds;
	/** 参与的fid列表 */
	private List<Integer> fids;
	/** 当前登录的用户uid */
	private Integer currentUid;
	/** 多活动标识 */
	private List<String> flags;
	/** 最小时间 */
	private String minTimeStr;
	/** 最大时间 */
	private String maxTimeStr;

	public void calDateScope() {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String dateScope = Optional.ofNullable(getDateScope()).orElse("");
		ActivityQueryDateScopeEnum activityQueryDateEnum = ActivityQueryDateScopeEnum.fromValue(dateScope);
		LocalDate today = LocalDate.now();
		String minTimeStr;
		String maxTimeStr;
		switch (activityQueryDateEnum) {
			case ALL:
				minTimeStr = "";
				maxTimeStr = "";
				break;
			case NEARLY_A_MONTH:
				minTimeStr = today.atTime(0, 0, 0).plusMonths(-1).format(dateTimeFormatter);
				maxTimeStr = "";
				break;
			case NEARLY_THREE_MONTH:
				minTimeStr = today.atTime(0, 0, 0).plusMonths(-3).format(dateTimeFormatter);
				maxTimeStr = "";
				break;
			case NEARLY_SIX_MONTH:
				minTimeStr = today.atTime(0, 0, 0).plusMonths(-6).format(dateTimeFormatter);
				maxTimeStr = "";
				break;
			case NEARLY_A_YEAR:
				minTimeStr = today.atTime(0, 0, 0).plusYears(-1).format(dateTimeFormatter);
				maxTimeStr = "";
				break;
			case TODAY:
				minTimeStr = today.atTime(0, 0, 0).format(dateTimeFormatter);
				maxTimeStr = today.atTime(23, 59, 59).format(dateTimeFormatter);
				break;
			case TOMORROW:
				minTimeStr = today.atTime(0, 0, 0).plusDays(1).format(dateTimeFormatter);
				maxTimeStr = today.atTime(23, 59, 59).plusDays(1).format(dateTimeFormatter);
				break;
			case WEEKEND:
				// 周末
				minTimeStr = today.with(DayOfWeek.SATURDAY).atTime(0, 0, 0).format(dateTimeFormatter);
				maxTimeStr = today.with(DayOfWeek.SUNDAY).atTime(23, 59, 59).format(dateTimeFormatter);
				break;
			case NEARLY_A_WEEK:
				minTimeStr = today.atTime(0,0,0).format(dateTimeFormatter);
				maxTimeStr = today.atTime(23,59,59).plusWeeks(1).format(dateTimeFormatter);
				break;
			case SPECIFIED:
				String date = getDate();
				if (StringUtils.isNotBlank(date)) {
					LocalDate specifiedDate = LocalDate.parse(date, dateFormatter);
					minTimeStr = specifiedDate.atTime(0, 0, 0).format(dateTimeFormatter);
					maxTimeStr = specifiedDate.atTime(23, 59, 59).format(dateTimeFormatter);
				} else {
					minTimeStr = "";
					maxTimeStr = "";
				}
				break;
			default:
				// 更早
				minTimeStr = "";
				maxTimeStr = today.atTime(0, 0, 0).plusYears(-1).format(dateTimeFormatter);
		}
		setMinTimeStr(minTimeStr);
		setMaxTimeStr(maxTimeStr);
	}

}