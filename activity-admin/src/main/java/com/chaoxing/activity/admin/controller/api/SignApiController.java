package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**报名签到相关api
 * @author wwb
 * @version ver 1.0
 * @className SignApiController
 * @description
 * @blame wwb
 * @date 2021-04-08 16:37:38
 */
@Slf4j
@RestController
@RequestMapping("api/sign")
public class SignApiController {

	@Resource
	private SignApiService signApiService;

	@LoginRequired
	@RequestMapping("sign-in/position-history")
	public RestRespDTO listSignInPositionHistory(HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		List<String> list = signApiService.listSignInPositionHistory(loginUser.getUid(), loginUser.getFid());
		return RestRespDTO.success(list);
	}

	@LoginRequired
	@RequestMapping("sign-in/position-history/add")
	public RestRespDTO addSignInPositionHistory(HttpServletRequest request, String jsonStr) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		signApiService.addSignInPositionHistory(loginUser.getUid(), loginUser.getFid(), jsonStr);
		return RestRespDTO.success();
	}

	@LoginRequired
	@RequestMapping("sign-in/position-history/delete")
	public RestRespDTO deleteSignInPositionHistory(HttpServletRequest request, String jsonStr) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		signApiService.deleteSignInPositionHistory(loginUser.getUid(), loginUser.getFid(), jsonStr);
		return RestRespDTO.success();
	}

	@LoginRequired
	@RequestMapping("{signId}/sign-in/num")
	public RestRespDTO countSignInNum(@PathVariable Integer signId) {
		return RestRespDTO.success(signApiService.countSignInNum(signId));
	}

	@LoginRequired
	@RequestMapping("{signId}/form-collection/num")
	public RestRespDTO countFormCollectionNum(@PathVariable Integer signId) {
		return RestRespDTO.success(signApiService.countFormCollectionNum(signId));
	}

}