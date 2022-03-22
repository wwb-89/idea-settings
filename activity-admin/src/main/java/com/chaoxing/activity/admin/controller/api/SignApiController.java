package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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