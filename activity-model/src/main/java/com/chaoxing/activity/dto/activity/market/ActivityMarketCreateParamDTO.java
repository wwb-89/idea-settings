package com.chaoxing.activity.dto.activity.market;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Market;
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

	private static final String DEFAULT_ICON_CLOUD_ID = "4b7269d11dcf4465ca533d2cfaf1d70e";

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
	/** 活动标识 */
	private String activityFlag;
	/** 是否启用通讯录 */
	private Boolean enableContacts;
	/** 是否启用组织架构 */
	private Boolean enableOrganization;
	/** 是否启用区域架构 */
	private Boolean enableRegional;

	public Market buildActivityMarket() {
		return Market.builder()
				.name(getName())
				.iconCloudId(getIconCloudId())
				.iconUrl(getIconUrl())
				.fid(getFid())
				.enableContacts(getEnableContacts())
				.enableOrganization(getEnableOrganization())
				.enableRegional(getEnableRegional())
				.build();
	}

	public static ActivityMarketCreateParamDTO build(Integer fid, Integer classifyId, String flag) {
		Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(flag);
		return ActivityMarketCreateParamDTO.builder()
				.name(activityFlagEnum.getName())
				.iconCloudId(DEFAULT_ICON_CLOUD_ID)
				.fid(fid)
				.classifyId(classifyId)
				.activityFlag(flag)
				.enableContacts(true)
				.enableOrganization(true)
				.enableRegional(true)
				.build();
	}

	public static ActivityMarketCreateParamDTO build(String name, Integer fid, String activityFlag) {
		ActivityMarketCreateParamDTO activityMarketCreateParam = ActivityMarketCreateParamDTO.build(fid, null, activityFlag);
		activityMarketCreateParam.setName(name);
		return activityMarketCreateParam;
	}

}