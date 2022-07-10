package com.chaoxing.activity.web.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**报名服务
 * @author wwb
 * @version ver 1.0
 * @className SignUpApiController
 * @description
 * @blame wwb
 * @date 2021-01-28 14:46:45
 */
@RestController
@RequestMapping("api/sign-up")
public class SignUpApiController {

	@Resource
	private SignApiService signApiService;

	/**取消报名
	 * @Description
	 * @author wwb
	 * @Date 2021-01-28 14:23:22
	 * @param request
	 * @param signUpId
	 * @return com.chaoxing.sign.dto.RestRespDTO
	 */
	@LoginRequired
	@PostMapping("{signUpId}/cancel")
	public RestRespDTO cancel(HttpServletRequest request, @PathVariable Integer signUpId) {
		signApiService.cancelSignUp(request, signUpId);
		return RestRespDTO.success();
	}

	/**撤销申请
	 * @Description
	 * @author wwb
	 * @Date 2021-01-28 14:23:32
	 * @param request
	 * @param signUpId
	 * @return com.chaoxing.sign.dto.RestRespDTO
	 */
	@LoginRequired
	@PostMapping("{signUpId}/revocation")
	public RestRespDTO revocation(HttpServletRequest request, @PathVariable Integer signUpId) {
		signApiService.revocationSignUp(request, signUpId);
		return RestRespDTO.success();
	}

}