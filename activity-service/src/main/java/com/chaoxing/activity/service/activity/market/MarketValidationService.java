package com.chaoxing.activity.service.activity.market;

import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className MarketValidationService
 * @description
 * @blame wwb
 * @date 2021-04-12 11:13:54
 */
@Slf4j
@Service
public class MarketValidationService {

	@Resource
	private MarketQueryService marketQueryService;

	/**活动市场存在
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 11:24:57
	 * @param marketId
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	public Market exist(Integer marketId) {
		Market activityMarket = marketQueryService.getById(marketId);
		Optional.ofNullable(activityMarket).orElseThrow(() -> new BusinessException("活动市场不存在"));
		return activityMarket;
	}

	/**是否有活动市场的全县
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-27 14:42:58
	 * @param marketId
	 * @param operateUserDto
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	public Market manageAble(Integer marketId, OperateUserDTO operateUserDto) {
		Market activityMarket = exist(marketId);
		if (!Objects.equals(activityMarket.getFid(), operateUserDto.getFid())) {
			throw new BusinessException("无权限");
		}
		return activityMarket;
	}

}
