package com.chaoxing.activity.dto.activity.market;

import com.chaoxing.activity.model.ActivityMarket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**更新活动市场的对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityMarketCreateParamDTO
 * @description
 * @blame wwb
 * @date 2021-07-14 16:08:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityMarketUpdateParamDTO {

	/** 主键 */
	private Integer id;
	/** 市场名称 */
	private String name;
	/** 图标云盘id */
	private String iconCloudId;
	/** 机构id */
	private Integer fid;
	/** 分类id（微服务创建使用） */
	private Integer classifyId;

	public ActivityMarket buildActivityMarket() {
		return ActivityMarket.builder()
				.id(getId())
				.name(getName())
				.iconCloudId(getIconCloudId())
				.fid(getFid())
				.build();
	}

	public ActivityMarketUpdateParamDTO buildFromActivityMarket(ActivityMarket activityMarket) {
		return ActivityMarketUpdateParamDTO.builder()
				.id(activityMarket.getId())
				.name(activityMarket.getName())
				.iconCloudId(activityMarket.getIconCloudId())
				.fid(activityMarket.getFid())
				.build();
	}

}