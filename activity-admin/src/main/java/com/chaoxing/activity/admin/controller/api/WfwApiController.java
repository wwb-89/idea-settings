package com.chaoxing.activity.admin.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.WfwContacterDTO;
import com.chaoxing.activity.dto.manager.WfwDepartmentDTO;
import com.chaoxing.activity.dto.manager.WfwGroupDTO;
import com.chaoxing.activity.service.manager.WfwContactApiService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**微服务api服务
 * @author wwb
 * @version ver 1.0
 * @className WfwApiController
 * @description
 * @blame wwb
 * @date 2021-03-28 17:22:52
 */
@RestController
@RequestMapping("api/wfw")
public class WfwApiController {

	@Resource
	private WfwGroupApiService wfwGroupApiService;
	@Resource
	private WfwContactApiService wfwContactApiService;

	/**查询组
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-28 17:24:08
	 * @param request
	 * @param gid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("org/groups")
	public RestRespDTO listGroup(HttpServletRequest request, Integer gid) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		List<WfwGroupDTO> wfwGroups = wfwGroupApiService.getGroupByGid(loginUser.getFid(), gid);
		return RestRespDTO.success(wfwGroups);
	}

	/**搜索联系人
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-28 17:35:25
	 * @param request
	 * @param sw
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("contacter/search")
	public RestRespDTO contacter(HttpServletRequest request, String sw) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Page<WfwContacterDTO> page = HttpServletRequestUtils.buid(request);
		page = wfwContactApiService.search(page, loginUser.getUid(), sw);
		return RestRespDTO.success(page);
	}

	/**机构下的部门列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-28 17:37:49
	 * @param request
	 * @param fid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("org/{fid}/department")
	public RestRespDTO listOrgDepartment(HttpServletRequest request, @PathVariable Integer fid) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Page<WfwDepartmentDTO> page = HttpServletRequestUtils.buid(request);
		page = wfwContactApiService.listOrgDepartment(page, fid, loginUser.getUid());
		return RestRespDTO.success(page);
	}

	/**查询部门下的联系人
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-28 17:55:20
	 * @param request
	 * @param departmentId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("department/{departmentId}/contacter")
	public RestRespDTO listDepartmentContacter(HttpServletRequest request, @PathVariable Integer departmentId) {
		Page<WfwContacterDTO> page = HttpServletRequestUtils.buid(request);
		page = wfwContactApiService.listDepartmentContacter(page, departmentId);
		return RestRespDTO.success(page);
	}

}
