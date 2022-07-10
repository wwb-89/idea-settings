package com.chaoxing.activity.service.blacklist;

import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.service.activity.market.MarketValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className BlacklistValidationService
 * @description
 * @blame wwb
 * @date 2021-07-27 14:39:35
 */
@Slf4j
@Service
public class BlacklistValidationService {

    @Resource
    private MarketValidationService activityMarketValidationService;

    /**是否有权限
     * @Description 
     * @author wwb
     * @Date 2021-07-27 14:43:33
     * @param marketId
     * @param operateUserDto
     * @return void
    */
    public void manageAble(Integer marketId, OperateUserDTO operateUserDto) {
        activityMarketValidationService.manageAble(marketId, operateUserDto);
    }

}
