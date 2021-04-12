package com.chaoxing.activity.service.activity.market;

import com.chaoxing.activity.model.ActivityMarket;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityMarketValidationService
 * @description
 * @blame wwb
 * @date 2021-04-12 11:13:54
 */
@Slf4j
@Service
public class ActivityMarketValidationService {

	@Resource
	private ActivityMarketQueryService activityMarketQueryService;

	/**活动市场存在
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 11:24:57
	 * @param activityMarketId
	 * @return com.chaoxing.activity.model.ActivityMarket
	*/
	public ActivityMarket exist(Integer activityMarketId) {
		ActivityMarket activityMarket = activityMarketQueryService.getById(activityMarketId);
		Optional.ofNullable(activityMarket).orElseThrow(() -> new BusinessException("活动市场不存在"));
		return activityMarket;
	}



}
