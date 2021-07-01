package com.chaoxing.activity.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	/** 活动状态 */
	private Integer status;
	/** 创建活动的fid列表 */
	private List<Integer> fids;
	/** 活动状态 */
	private String statuses;
	/** 活动状态列表 */
	private List<Integer> statusList;
	/** 置顶fid */
	private Integer topFid;
	/** 关键字 */
	private String sw;
	/** 是不是严格模式， 严格模式：只显示自己创建的活动 */
	private Integer strict;
	/** 创建人id */
	private Integer createUid;
	/** 创建wfwfid */
	private Integer createWfwfid;

	public List<Integer> getStatusList() {
		if (StringUtils.isBlank(statuses)) {
			return null;
		}
		return Arrays.stream(statuses.split(",")).map(Integer::valueOf).collect(Collectors.toList());
	}
}