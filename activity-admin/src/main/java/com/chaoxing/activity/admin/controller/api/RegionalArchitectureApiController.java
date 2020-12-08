package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**层级架构api服务
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
		List<WfwRegionalArchitectureDTO> regionalArchitectures = wfwRegionalArchitectureApiService.listByFid(activity.getCreateFid());
		return RestRespDTO.success(regionalArchitectures);
	}

}