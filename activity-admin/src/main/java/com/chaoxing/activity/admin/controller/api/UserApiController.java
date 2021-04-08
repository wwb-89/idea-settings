package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OrgDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
		List<OrgDTO> orgs = Lists.newArrayList();
		PassportUserDTO passportUser = passportApiService.getByUid(loginUser.getUid());
		if (passportUser != null && CollectionUtils.isNotEmpty(passportUser.getAffiliations())) {
			orgs = passportUser.getAffiliations();
		}
		return RestRespDTO.success(orgs);
	}

}