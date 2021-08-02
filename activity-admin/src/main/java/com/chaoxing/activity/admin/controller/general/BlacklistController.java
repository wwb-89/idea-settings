package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.service.activity.market.ActivityMarketValidationService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
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
	private ActivityMarketValidationService marketValidationService;

	@LoginRequired
	@RequestMapping("list")
	public String list(HttpServletRequest request, @PathVariable Integer marketId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		marketValidationService.manageAble(marketId, loginUser.buildOperateUserDTO());
		return "pc/blacklist/blacklist-list";
	}

}
