package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.ConditionDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.MarketSignUpConfig;
import com.chaoxing.activity.model.SignUpCondition;
import com.chaoxing.activity.model.Tag;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import com.chaoxing.activity.service.activity.market.MarketSignupConfigService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.tag.TagQueryService;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.vo.manager.WfwFormFieldVO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/3 16:11
 * <p>
 */
@Slf4j
@Controller
@RequestMapping("activity/{activityId}/setting")
public class ActivitySettingController {

    @Resource
    private ActivityValidationService activityValidationService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private TemplateComponentService templateComponentService;
    @Resource
    private ClassifyQueryService classifyQueryService;
    @Resource
    private ActivityScopeQueryService activityScopeQueryService;
    @Resource
    private SignApiService signApiService;
    @Resource
    private WfwGroupApiService wfwGroupApiService;
    @Resource
    private WfwContactApiService wfwContactApiService;
    @Resource
    private ActivityMenuService activityMenuService;
    @Resource
    private MarketSignupConfigService marketSignupConfigService;
    @Resource
    private SignUpConditionService signUpConditionService;
    @Resource
    private WfwFormApiService formApiService;
    @Resource
    private TagQueryService tagQueryService;

    @RequestMapping("index")
    public String settingIndex(Model model, @PathVariable Integer activityId) {
//		todo 暂时屏蔽校验
//		Activity activity = activityValidationService.manageAble(activityId, operateUid);
        Activity activity = activityValidationService.activityExist(activityId);

        model.addAttribute("activityId", activityId);
        model.addAttribute("openSignUp", templateComponentService.exitSignUpComponent(activity.getTemplateId()));
        model.addAttribute("mainDomain", DomainConstant.MAIN);
        return "pc/activity/setting/index";
    }

    /**基本信息设置页面
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-03 16:13:03
    * @param request
    * @param model
    * @param activityId
    * @return java.lang.String
    */
    @RequestMapping("basic-info/edit")
    public String basicInfoEdit(HttpServletRequest request, Model model, @PathVariable Integer activityId, @RequestParam(defaultValue = "0") Integer strict) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Activity activity = activityValidationService.manageAble(activityId, loginUser.getUid());
        ActivityCreateParamDTO createParamDTO = activityQueryService.packageActivityCreateParamByActivity(activity);
        model.addAttribute("signId", activity.getSignId());
        model.addAttribute("activity", createParamDTO);
        model.addAttribute("templateComponents", templateComponentService.listBasicInfoTemplateComponents(activity.getTemplateId(), activity.getCreateFid()));
        // 活动类型列表
        model.addAttribute("activityTypes", activityQueryService.listActivityType());
        // 活动分类列表范围
        if (activity.getMarketId() == null) {
            model.addAttribute("activityClassifies", classifyQueryService.listOrgClassifies(activity.getCreateFid()));
        } else {
            model.addAttribute("activityClassifies", classifyQueryService.listMarketClassifies(activity.getMarketId()));
        }
        // 活动发布范围
        List<WfwAreaDTO> wfwRegionalArchitectures = activityScopeQueryService.listByActivityId(activityId);
        model.addAttribute("participatedOrgs", wfwRegionalArchitectures);
        String activityFlag = activity.getActivityFlag();
        model.addAttribute("activityFlag", activityFlag);
        model.addAttribute("strict", strict);
        // 活动标签
        Integer marketId = activity.getMarketId();
        Integer activityCreateFid = activity.getCreateFid();
        List<Tag> tags = Optional.ofNullable(marketId).map(v -> tagQueryService.listMarketTag(marketId)).orElse(tagQueryService.listOrgTag(activityCreateFid));
        model.addAttribute("tags", tags);
        model.addAttribute("workDomain", DomainConstant.WORK);
        model.addAttribute("noteDomain", DomainConstant.NOTE);
        model.addAttribute("cloudDomain", DomainConstant.CLOUD_RESOURCE);
        model.addAttribute("mainDomain", DomainConstant.MAIN);
        return "pc/activity/setting/basic-info-edit";
    }

    /**基本信息设置页面
    * @Description
    * @author huxiaolong
    * @Date 2021-08-03 16:13:03
    * @param request
    * @param model
    * @param activityId
    * @return java.lang.String
    */
    @RequestMapping("basic-info/view")
    public String basicInfoView(HttpServletRequest request, Model model, @PathVariable Integer activityId, @RequestParam(defaultValue = "0") Integer strict) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Activity activity = activityValidationService.manageAble(activityId, loginUser.getUid());
        ActivityCreateParamDTO createParamDTO = activityQueryService.packageActivityCreateParamByActivity(activity);
        model.addAttribute("activity", createParamDTO);
        model.addAttribute("templateComponents", templateComponentService.listBasicInfoTemplateComponents(activity.getTemplateId(), activity.getCreateFid()));
        List<String> participateScopes = activityScopeQueryService.listByActivityId(activityId).stream().map(WfwAreaDTO::getName).collect(Collectors.toList());
        model.addAttribute("participateScopes", participateScopes);
        model.addAttribute("mainDomain", DomainConstant.MAIN);
        model.addAttribute("cloudDomain", DomainConstant.CLOUD_RESOURCE);
        return "pc/activity/setting/basic-info-view";
    }

    /**报名设置编辑页面
    * @Description
    * @author huxiaolong
    * @Date 2021-08-03 16:13:03
    * @param request
    * @param model
    * @param activityId
    * @return java.lang.String
    */
    @RequestMapping("sign-up")
    public String signUpSetting(HttpServletRequest request, Model model, @PathVariable Integer activityId) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        Activity activity = activityValidationService.manageAble(activityId, loginUser.getUid());
        ActivityCreateParamDTO createParamDTO = activityQueryService.packageActivityCreateParamByActivity(activity);
        // 报名签到
        Integer signId = activity.getSignId();
        SignCreateParamDTO sign = SignCreateParamDTO.builder().build();
        if (signId != null) {
            sign = signApiService.getCreateById(signId);
        }
        model.addAttribute("activity", createParamDTO);
        model.addAttribute("signUpTemplateComponents", templateComponentService.listSignUpTemplateComponents(activity.getTemplateId()));
        model.addAttribute("sign", sign);
        // 报名范围
        // 微服务组织架构
        model.addAttribute("wfwGroups", WfwGroupDTO.perfectWfwGroups(wfwGroupApiService.listGroupByFid(loginUser.getFid())));
        // 通讯录组织架构
        model.addAttribute("contactGroups", WfwGroupDTO.perfectWfwGroups(wfwContactApiService.listUserContactOrgsByFid(loginUser.getFid())));
        String activityFlag = activity.getActivityFlag();
        model.addAttribute("activityFlag", activityFlag);
        List<SignUpCondition> signUpConditions = signUpConditionService.listEditActivityConditions(activityId, activity.getTemplateId());
        // 获取表单结构map
        List<String> formIds = signUpConditions.stream().map(SignUpCondition::getOriginIdentify).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, List<WfwFormFieldVO>> formFieldStructures = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(formIds)) {
            formFieldStructures = formIds.stream().collect(Collectors.toMap(
                    v -> v,
                    v -> formApiService.getFormStructure(Integer.valueOf(v), activity.getCreateFid())
                            .stream().map(WfwFormFieldVO::buildFromWfwFormFieldDTO)
                            .collect(Collectors.toList()),
                    (v1, v2) -> v2));
        }
        model.addAttribute("formFieldStructures", formFieldStructures);
        model.addAttribute("sucTplComponentIds", signUpConditionService.listActivityEnabledTemplateComponentId(activityId));
        model.addAttribute("signUpConditions", signUpConditions);
        model.addAttribute("conditionEnums", ConditionDTO.list());
        // 市场报名配置
        MarketSignUpConfig marketSignUpConfig = marketSignupConfigService.get(createParamDTO.getMarketId());
        model.addAttribute("marketSignUpConfig", marketSignUpConfig);
        model.addAttribute("mainDomain", DomainConstant.MAIN);
        model.addAttribute("wfwFormDomain", DomainConstant.WFW_FORM_API);
        model.addAttribute("signWebDomain", DomainConstant.SIGN_WEB);
        return "pc/activity/setting/sign-up";
    }

    /**报名设置编辑页面
    * @Description
    * @author huxiaolong
    * @Date 2021-08-03 16:13:03
    * @param model
    * @param activityId
    * @return java.lang.String
    */
    @RequestMapping("menu")
    public String menuSetting(Model model, @PathVariable Integer activityId) {
        model.addAttribute("activityId", activityId);
        model.addAttribute("activityMenus", activityMenuService.listActivityMenuConfig(activityId));
        model.addAttribute("menuList", activityMenuService.listMenu());
        model.addAttribute("mainDomain", DomainConstant.MAIN);
        return "pc/activity/setting/menu";
    }
}
