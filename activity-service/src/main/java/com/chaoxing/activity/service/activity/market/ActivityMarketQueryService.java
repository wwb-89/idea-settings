package com.chaoxing.activity.service.activity.market;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ActivityMarketMapper;
import com.chaoxing.activity.model.ActivityMarket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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


}