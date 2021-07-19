package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**活动市场
 * @author wwb
 * @version ver 1.0
 * @className ActivityMarketController
 * @description
 * @blame wwb
 * @date 2021-07-16 15:25:17
 */
@Controller
@RequestMapping("market")
public class MarketController {

	/**活动市场管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-18 20:41:17
	 * @param request
	 * @param model
	 * @return java.lang.String
	*/
	@RequestMapping("{marketId}")
	public String index(HttpServletRequest request, Model model, @PathVariable Integer marketId) {
		model.addAttribute("marketId", marketId);
		return "redirect:/?marketId="+ marketId;
	}

	/**微服务创建营应用页面
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-18 20:11:11
	 * @param request
	 * @param model
	 * @param classifyId
	 * @param fid
	 * @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping("new/from-wfw")
	public String newFromWfw(HttpServletRequest request, Model model, Integer classifyId, Integer fid) {
		String referer = request.getHeader("referer");
		fid = Optional.ofNullable(fid).orElse(LoginUtils.getLoginUser(request).getFid());
		model.addAttribute("classifyId", classifyId);
		model.addAttribute("fid", fid);
		model.addAttribute("referer", referer);
		return "pc/market/wfw-new-market";
	}

}