package com.chaoxing.activity.admin.controller.pc;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OrgFormConfigDTO;
import com.chaoxing.activity.dto.sign.SignUpScopeTypeDTO;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.org.OrgFormConfigService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**机构表单配置服务
 * @author wwb
 * @version ver 1.0
 * @className OrgFormConfigController
 * @description
 * @blame wwb
 * @date 2021-06-08 11:10:05
 */
@Controller
@RequestMapping("org/form/config")
public class OrgFormConfigController {

	@Resource
	private OrgFormConfigService orgFormConfigService;
	@Resource
	private PassportApiService passportApiService;

	/**机构表单配置页面
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-08 17:35:12
	 * @param request
	 * @param model
	 * @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping
	public String index(HttpServletRequest request, Model model) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Integer fid = loginUser.getFid();
		String orgName = passportApiService.getOrgName(fid);
		model.addAttribute("orgName", orgName);
		// 查询机构配置的表单
		OrgFormConfigDTO orgFormConfig = orgFormConfigService.getByFid(fid);
		model.addAttribute("orgFormConfig", orgFormConfig);
		// 报名使用的组织架构
		List<SignUpScopeTypeDTO> signUpScopeTypes = SignUpScopeTypeDTO.fromSignUpScopeTypeEnum();
		model.addAttribute("signUpScopeTypes", signUpScopeTypes);
		return "pc/config/org-form";
	}

}
