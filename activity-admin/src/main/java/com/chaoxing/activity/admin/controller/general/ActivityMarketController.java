package com.chaoxing.activity.admin.controller.general;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

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
public class ActivityMarketController {

	@RequestMapping("new/from-wfw")
	public String newFromWfw(HttpServletRequest request, Model model, Integer classifyId, Integer fid) {
		model.addAttribute("classifyId", classifyId);
		model.addAttribute("fid", fid);
		return "pc/market/wfw-new-market";
	}

}