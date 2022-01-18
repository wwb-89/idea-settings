package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwRoleDTO;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.manager.wfw.WfwRoleApiService;
import com.chaoxing.activity.vo.manager.WfwFormVO;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

	@Resource
	private WfwFormApiService formApiService;
	@Resource
	private WfwRoleApiService wfwRoleApiService;
	@Resource
	private WfwAreaApiService wfwAreaApiService;

	/**查询机构下的微服务表单列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-08 17:42:32
	 * @param fid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{fid}/wfw-form/list")
	public RestRespDTO listOrgWfwForm(@PathVariable Integer fid) {
		List<WfwFormVO> wfwForms = formApiService.listOrgForm(fid);
		return RestRespDTO.success(wfwForms);
	}

	/**查询角色
	 * @Description 用户下的角色+区域下的角色
	 * @author wwb
	 * @Date 2022-01-18 11:22:16
	 * @param fid
	 * @param areaCode
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{fid}/roles")
	public RestRespDTO listRoles(@PathVariable Integer fid, String areaCode) {
		List<WfwRoleDTO> orgWfwRoles = wfwRoleApiService.listFidRole(fid);
		if (StringUtils.isNotBlank(areaCode)) {
			List<WfwAreaDTO> wfwAreas = wfwAreaApiService.listByFid(fid);
			Integer fwId = Optional.ofNullable(wfwAreas).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(v.getCode(), areaCode)).findFirst().map(WfwAreaDTO::getId).orElse(null);
			// 创建一个区域角色组
			Integer areaRoleGroupId = -1;
			WfwRoleDTO areaRoleGroup = WfwRoleDTO.builder()
					.role(null)
					.id(areaRoleGroupId)
					.name("区域角色")
					.group(true)
					.build();
			List<WfwRoleDTO> wfwRoles = wfwRoleApiService.listAreaRole(fwId);
			for (WfwRoleDTO wfwRole : wfwRoles) {
				wfwRole.setRoleGroupId(areaRoleGroupId);
			}
			orgWfwRoles.add(areaRoleGroup);
			orgWfwRoles.addAll(wfwRoles);
		}
		return RestRespDTO.success(orgWfwRoles);
	}

}