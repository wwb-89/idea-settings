package com.chaoxing.activity.service.activity.market;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketCreateParamDTO;
import com.chaoxing.activity.dto.activity.market.ActivityMarketUpdateParamDTO;
import com.chaoxing.activity.mapper.ActivityMarketMapper;
import com.chaoxing.activity.model.ActivityMarket;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityMarketHandleService
 * @description
 * @blame wwb
 * @date 2021-04-12 11:13:02
 */
@Slf4j
@Service
public class ActivityMarketHandleService {

	@Resource
	private ActivityMarketMapper activityMarketMapper;

	@Resource
	private ActivityMarketQueryService activityMarketQueryService;


	/**创建活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 16:34:09
	 * @param activityMarketCreateParamDto
	 * @param operateUserDto
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void add(ActivityMarketCreateParamDTO activityMarketCreateParamDto, OperateUserDTO operateUserDto) {
		ActivityMarket activityMarket = activityMarketCreateParamDto.buildActivityMarket();
		activityMarket.perfectCreator(operateUserDto);
		activityMarketMapper.insert(activityMarket);
		// 给市场克隆一个通用模版

	}

	/**更新活动市场
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 16:47:43
	 * @param activityMarketUpdateParamDto
	 * @param operateUserDto
	 * @return void
	*/
	public void update(ActivityMarketUpdateParamDTO activityMarketUpdateParamDto, OperateUserDTO operateUserDto) {
		ActivityMarket activityMarket = activityMarketUpdateParamDto.buildActivityMarket();
		Integer marketId = activityMarket.getId();
		Optional.ofNullable(activityMarketQueryService.getById(marketId)).orElseThrow(() -> new BusinessException("活动市场不存在"));
		activityMarket.updateValidate(operateUserDto);
		activityMarketMapper.update(activityMarket, new LambdaUpdateWrapper<ActivityMarket>()
			.eq(ActivityMarket::getId, activityMarket.getId())
		);
	}

}