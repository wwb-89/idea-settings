package com.chaoxing.activity.dto.activity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**用户参与的活动对象
 * @author wwb
 * @version ver 1.0
 * @className UserParticipateActivityDTO
 * @description
 * @blame wwb
 * @date 2021-06-06 19:50:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserParticipateActivityDTO {

	/** 活动id */
	private Integer id;
	/** 活动名称 */
	private String name;
	/** 封面云盘id */
	@JSONField(serialize = false)
	private String coverCloudId;
	/** 封面地址 */
	private String coverUrl;
	/** 开始时间 */
	private Long startTime;
	/** 结束时间 */
	private Long endTime;
	/** 活动形式 */
	private String activityType;
	/** 活动分类 */
	private String activityClassify;
	/** 活动地址 */
	private String address;
	/** 是否报名 */
	private Boolean signedUp;
	/** 报名时间 */
	private Long signedUpTime;
	/** 签到时间 */
	private Integer signedInNum;
	/** 签到率 */
	private BigDecimal signedInRate;
	/** 参与时长 */
	private Integer participateTimeLength;
	/** 是否已评价 */
	private Boolean haveRating;
	/** 是否合格 */
	private Boolean qualified;
	/** 获得积分 */
	private BigDecimal integral;
	/** 更新时间 */
	private Long updateTime;
	/** 总得分 */
	private BigDecimal totalScore;

}