package com.chaoxing.activity.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
	/** 参与的fid列表 */
	private List<Integer> fids;
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
	/** 多活动标识 */
	private List<String> flags;
	/** 时间区间 */
	private String dateScope;
	/** 时间 */
	private String date;
	/** 状态 */
	private Integer status;
	/** 活动标示 */
	private String flag;
	/** 状态列表 */
	private List<Integer> statusList;

	/** 最小日期 */
	private String minDateStr;
	/** 最大日期 */
	private String maxDateStr;

	// 定制
	/** 区域码 */
	private String areaCode;

	/** 活动类型 */
	private String activityType;
	/** flag的查询范围，0：默认，1：所有 */
	private Integer scope;
	/** 是否只查询能报名的 */
	private Boolean signUpAble;
	/** 当前登录的用户uid */
	private Integer currentUid;

}