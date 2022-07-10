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
 * @className MarketCreateParamDTO
 * @description
 * @blame wwb
 * @date 2021-07-14 16:08:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketCreateParamDTO {

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
	/** 来源类型 */
	private String originType;
	/** 来源 */
	private String origin;

	public Market buildActivityMarket() {
		return Market.builder()
				.name(getName())
				.iconCloudId(getIconCloudId())
				.iconUrl(getIconUrl())
				.fid(getFid())
				.enableContacts(getEnableContacts())
				.enableOrganization(getEnableOrganization())
				.enableRegional(getEnableRegional())
				.originType(getOriginType())
				.origin(getOrigin())
				.build();
	}

	public static MarketCreateParamDTO buildSystem(Integer fid, String flag) {
		Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(flag);
		return MarketCreateParamDTO.builder()
				.name(activityFlagEnum.getName())
				.iconCloudId(DEFAULT_ICON_CLOUD_ID)
				.fid(fid)
				.activityFlag(flag)
				.enableContacts(false)
				.enableOrganization(true)
				.enableRegional(false)
				.originType(Market.OriginTypeEnum.SYSTEM.getValue())
				.build();
	}

	public static MarketCreateParamDTO buildFromWfw(Integer fid, Integer classifyId, String flag, String origin) {
		Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(flag);
		return MarketCreateParamDTO.builder()
				.name(activityFlagEnum.getName())
				.iconCloudId(DEFAULT_ICON_CLOUD_ID)
				.fid(fid)
				.classifyId(classifyId)
				.activityFlag(flag)
				.enableContacts(false)
				.enableOrganization(true)
				.enableRegional(false)
				.originType(Market.OriginTypeEnum.WFW.getValue())
				.origin(origin)
				.build();
	}

	public static MarketCreateParamDTO buildFromWfwForm(Integer fid, String flag, Integer formId) {
		Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(flag);
		return MarketCreateParamDTO.builder()
				.name(activityFlagEnum.getName())
				.iconCloudId(DEFAULT_ICON_CLOUD_ID)
				.fid(fid)
				.activityFlag(flag)
				.enableContacts(false)
				.enableOrganization(true)
				.enableRegional(false)
				.originType(Market.OriginTypeEnum.WFW_FORM.getValue())
				.origin(String.valueOf(formId))
				.build();
	}

}