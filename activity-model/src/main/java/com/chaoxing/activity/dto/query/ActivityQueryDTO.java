package com.chaoxing.activity.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	/** 参与用户fid */
	private Integer fid;
	/** 分类id */
	private Integer activityClassifyId;
	/** 时间 */
	private String date;
	/** 区域 */
	private String area;
	/** 状态 */
	private Integer status;

	/** 最小日期 */
	private String minDateStr;
	/** 最大日期 */
	private String maxDateStr;

}