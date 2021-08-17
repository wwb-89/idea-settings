package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormFieldDTO;
import com.chaoxing.activity.service.manager.WfwFormApiService;
import com.chaoxing.activity.service.manager.WfwFormCreateApiService;
import com.chaoxing.activity.vo.manager.WfwFormFieldVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**微服务表单api服务
 * @author wwb
 * @version ver 1.0
 * @className WfwFormApiController
 * @description
 * @blame wwb
 * @date 2021-07-08 17:59:05
 */
@RestController
@RequestMapping("api/wfw-form")
public class WfwFormApiController {

	@Resource
	private WfwFormApiService formApiService;

	@Resource
	private WfwFormCreateApiService formCreateApiService;

	/**查询微服务表单的字段列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-08 18:00:50
	 * @param formId
	 * @param fid
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{formId}/field")
	public RestRespDTO listWfwFormField(@PathVariable Integer formId, @RequestParam Integer fid) {
		List<WfwFormFieldDTO> formFields = formApiService.listFormField(fid, formId);
		return RestRespDTO.success(formFields.stream().map(WfwFormFieldVO::buildFromWfwFormFieldDTO).collect(Collectors.toList()));
	}


	/**获取构建表单创建地址
	* @Description
	* @author huxiaolong
	* @Date 2021-08-17 17:50:13
	* @param fid
	* @param uid
	* @param templateType
	* @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("build/create-url")
	public RestRespDTO listWfwFormField(@RequestParam Integer fid, @RequestParam Integer uid, @RequestParam String templateType) {
		return RestRespDTO.success(formCreateApiService.buildCreateFormUrl(fid, uid, templateType));
	}

}