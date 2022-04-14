package com.chaoxing.activity.api.controller.custom;

import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.util.UserAgentUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**学习通登录服务
 * @author wwb
 * @version ver 1.0
 * @className XxtLoginApiController
 * @description
 * @blame wwb
 * @date 2022-04-05 22:11:16
 */
@Slf4j
@Controller
@RequestMapping("custom/xxt/login")
public class XxtLoginApiController {

	private static final String ACTIVITY_URL = "https://5326hwd.mh.chaoxing.com/page/250238/show";

	/**登录页面
	 * @Description 
	 * @author wwb
	 * @Date 2022-04-05 22:25:57
	 * @param model
	 * @param request
	 * @param refer
	 * @return java.lang.String
	*/
	@RequestMapping
	public String index(Model model, HttpServletRequest request, String refer) {
		Integer uid = CookieUtils.getUid(request);
		if (uid != null) {
			return "redirect:" + refer;
		}
		refer = Optional.ofNullable(refer).filter(StringUtils::isNotBlank).orElse(ACTIVITY_URL);
		if (UserAgentUtils.isMobileAccess(request)) {
			model.addAttribute("refer", refer);
			return "mobile/custom/xxt-login";
		} else {
			return "redirect:https://passport2.chaoxing.com/login?loginType=1&fid=&newversion=true&refer=" + refer;
		}
	}

}
