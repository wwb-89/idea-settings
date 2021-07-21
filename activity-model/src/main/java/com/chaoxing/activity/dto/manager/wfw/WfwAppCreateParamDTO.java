package com.chaoxing.activity.dto.manager.wfw;

import com.chaoxing.activity.model.ActivityMarket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**微服务应用创建参数对象
 * @author wwb
 * @version ver 1.0
 * @className WfwAppCreateParamDTO
 * @description
 * @blame wwb
 * @date 2021-07-16 16:00:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwAppCreateParamDTO {

	/** 应用名称 */
	private String name;
	/** 应用图标url */
	private String iconUrl;
	/** 分类id（微服务创建应用跳转携带） */
	private Integer classifyId;
	/** 机构id（微服务创建应用跳转携带） */
	private Integer fid;
	/** 应用移动端地址 */
	private String appUrl;
	/** 应用pc端地址 */
	private String pcUrl;
	/** 应用管理端地址 */
	private String adminUrl;

	public static WfwAppCreateParamDTO buildFromActivityMarket(ActivityMarket activityMarket, Integer classifyId) {
		return WfwAppCreateParamDTO.builder()
				.name(activityMarket.getName())
				.iconUrl(activityMarket.getIconUrl())
				.classifyId(classifyId)
				.fid(activityMarket.getFid())
				.appUrl(activityMarket.buildAppUrl())
				.pcUrl(activityMarket.buildPcUrl())
				.adminUrl(activityMarket.buildMarketmanageUrl())
				.build();
	}

}