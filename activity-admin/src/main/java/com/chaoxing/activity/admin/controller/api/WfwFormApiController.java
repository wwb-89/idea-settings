package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateResultDTO;
import com.chaoxing.activity.model.SignUpFillInfoType;
import com.chaoxing.activity.service.manager.wfw.WfwApprovalApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.util.WfwFormUtils;
import com.chaoxing.activity.vo.manager.WfwFormFieldVO;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
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
	private WfwFormApiService wfwFormApiService;
	@Resource
	private WfwApprovalApiService wfwApprovalApiService;

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
		List<FormStructureDTO> formFields = wfwFormApiService.getFormStructure(formId, fid);
		List<WfwFormFieldVO> result = formFields.stream().map(v -> {
			WfwFormFieldVO fieldItem = WfwFormFieldVO.buildFromWfwFormFieldDTO(v);
			JSONObject optionBindInfo = v.getOptionBindInfo();
			// 如果当前字段为关联表字段，则查询关联表字段的下拉数据源
			if (optionBindInfo != null) {
				Integer fieldId = optionBindInfo.getInteger("bindFieldId");
				Integer bindFormId = optionBindInfo.getInteger("bindFormId");
				FormStructureDTO relatedStructure = wfwFormApiService.getFormStructure(bindFormId, fid).stream().filter(u -> Objects.equals(u.getId(), fieldId)).findFirst().orElse(null);
				if (relatedStructure != null) {
					List<String> options = Lists.newArrayList();
					List<FormDataDTO> relatedRecords = wfwFormApiService.listFormRecord(bindFormId, fid);
					String fieldAlias = relatedStructure.getAlias();
					for (FormDataDTO formDatum : relatedRecords) {
						String formValue = WfwFormUtils.getValue(formDatum, fieldAlias);
						options.add(formValue);
					}
					fieldItem.setOptions(options);
				}
			}
			return fieldItem;
		}).collect(Collectors.toList());
		return RestRespDTO.success(result);
	}

	/**获取表单编辑url地址(万能表单和审批)
	* @Description
	* @author huxiaolong
	* @Date 2021-08-17 17:50:13
	* @param fid
	* @param uid
	* @param formId
	 * @param signUpFormType 报名万能表单类型(wfw_form; approval)
	* @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("build/edit-url")
	public RestRespDTO getWfwFormEditUrl(@RequestParam Integer fid, @RequestParam Integer uid, @RequestParam Integer formId, @RequestParam String signUpFormType) {
		SignUpFillInfoType.TypeEnum type = SignUpFillInfoType.TypeEnum.fromValue(signUpFormType);
		String url = "";
		if (Objects.equals(SignUpFillInfoType.TypeEnum.WFW_FORM, type)) {
			url = wfwFormApiService.buildEditUrl(formId, uid, fid);
		} else {
			url = wfwApprovalApiService.buildEditUrl(formId, uid, fid);
		}
		return RestRespDTO.success(url);
	}

	/**根据id为wfwFormTemplateId的万能表单模板创建表单，并带上新表单的编辑页面url
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-19 16:20:09
	 * @param fid
	 * @param uid
	 * @param wfwFormTemplateId
	 * @param signUpFormType 报名万能表单类型(wfw_form; approval)
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@RequestMapping("create/from/wfw-form-template")
	public RestRespDTO createWfwFormWithEditUrl(@RequestParam Integer fid,
												@RequestParam Integer uid,
												Integer wfwFormTemplateId,
												@RequestParam String signUpFormType) {
		WfwFormCreateResultDTO wfwForm = wfwFormApiService.createWfwForm(fid, uid, wfwFormTemplateId, signUpFormType);
		return RestRespDTO.success(wfwForm);
	}

	/**获取表单管理地址
	* @Description
	* @author huxiaolong
	* @Date 2021-08-20 14:59:36
	* @param fid
	* @param uid
	* @param formId
	* @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("build/manage-url")
	public RestRespDTO getWfwFormManageUrl(@RequestParam Integer fid, @RequestParam Integer uid, @RequestParam Integer formId) {
		return RestRespDTO.success(wfwFormApiService.getFormAdminUrl(formId, fid, uid));
	}


}