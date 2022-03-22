package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.verification.UserSignUpAbleVerificationDTO;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**验证服务
 * @author wwb
 * @version ver 1.0
 * @className ValidationApiService
 * @description
 * @blame wwb
 * @date 2021-07-16 10:01:39
 */
@RestController
@RequestMapping("validate")
public class ValidationApiService {

	@Resource
	private SignUpConditionService signUpConditionService;

	/**用户是否能报名
	 * @Description
	 * 1、满足配置的所有报名条件
	 * 2、没有超过活动市场设置的同时报名的活动数量
	 * 3、没有在黑名单中
	 * @author wwb
	 * @Date 2021-07-16 10:09:09
	 * @param uid
	 * @param signId
	 * @param templateComponentId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("sign-up-able")
	public RestRespDTO signUpAble(Integer uid, Integer signId, Integer templateComponentId) {
		UserSignUpAbleVerificationDTO userSignUpAbleVerification = signUpConditionService.userCanSignUp(uid, signId, templateComponentId);
		return RestRespDTO.success(userSignUpAbleVerification);
	}

}