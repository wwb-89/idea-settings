package com.chaoxing.activity.service.activity.market;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.mapper.MarketSignUpConfigMapper;
import com.chaoxing.activity.model.MarketSignUpConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**活动市场报名配置服务
 * @author wwb
 * @version ver 1.0
 * @className MarketSignupConfigService
 * @description
 * @blame wwb
 * @date 2021-11-17 15:05:23
 */
@Slf4j
@Service
public class MarketSignupConfigService {

    @Resource
    private MarketSignUpConfigMapper marketSignUpConfigMapper;

    @Resource
    private MarketValidationService marketValidationService;

    /**查询市场的报名配置
     * @Description 
     * @author wwb
     * @Date 2021-11-17 15:07:35
     * @param marketId
     * @return com.chaoxing.activity.model.MarketSignUpConfig
    */
    public MarketSignUpConfig get(Integer marketId) {
        List<MarketSignUpConfig> marketSignUpConfigs = marketSignUpConfigMapper.selectList(new LambdaQueryWrapper<MarketSignUpConfig>()
                .eq(MarketSignUpConfig::getMarketId, marketId)
        );
        return marketSignUpConfigs.stream().findFirst().orElse(null);
    }

    /**初始化活动市场报名配置
     * @Description 
     * @author wwb
     * @Date 2021-11-17 15:11:35
     * @param marketId
     * @return void
    */
    public void init(Integer marketId) {
        MarketSignUpConfig marketSignUpConfig = get(marketId);
        if (marketSignUpConfig != null) {
            return;
        }
        marketSignUpConfig = MarketSignUpConfig.builder()
                .marketId(marketId)
                .build();
        marketSignUpConfigMapper.insert(marketSignUpConfig);
    }

    /**更新活动市场报名配置的可同时报名活动数
     * @Description 
     * @author wwb
     * @Date 2021-11-17 15:22:16
     * @param marketId
     * @param signUpActivityLimit
     * @param operateUser
     * @return void
    */
    public void updateSignUpActivityLimit(Integer marketId, @NotNull Integer signUpActivityLimit, OperateUserDTO operateUser) {
        marketValidationService.manageAble(marketId, operateUser);
        MarketSignUpConfig existMarketSignUpConfig = get(marketId);
        if (existMarketSignUpConfig == null) {
            init(marketId);
        }
        marketSignUpConfigMapper.update(null, new LambdaUpdateWrapper<MarketSignUpConfig>()
                .eq(MarketSignUpConfig::getMarketId, marketId)
                .set(MarketSignUpConfig::getSignUpActivityLimit, signUpActivityLimit)
        );
    }

    /**更新活动市场报名配置的报名按钮
     * @Description 
     * @author wwb
     * @Date 2021-11-17 16:18:18
     * @param marketId
     * @param name
     * @param keyword
     * @param operateUser
     * @return void
    */
    public void updateSignUpBtnName(Integer marketId, String name, String keyword, OperateUserDTO operateUser) {
        marketValidationService.manageAble(marketId, operateUser);
        MarketSignUpConfig existMarketSignUpConfig = get(marketId);
        if (existMarketSignUpConfig == null) {
            init(marketId);
        }
        marketSignUpConfigMapper.update(null, new LambdaUpdateWrapper<MarketSignUpConfig>()
                .eq(MarketSignUpConfig::getMarketId, marketId)
                .set(MarketSignUpConfig::getSignUpBtnName, name)
                .set(MarketSignUpConfig::getSignUpKeyword, keyword)
        );
    }

}