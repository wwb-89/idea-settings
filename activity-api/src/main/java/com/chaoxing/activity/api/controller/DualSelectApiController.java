package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.util.constant.UrlConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;

/**双选会
 * @author wwb
 * @version ver 1.0
 * @className DualSelectApiController
 * @description
 * @Deprecated 请使用 /redirect/dual-select/** 来替换，所有的重定向工作都迁移到 /redirect/**中
 * @blame wwb
 * @date 2021-04-08 17:22:37
 */
@Controller
@RequestMapping("dual-select")
@Deprecated
public class DualSelectApiController {

	@Resource
	private ActivityQueryService activityQueryService;

	/**转发到双选会会场
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-08 17:23:51
	 * @param pageId
	 * @return java.lang.String
	*/
	@RequestMapping("forward")
	public RedirectView forward(Integer pageId) {
		Activity activity = activityQueryService.getByPageId(pageId);
		Integer fid = activity.getCreateFid();
		String url = String.format(UrlConstant.DUAL_SELECT_INDEX_URL, activity.getId(), fid);
		return new RedirectView(url);
	}

}
