package com.chaoxing.activity.dto.query;

import com.chaoxing.activity.util.enums.OrderTypeEnum;
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

	/** 当前机构fid */
	private Integer fid;
	/** 当前市场Id */
	private Integer marketId;
	/** 活动状态 */
	private Integer status;
	/** 创建活动的fid列表 */
	private List<Integer> fids;
	/** 置顶fid */
	private Integer topFid;
	/** 关键字 */
	private String sw;
	/** 是不是严格模式， 严格模式：只显示自己创建的活动 */
	private Integer strict;
	/** 活动标识 */
	private String activityFlag;
	/** 活动分类id */
	private Integer activityClassifyId;
	/** 创建人id */
	private Integer createUid;
	/** 创建wfwfid */
	private Integer createWfwfid;
	/** 排序字段id */
	private Integer orderFieldId;
	/** 排序字段code */
	private String orderField;
	/** 排序方式*/
	private OrderTypeEnum orderType;
	/** 图书馆使用的code */
	private String code;
}