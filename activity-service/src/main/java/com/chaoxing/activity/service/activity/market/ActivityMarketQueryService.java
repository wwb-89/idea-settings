package com.chaoxing.activity.service.activity.market;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.dto.activity.market.ActivityMarketUpdateParamDTO;
import com.chaoxing.activity.mapper.ActivityMarketMapper;
import com.chaoxing.activity.model.ActivityMarket;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityMarketQueryService
 * @description
 * @blame wwb
 * @date 2021-04-12 11:12:46
 */
@Slf4j
@Service
public class ActivityMarketQueryService {

	@Resource
	private ActivityMarketMapper activityMarketMapper;
	
	/**根据id查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 11:23:54
	 * @param activityMarketId
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	public ActivityMarket getById(Integer activityMarketId) {
		return activityMarketMapper.selectOne(new LambdaQueryWrapper<ActivityMarket>()
				.eq(ActivityMarket::getId, activityMarketId)
				.eq(ActivityMarket::getDeleted, false)
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
		List<ActivityMarket> markets = activityMarketMapper.selectList(new LambdaQueryWrapper<ActivityMarket>()
				.eq(ActivityMarket::getWfwAppId, wfwAppId)
		);
		if (CollectionUtils.isEmpty(markets)) {
			throw new BusinessException("活动市场不存在");
		}
		ActivityMarket market = markets.get(0);
		return ActivityMarketUpdateParamDTO.buildFromActivityMarket(market);
	}

}