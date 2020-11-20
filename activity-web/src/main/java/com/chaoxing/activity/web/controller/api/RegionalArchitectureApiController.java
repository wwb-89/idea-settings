package com.chaoxing.activity.web.controller.api;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import com.chaoxing.activity.web.util.LoginUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className RegionalArchitectureApiController
 * @description
 * @blame wwb
 * @date 2020-11-19 21:28:20
 */
@RestController
@RequestMapping("api/regional-architecture")
public class RegionalArchitectureApiController {

	@Resource
	private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;

	@RequestMapping("")
	public RestRespDTO listByFid(HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		List<WfwRegionalArchitectureDTO> regionalArchitectures = wfwRegionalArchitectureApiService.listByFid(loginUser.getFid());
		return RestRespDTO.success(regionalArchitectures);
	}

}