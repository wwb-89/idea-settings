package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OrgFormConfigDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.org.OrgFormConfigService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrgFormApiConfigController
 * @description
 * @blame wwb
 * @date 2021-06-08 17:35:35
 */
@RestController
@RequestMapping("api/org/form/config")
public class OrgFormApiConfigController {

	@Resource
	private OrgFormConfigService orgFormConfigService;

	/**配置机构表单
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-08 17:36:54
	 * @param request
	 * @param orgFormConfig
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping
	public RestRespDTO configForm(HttpServletRequest request, OrgFormConfigDTO orgFormConfig) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		orgFormConfig.setFid(loginUser.getFid());
		orgFormConfigService.configOrgForm(orgFormConfig);
		return RestRespDTO.success();
	}

}
