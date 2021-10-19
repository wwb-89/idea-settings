package com.chaoxing.activity.admin.controller.api.inspection;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.InspectionConfig;
import com.chaoxing.activity.model.InspectionConfigDetail;
import com.chaoxing.activity.service.inspection.InspectionConfigHandleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**考核api服务
 * @author wwb
 * @version ver 1.0
 * @className InspectionApiController
 * @description
 * @blame wwb
 * @date 2021-06-16 17:44:38
 */
@RestController
@RequestMapping("api/inspection")
public class InspectionApiController {

	@Resource
	private InspectionConfigHandleService inspectionConfigHandleService;

	/**配置
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-16 18:01:16
	 * @param request
	 * @param inspectionConfigStr
	 * @param inspectionConfigDetailsStr
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("config")
	public RestRespDTO config(HttpServletRequest request, String inspectionConfigStr, String inspectionConfigDetailsStr) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		InspectionConfig inspectionConfig = JSON.parseObject(inspectionConfigStr, InspectionConfig.class);
		List<InspectionConfigDetail> inspectionConfigDetails = JSON.parseArray(inspectionConfigDetailsStr, InspectionConfigDetail.class);
		return RestRespDTO.success(inspectionConfigHandleService.config(inspectionConfig, inspectionConfigDetails, loginUser));
	}

}
