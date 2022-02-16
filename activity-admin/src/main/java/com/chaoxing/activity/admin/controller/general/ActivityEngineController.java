package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.ConditionDTO;
import com.chaoxing.activity.dto.OptionDTO;
import com.chaoxing.activity.dto.engine.ActivityEngineDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.IconQueryService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.classify.component.ClassifyShowComponentQueryService;
import com.chaoxing.activity.service.activity.engine.ActivityEngineQueryService;
import com.chaoxing.activity.service.activity.engine.SignUpWfwFormTemplateService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.vo.manager.WfwFormVO;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/7/6 2:17 下午
 * <p>
 */
@Controller
@RequestMapping("market/{marketId}/template")
public class ActivityEngineController {

    @Resource
    private ActivityEngineQueryService activityEngineQueryService;
    @Resource
    private WfwFormApiService formApiService;
    @Resource
    private TemplateQueryService templateQueryService;
    @Resource
    private SignUpWfwFormTemplateService signUpWfwFormTemplateService;
    @Resource
    private IconQueryService iconQueryService;
    @Resource
    private ClassifyQueryService classifyQueryService;
    @Resource
    private TemplateComponentService templateComponentService;
    @Resource
    private ClassifyShowComponentQueryService classifyShowComponentQueryService;

    @LoginRequired
    @RequestMapping("{templateId}")
    public String templateIndex(HttpServletRequest request, Model model, @PathVariable Integer marketId, @PathVariable Integer templateId, Integer wfwfid, Integer unitId, Integer state, Integer fid) {
        Integer realFid = Optional.ofNullable(wfwfid).
                orElse(
                        Optional.ofNullable(unitId).orElse(
                                Optional.ofNullable(state).orElse(
                                        Optional.ofNullable(fid).orElse(
                                                LoginUtils.getLoginUser(request).getFid()))));
        return index(model, marketId, templateId, realFid);
    }

    @RequestMapping("")
    public String firstTemplateIndex(Model model, @PathVariable Integer marketId) {
        Template template = templateQueryService.getMarketFirstTemplate(marketId);
        Integer templateId = Optional.ofNullable(template).map(Template::getId).orElse(null);
        return index(model, marketId, templateId, Optional.ofNullable(template).map(Template::getFid).orElse(null));
    }

    private String index(Model model, Integer marketId, Integer templateId, Integer fid) {
        List<WfwFormVO> wfwForms = formApiService.listOrgForm(fid);
        model.addAttribute("cloudDomain", DomainConstant.CLOUD_RESOURCE);
        model.addAttribute("fid", fid);
        model.addAttribute("marketId", marketId);
        model.addAttribute("templateId", templateId);
        ActivityEngineDTO templateInfo = activityEngineQueryService.findEngineTemplateInfo(templateId, marketId);
        model.addAttribute("templateInfo", templateInfo);
        model.addAttribute("wfwForms", wfwForms);
        model.addAttribute("wfwFormTemplates", signUpWfwFormTemplateService.listNormal());
        model.addAttribute("wfwFormApprovalTemplates", signUpWfwFormTemplateService.listApproval());
        model.addAttribute("conditionEnums", ConditionDTO.listWithoutNoLimit());
        model.addAttribute("icons", iconQueryService.list());
        // 市场下的所有分类
        List<Classify> classifies = classifyQueryService.listMarketClassifies(marketId);
        model.addAttribute("classifies", classifies);
        // 模板下的所有组件
        List<TemplateComponent> templateComponents = templateComponentService.listSupperTemplateComponentByTemplateId(templateId);
        model.addAttribute("templateComponents", templateComponents);
        // 分类关联显示组件
        List<ClassifyShowComponent> classifyShowComponents = classifyShowComponentQueryService.listByTemplateId(templateId);
        model.addAttribute("classifyShowComponents", classifyShowComponents);
        // 自定义接口调用时机列表
        model.addAttribute("callTimings", OptionDTO.listInterfaceCallTiming());
        // 开关的系统组件code列表
        List<String> switchBtnComptCodes = Arrays.stream(Component.SystemComponentCodeEnum.values()).filter(Component.SystemComponentCodeEnum::getSwitchBtn).map(Component.SystemComponentCodeEnum::getValue).collect(Collectors.toList());
        model.addAttribute("switchBtnComptCodes", switchBtnComptCodes);
        return "pc/engine/index";
    }
}
