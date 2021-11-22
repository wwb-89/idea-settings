package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.SignUpBtnDTO;
import com.chaoxing.activity.dto.blacklist.BlacklistRuleDTO;
import com.chaoxing.activity.model.BlacklistRule;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.model.MarketSignUpConfig;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.market.MarketSignupConfigService;
import com.chaoxing.activity.service.activity.market.MarketValidationService;
import com.chaoxing.activity.service.blacklist.BlacklistQueryService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
@RequestMapping("market/{marketId}")
public class MarketSettingController {

    @Resource
    private BlacklistQueryService blacklistQueryService;
    @Resource
    private MarketQueryService marketQueryService;
    @Resource
    private MarketValidationService marketValidationService;
    @Resource
    private MarketSignupConfigService marketSignupConfigService;

    /**活动市场设置主页
     * @Description 
     * @author wwb
     * @Date 2021-11-19 15:25:34
     * @param request
     * @param model
     * @param marketId
     * @return java.lang.String
    */
    @LoginRequired
    @RequestMapping("setting")
    public String index(HttpServletRequest request, Model model, @PathVariable Integer marketId) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Market market = marketValidationService.manageAble(marketId, loginUser.buildOperateUserDTO());
        model.addAttribute("market", market);
        return "pc/market/setting-new";
    }

    /**规则配置主页
    * @Description
    * @author huxiaolong
    * @Date 2021-07-26 16:09:36
    * @param
    * @return java.lang.String
    */
    @RequestMapping("rule")
    public String rule(HttpServletRequest request, Model model, @PathVariable Integer marketId) {
        BlacklistRule blacklistRule = blacklistQueryService.getBlacklistRuleByMarketId(marketId);
        BlacklistRuleDTO blacklistRuleDto = Optional.ofNullable(blacklistRule).map(BlacklistRuleDTO::buildFromBlacklistRule).orElse(BlacklistRuleDTO.buildDefault(marketId));
        Market market = marketQueryService.getById(marketId);
        model.addAttribute("blacklistRule", blacklistRuleDto);
        model.addAttribute("marketId", marketId);
        // 黑名单的人数
        int blacklistNum = blacklistQueryService.countMarketBlackNum(marketId);
        model.addAttribute("blacklistNum", blacklistNum);
        MarketSignUpConfig marketSignUpConfig = marketSignupConfigService.get(marketId);
        model.addAttribute("marketSignUpConfig", marketSignUpConfig);
        // 报名按钮列表
        List<SignUpBtnDTO> signUpBtns = SignUpBtnDTO.list();
        model.addAttribute("signUpBtns", signUpBtns);
        return "pc/market/setting";
    }

}
