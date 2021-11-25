package com.chaoxing.activity.dto.manager.wfw;

import com.chaoxing.activity.model.Market;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**微服务应用参数对象
 * @author wwb
 * @version ver 1.0
 * @className WfwAppParamDTO
 * @description
 * @blame wwb
 * @date 2021-07-16 16:00:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwAppParamDTO {

	/** 应用id */
	private Integer id;
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
	/** 微信端应用地址 */
	private String wechatUrl;

	public static WfwAppParamDTO buildFromActivityMarket(Market market, Integer classifyId) {
		return WfwAppParamDTO.builder()
				.id(Optional.ofNullable(market.getOrigin()).filter(StringUtils::isNotBlank).map(Integer::parseInt).orElse(null))
				.name(market.getName())
				.iconUrl(market.getIconUrl())
				.classifyId(classifyId)
				.fid(market.getFid())
				.appUrl(market.buildAppUrl())
				.pcUrl(market.buildPcUrl())
				.adminUrl(market.buildMarketManageUrl())
				.wechatUrl(market.buildAppUrl())
				.build();
	}

}