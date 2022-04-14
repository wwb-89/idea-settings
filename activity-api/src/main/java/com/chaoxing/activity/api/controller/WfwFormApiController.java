package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.SignUpFillInfoType;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**微服务表单api服务
 * @author wwb
 * @version ver 1.0
 * @className WfwFormApiController
 * @description
 * @blame wwb
 * @date 2021-07-08 17:59:05
 */
@RestController
@RequestMapping("wfw-form")
public class WfwFormApiController {

	@Resource
	private WfwFormApiService wfwFormApiService;

	/**为报名克隆万能表单
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-19 17:57:07
	 * @param originFid
	 * @param formId
	 * @param fid
	 * @param uid
	 * @param tplComponentId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@RequestMapping("clone/by-sign-up")
	public RestRespDTO cloneSignUpWfwForm(@RequestParam Integer originFid,
										  Integer formId,
										  @RequestParam Integer fid,
										  @RequestParam Integer uid,
										  @RequestParam Integer tplComponentId,
										  String formType) {
		formType = StringUtils.isBlank(formType) ? SignUpFillInfoType.TypeEnum.WFW_FORM.getValue() : formType;
		return RestRespDTO.success(wfwFormApiService.cloneSignUpWfwForm(originFid, formId, fid, uid, tplComponentId, formType));
	}


}