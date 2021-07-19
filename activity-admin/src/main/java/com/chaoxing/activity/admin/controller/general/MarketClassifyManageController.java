package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**市场活动分类管理
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyManageController
 * @description
 * @blame wwb
 * @date 2021-04-11 22:33:40
 */
@Controller
@RequestMapping("market/{marketId}/classify")
public class MarketClassifyManageController {

	@Resource
	private ClassifyQueryService classifyQueryService;

	/**市场活动分类管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-11 22:37:33
	 * @param request
	 * @param model
	 * @param marketId 活动市场id
	 * @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping("")
	public String index(HttpServletRequest request, Model model, @PathVariable Integer marketId) {
		model.addAttribute("marketId", marketId);
		// 查询所有的活动类型
		List<Classify> classifies = classifyQueryService.listMarketClassifies(marketId);
		model.addAttribute("classifies", classifies);
		return "pc/classify/market-classify";
	}

}
