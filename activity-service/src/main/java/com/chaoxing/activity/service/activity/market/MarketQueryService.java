package com.chaoxing.activity.service.activity.market;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.dto.activity.market.ActivityMarketUpdateParamDTO;
import com.chaoxing.activity.mapper.MarketMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className MarketQueryService
 * @description
 * @blame wwb
 * @date 2021-04-12 11:12:46
 */
@Slf4j
@Service
public class MarketQueryService {

	@Resource
	private MarketMapper marketMapper;

	@Resource
	private TemplateQueryService templateQueryService;
	
	/**根据id查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 11:23:54
	 * @param marketId
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	public Market getById(Integer marketId) {
		return marketMapper.selectOne(new LambdaQueryWrapper<Market>()
				.eq(Market::getId, marketId)
				.eq(Market::getDeleted, false)
		);
	}

	/**根据微服务应用id查询活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-21 14:33:14
	 * @param wfwAppId
	 * @return com.chaoxing.activity.dto.activity.market.ActivityMarketUpdateParamDTO
	*/
	public ActivityMarketUpdateParamDTO getByWfwAppId(Integer wfwAppId) {
		List<Market> markets = marketMapper.selectList(new LambdaQueryWrapper<Market>()
				.eq(Market::getWfwAppId, wfwAppId)
		);
		if (CollectionUtils.isEmpty(markets)) {
			throw new BusinessException("活动市场不存在");
		}
		Market market = markets.get(0);
		return ActivityMarketUpdateParamDTO.buildFromActivityMarket(market);
	}

	/**查询机构下的活动市场列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-02 19:15:25
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.Market>
	*/
	public List<Market> listByFid(Integer fid) {
		return marketMapper.selectList(new LambdaQueryWrapper<Market>()
				.eq(Market::getFid, fid)
				.eq(Market::getDeleted, false)
		);
	}

	/**查询机构下的对应活动的活动市场列表id
	 * @Description
	 * @author wwb
	 * @Date 2021-08-02 19:15:25
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.Market>
	*/
	public List<Integer> listMarketIdsByActivityIdFid(Integer fid, Integer activityId) {
		return marketMapper.listMarketIdsByActivityIdFid(fid, activityId);
	}

	/**通过fid，活动标识查询模板，根据模板获取市场id
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-09-01 11:52:12
	 * @param fid
	 * @param flag
	 * @return java.lang.Integer
	 */
	public Integer getMarketIdByFlag(Integer fid, String flag) {
		Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(flag);
		if (activityFlagEnum == null) {
			return null;
		}
		Template template = templateQueryService.getOrgTemplateByActivityFlag(fid, activityFlagEnum);
		return Optional.ofNullable(template).map(Template::getMarketId).orElse(null);
	}

}