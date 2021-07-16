package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**区域api服务
 * @author wwb
 * @version ver 1.0
 * @className AreaApiController
 * @description
 * @blame wwb
 * @date 2020-11-19 21:28:20
 */
@RestController
@RequestMapping("api/area")
public class AreaApiController {

	@Resource
	private WfwAreaApiService wfwAreaApiService;
	@Resource
	private ActivityQueryService activityQueryService;

	/**获取架构
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-01 16:34:18
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("")
	public RestRespDTO listByFid(Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		List<WfwAreaDTO> regionalArchitectures = wfwAreaApiService.listByFid(activity.getCreateFid());
		return RestRespDTO.success(regionalArchitectures);
	}

	/**获取机构的层级架构
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-20 12:53:08
	 * @param request
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("list")
	public RestRespDTO listByFid(HttpServletRequest request) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		List<WfwAreaDTO> regionalArchitectures = wfwAreaApiService.listByFid(loginUser.getFid());
		return RestRespDTO.success(regionalArchitectures);
	}

}