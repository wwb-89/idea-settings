package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.manager.FormApiService;
import com.chaoxing.activity.vo.manager.WfwFormVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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
	private FormApiService formApiService;

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

}