package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OrgDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.UcApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

/**用户api服务
 * @author wwb
 * @version ver 1.0
 * @className UserApiController
 * @description
 * @blame wwb
 * @date 2021-03-27 12:01:07
 */
@RestController
@RequestMapping("api/user")
public class UserApiController {

	@Resource
	private PassportApiService passportApiService;

	@Resource
	private UcApiService ucApiService;

	/**查询用户的机构列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-27 12:02:11
	 * @param request
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("orgs")
	public RestRespDTO orgs(HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		PassportUserDTO passportUser = passportApiService.getByUid(loginUser.getUid());
		List<OrgDTO> orgs = Optional.ofNullable(passportUser).map(PassportUserDTO::getAffiliations).orElse(Lists.newArrayList());
		return RestRespDTO.success(orgs);
	}

	@RequestMapping("clazz/teaching")
	public RestRespDTO teachingClazz(HttpServletRequest request, String url) {
		return RestRespDTO.success(ucApiService.listTeacherTeachingClazz(request, url));
	}

}