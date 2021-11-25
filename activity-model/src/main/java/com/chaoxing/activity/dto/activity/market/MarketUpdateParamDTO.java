package com.chaoxing.activity.dto.activity.market;

import com.chaoxing.activity.model.Market;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

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
public class MarketUpdateParamDTO {

	/** 主键 */
	private Integer id;
	/** 市场名称 */
	private String name;
	/** 图标云盘id */
	private String iconCloudId;
	/** 图标url */
	private String iconUrl;
	/** 机构id */
	private Integer fid;
	/** 分类id（微服务创建使用） */
	private Integer classifyId;
	/** 微服务应用id */
	private Integer wfwAppId;
	/** 是否启用通讯录 */
	private Boolean enableContacts;
	/** 是否启用组织架构 */
	private Boolean enableOrganization;
	/** 是否启用区域架构 */
	private Boolean enableRegional;

	public Market buildActivityMarket() {
		return Market.builder()
				.id(getId())
				.name(getName())
				.iconCloudId(getIconCloudId())
				.iconUrl(getIconUrl())
				.fid(getFid())
				.iconCloudId(getIconCloudId())
				.origin(String.valueOf(getWfwAppId()))
				.enableContacts(getEnableContacts())
				.enableOrganization(getEnableOrganization())
				.enableRegional(getEnableRegional())
				.build();
	}

	public static MarketUpdateParamDTO buildFromActivityMarket(Market market) {
		return MarketUpdateParamDTO.builder()
				.id(market.getId())
				.name(market.getName())
				.iconCloudId(market.getIconCloudId())
				.iconUrl(market.getIconUrl())
				.fid(market.getFid())
				.iconCloudId(market.getIconCloudId())
				.wfwAppId(Optional.ofNullable(market.getOrigin()).filter(StringUtils::isNotBlank).map(Integer::parseInt).orElse(null))
				.enableContacts(market.getEnableContacts())
				.enableOrganization(market.getEnableOrganization())
				.enableRegional(market.getEnableRegional())
				.build();
	}

}