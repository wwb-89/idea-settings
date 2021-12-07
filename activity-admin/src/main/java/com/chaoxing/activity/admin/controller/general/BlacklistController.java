package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.service.activity.market.MarketValidationService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.util.constant.DomainConstant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**黑名单
 * @author wwb
 * @version ver 1.0
 * @className BlacklistController
 * @description
 * @blame wwb
 * @date 2021-07-29 09:54:55
 */
@Controller
@RequestMapping("market/{marketId}/blacklist")
public class BlacklistController {

	@Resource
	private MarketValidationService marketValidationService;

	@LoginRequired
	@RequestMapping("list")
	public String list(HttpServletRequest request, Model model, @PathVariable Integer marketId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Market market = marketValidationService.manageAble(marketId, loginUser.buildOperateUserDTO());
		model.addAttribute("market", market);
		model.addAttribute("marketId", marketId);
		model.addAttribute("photoDomain", DomainConstant.PHOTO);
		return "pc/blacklist/blacklist-list";
	}

}
