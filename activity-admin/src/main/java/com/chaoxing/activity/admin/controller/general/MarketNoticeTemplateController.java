package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.notice.MarketNoticeTemplateDTO;
import com.chaoxing.activity.service.activity.market.MarketValidationService;
import com.chaoxing.activity.service.notice.MarketNoticeTemplateService;
import com.chaoxing.activity.service.notice.NoticeFieldDTO;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**活动市场通知模版服务
 * @author wwb
 * @version ver 1.0
 * @className MarketNoticeTemplateController
 * @description
 * @blame wwb
 * @date 2021-11-11 15:19:49
 */
@Controller
@RequestMapping("market/{marketId}/notice-template")
public class MarketNoticeTemplateController {

	@Resource
	private MarketNoticeTemplateService marketNoticeTemplateService;
	@Resource
	private MarketValidationService marketValidationService;

	/**活动市场通知模版管理主页
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-11 15:21:55
	 * @param model
	 * @param request
	 * @param marketId
	 * @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping
	public String index(Model model, HttpServletRequest request, @PathVariable Integer marketId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		marketValidationService.manageAble(marketId, loginUser.buildOperateUserDTO());
		List<MarketNoticeTemplateDTO> marketNoticeTemplates = marketNoticeTemplateService.listByMarketId(marketId);
		model.addAttribute("marketNoticeTemplates", marketNoticeTemplates);
		List<NoticeFieldDTO> noticeFields = marketNoticeTemplateService.listNoticeField();
		model.addAttribute("noticeFields", noticeFields);
		model.addAttribute("marketId", marketId);
		return "pc/market/notice-template";
	}

}