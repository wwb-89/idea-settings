package com.chaoxing.activity.admin.controller.api.market;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.notice.MarketNoticeTemplateDTO;
import com.chaoxing.activity.service.notice.MarketNoticeTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wwb
 * @version ver 1.0
 * @className MarketNoticeTemplateApiController
 * @description
 * @blame wwb
 * @date 2021-11-11 21:49:01
 */
@Slf4j
@RestController
@RequestMapping("api/market/{marketId}/notice-template")
public class MarketNoticeTemplateApiController {

	@Resource
	private MarketNoticeTemplateService marketNoticeTemplateService;

	@RequestMapping("update")
	public RestRespDTO update(HttpServletRequest request, @PathVariable Integer marketId, MarketNoticeTemplateDTO marketNoticeTemplate) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		marketNoticeTemplateService.addOrEdit(marketNoticeTemplate, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	@RequestMapping("enable")
	public RestRespDTO enable(HttpServletRequest request, @PathVariable Integer marketId, @RequestParam String noticeType) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		marketNoticeTemplateService.enable(marketId, noticeType, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	@RequestMapping("disable")
	public RestRespDTO disable(HttpServletRequest request, @PathVariable Integer marketId, @RequestParam String noticeType) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		marketNoticeTemplateService.disable(marketId, noticeType, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

}
