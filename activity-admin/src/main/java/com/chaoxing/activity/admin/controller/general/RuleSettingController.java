package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.dto.blacklist.BlacklistRuleDTO;
import com.chaoxing.activity.model.BlacklistRule;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.blacklist.BlacklistQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/7/26 4:07 下午
 * <p>
 */
@Slf4j
@Controller
@RequestMapping("market/{marketId}/rule")
public class RuleSettingController {

    @Resource
    private BlacklistQueryService blacklistQueryService;
    @Resource
    private MarketQueryService marketQueryService;

    /**规则配置主页
    * @Description
    * @author huxiaolong
    * @Date 2021-07-26 16:09:36
    * @param
    * @return java.lang.String
    */
    @RequestMapping("")
    public String index(HttpServletRequest request, Model model, @PathVariable Integer marketId) {
        BlacklistRule blacklistRule = blacklistQueryService.getBlacklistRuleByMarketId(marketId);
        BlacklistRuleDTO blacklistRuleDto = Optional.ofNullable(blacklistRule).map(BlacklistRuleDTO::buildFromBlacklistRule).orElse(BlacklistRuleDTO.buildDefault(marketId));
        Market market = marketQueryService.getById(marketId);
        model.addAttribute("blacklistRule", blacklistRuleDto);
        model.addAttribute("marketId", marketId);
        model.addAttribute("signUpActivityLimit", Optional.ofNullable(market.getSignUpActivityLimit()).orElse(0));
        return "pc/market/setting";
    }

}
