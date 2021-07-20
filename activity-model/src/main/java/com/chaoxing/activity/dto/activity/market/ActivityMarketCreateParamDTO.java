package com.chaoxing.activity.dto.activity.market;

import com.chaoxing.activity.model.ActivityMarket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**创建活动市场的对象
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
public class ActivityMarketCreateParamDTO {

	/** 市场名称 */
	private String name;
	/** 图标云盘id */
	private String iconCloudId;
	/** icon url */
	private String iconUrl;
	/** 机构id */
	private Integer fid;
	/** 分类id（微服务创建使用） */
	private Integer classifyId;

	public ActivityMarket buildActivityMarket() {
		return ActivityMarket.builder()
				.name(getName())
				.iconCloudId(getIconCloudId())
				.iconUrl(getIconUrl())
				.fid(getFid())
				.build();
	}

	public static ActivityMarketCreateParamDTO build(String name, String iconCloudId, Integer fid) {
		return ActivityMarketCreateParamDTO.builder()
				.name(name)
				.iconCloudId(iconCloudId)
				.fid(fid)
				.build();
	}

}