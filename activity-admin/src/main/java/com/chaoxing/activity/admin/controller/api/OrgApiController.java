package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**机构api服务
 * @author wwb
 * @version ver 1.0
 * @className OrgApiController
 * @description
 * @blame wwb
 * @date 2021-03-28 11:14:08
 */
@RestController
@RequestMapping("api/org")
public class OrgApiController {



	/**查询机构下的部门列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-28 11:15:24
	 * @param request
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("departments")
	public RestRespDTO departments(HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		return RestRespDTO.success();
	}

}
