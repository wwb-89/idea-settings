package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.model.SignUpWfwFormTemplate;
import com.chaoxing.activity.service.activity.market.MarketValidationService;
import com.chaoxing.activity.service.activity.template.signup.SignUpWfwFormTemplateHandleService;
import com.chaoxing.activity.service.activity.template.signup.SignUpWfwFormTemplateQueryService;
import com.chaoxing.activity.service.manager.wfw.WfwApprovalApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**报名填写模板配置
 * @author wwb
 * @version ver 1.0
 * @className SignUpFillInTemplateConfigController
 * @description
 * @blame wwb
 * @date 2022-03-29 15:57:43
 */
@Slf4j
@Controller
@RequestMapping("market/{marketId}/template/sign-up/fill-in")
public class SignUpFillInTemplateConfigController {

	@Resource
	private MarketValidationService marketValidationService;
	@Resource
	private SignUpWfwFormTemplateQueryService signUpWfwFormTemplateQueryService;
	@Resource
	private SignUpWfwFormTemplateHandleService signUpWfwFormTemplateHandleService;
	@Resource
	private WfwFormApiService wfwFormApiService;
	@Resource
	private WfwApprovalApiService wfwApprovalApiService;

	/**报名填写模板配置列表
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 16:44:42
	 * @param model
	 * @param marketId
	 * @return java.lang.String
	*/
	@LoginRequired
	@RequestMapping
	public String index(Model model, @PathVariable Integer marketId) {
		List<SignUpWfwFormTemplate> signUpWfwFormTemplates = signUpWfwFormTemplateQueryService.listMarketTemplate(marketId);
		model.addAttribute("marketId", marketId);
		model.addAttribute("signUpFillInTemplates", signUpWfwFormTemplates);
		String wfwFormDomain = DomainConstant.WFW_FORM_API;
		model.addAttribute("wfwFormDomain", wfwFormDomain);
		return "/pc/market/sign-up-fill-in-template";
	}

	/**新增万能表单模板
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:41:00
	 * @param request
	 * @param marketId
	 * @return org.springframework.web.servlet.view.RedirectView
	*/
	@LoginRequired
	@GetMapping("wfw-form/add")
	public RedirectView toAddWfwForm(HttpServletRequest request, @PathVariable Integer marketId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Market market = marketValidationService.exist(marketId);
		SignUpWfwFormTemplate originTemplate = signUpWfwFormTemplateQueryService.getSystemNormalTemplate(SignUpWfwFormTemplate.TypeEnum.WFW_FORM);
		Optional.ofNullable(originTemplate).orElseThrow(() -> new BusinessException("原始模板不存在"));
		String url = wfwFormApiService.buildCreateUrl(originTemplate.getSign(), originTemplate.getKey(), loginUser.getUid(), market.getFid());
		return new RedirectView(url);
	}

	/**新增万能表单模板
	 * @Description
	 * @author wwb
	 * @Date 2022-03-29 18:40:33
	 * @param request
	 * @param marketId
	 * @param signUpWfwFormTemplate
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@PostMapping("wfw-form/add")
	@ResponseBody
	public RestRespDTO addWfwForm(HttpServletRequest request, @PathVariable Integer marketId, SignUpWfwFormTemplate signUpWfwFormTemplate) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		SignUpWfwFormTemplate systemApprovalTemplate = signUpWfwFormTemplateQueryService.getSystemNormalTemplate(SignUpWfwFormTemplate.TypeEnum.WFW_FORM);
		signUpWfwFormTemplate.asWfwForm(systemApprovalTemplate.getSign(), systemApprovalTemplate.getKey());
		signUpWfwFormTemplateHandleService.add(signUpWfwFormTemplate, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**新增审批模板
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:41:16
	 * @param request
	 * @param marketId
	 * @return org.springframework.web.servlet.view.RedirectView
	*/
	@GetMapping("approval/add")
	public RedirectView toAddApproval(HttpServletRequest request, @PathVariable Integer marketId) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Market market = marketValidationService.exist(marketId);
		SignUpWfwFormTemplate originTemplate = signUpWfwFormTemplateQueryService.getSystemNormalTemplate(SignUpWfwFormTemplate.TypeEnum.APPROVAL);
		Optional.ofNullable(originTemplate).orElseThrow(() -> new BusinessException("原始模板不存在"));
		String url = wfwApprovalApiService.buildCreateUrl(originTemplate.getSign(), originTemplate.getKey(), loginUser.getUid(), market.getFid());
		return new RedirectView(url);
	}

	/**新增审批模板
	 * @Description
	 * @author wwb
	 * @Date 2022-03-29 18:40:44
	 * @param request
	 * @param marketId
	 * @param signUpWfwFormTemplate
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@PostMapping("approval/add")
	@ResponseBody
	public RestRespDTO addApproval(HttpServletRequest request, @PathVariable Integer marketId, SignUpWfwFormTemplate signUpWfwFormTemplate) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		SignUpWfwFormTemplate systemWfwFormTemplate = signUpWfwFormTemplateQueryService.getSystemNormalTemplate(SignUpWfwFormTemplate.TypeEnum.APPROVAL);
		signUpWfwFormTemplate.asApproval(systemWfwFormTemplate.getSign(), systemWfwFormTemplate.getKey());
		signUpWfwFormTemplateHandleService.add(signUpWfwFormTemplate, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**修改万能表单模板
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:49:40
	 * @param request
	 * @param marketId
	 * @param id
	 * @return org.springframework.web.servlet.view.RedirectView
	*/
	@GetMapping("wfw-form/edit")
	public RedirectView toEditWfwForm(HttpServletRequest request, @PathVariable Integer marketId, Integer id) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		SignUpWfwFormTemplate signUpWfwFormTemplate = signUpWfwFormTemplateQueryService.getById(id);
		Optional.ofNullable(signUpWfwFormTemplate).orElseThrow(() -> new BusinessException("模板不存在"));
		String url = wfwFormApiService.buildEditUrl(signUpWfwFormTemplate.getFormId(), loginUser.getUid(), signUpWfwFormTemplate.getFid());
		return new RedirectView(url);
	}

	/**修改万能表单模板
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:43:57
	 * @param request
	 * @param marketId
	 * @param signUpWfwFormTemplate
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("wfw-form/edit")
	@ResponseBody
	public RestRespDTO editWfwForm(HttpServletRequest request, @PathVariable Integer marketId, SignUpWfwFormTemplate signUpWfwFormTemplate) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		signUpWfwFormTemplateHandleService.edit(signUpWfwFormTemplate, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**修改审批模板
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:49:54
	 * @param request
	 * @param marketId
	 * @param id
	 * @return org.springframework.web.servlet.view.RedirectView
	*/
	@GetMapping("approval/edit")
	public RedirectView toEditApproval(HttpServletRequest request, @PathVariable Integer marketId, Integer id) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		SignUpWfwFormTemplate signUpWfwFormTemplate = signUpWfwFormTemplateQueryService.getById(id);
		Optional.ofNullable(signUpWfwFormTemplate).orElseThrow(() -> new BusinessException("模板不存在"));
		String url = wfwApprovalApiService.buildEditUrl(signUpWfwFormTemplate.getFormId(), loginUser.getUid(), signUpWfwFormTemplate.getFid());
		return new RedirectView(url);
	}

	/**修改审批模板
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:44:19
	 * @param request
	 * @param marketId
	 * @param signUpWfwFormTemplate
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("approval/edit")
	@ResponseBody
	public RestRespDTO editApproval(HttpServletRequest request, @PathVariable Integer marketId, SignUpWfwFormTemplate signUpWfwFormTemplate) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		signUpWfwFormTemplateHandleService.add(signUpWfwFormTemplate, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**启用
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:42:35
	 * @param request
	 * @param marketId
	 * @param id
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("enable")
	@ResponseBody
	public RestRespDTO enable(HttpServletRequest request, @PathVariable Integer marketId, Integer id) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		signUpWfwFormTemplateHandleService.enable(id, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**禁用
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:43:05
	 * @param request
	 * @param marketId
	 * @param id
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("disable")
	@ResponseBody
	public RestRespDTO disable(HttpServletRequest request, @PathVariable Integer marketId, Integer id) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		signUpWfwFormTemplateHandleService.disable(id, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**删除
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:43:19
	 * @param request
	 * @param marketId
	 * @param id
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("delete")
	@ResponseBody
	public RestRespDTO delete(HttpServletRequest request, @PathVariable Integer marketId, Integer id) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		signUpWfwFormTemplateHandleService.delete(id, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

	/**排序
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-29 18:41:37
	 * @param request
	 * @param marketId
	 * @param ids
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("sort")
	@ResponseBody
	public RestRespDTO sort(HttpServletRequest request, @PathVariable Integer marketId, @RequestParam("ids[]") List<Integer> ids) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		signUpWfwFormTemplateHandleService.sort(ids, loginUser.buildOperateUserDTO());
		return RestRespDTO.success();
	}

}