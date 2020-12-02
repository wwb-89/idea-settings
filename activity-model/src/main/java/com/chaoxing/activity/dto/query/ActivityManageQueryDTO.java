package com.chaoxing.activity.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
	/** 创建者uid */
	private Integer createUid;
	/** 创建活动的fid列表 */
	private List<Integer> fids;
	/** 置顶fid */
	private Integer topFid;

}