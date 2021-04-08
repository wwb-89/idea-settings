package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.form.FormDTO;
import com.chaoxing.activity.service.manager.FormApiService;
import com.chaoxing.activity.admin.util.LoginUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**表单api
 * @author wwb
 * @version ver 1.0
 * @className FormApiController
 * @description
 * @blame wwb
 * @date 2020-11-18 19:16:08
 */
@RestController
@RequestMapping("api/form")
public class FormApiController {

	@Resource
	private FormApiService formApiService;

	/**查询机构下的表单列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-18 19:23:05
	 * @param request
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("list")
	public RestRespDTO listOrgForms(HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		List<FormDTO> forms = formApiService.listOrgForm(loginUser.getFid());
		return RestRespDTO.success(forms);
	}

}
