package com.chaoxing.activity.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动管理查询对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityManageQueryDTO
 * @description
 * @blame wwb
 * @date 2020-11-18 13:57:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityManageQueryDTO {

	/** 活动状态 */
	private Integer status;
	/** 创建人id */
	private Integer createUid;

}