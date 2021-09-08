package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.SignActivityManageIndexDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.engine.ActivityEngineQueryService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeQueryService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
    private ActivityEngineQueryService activityEngineQueryService;
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
    private TemplateQueryService templateQueryService;

    @RequestMapping("index")
    public String settingIndex(Model model, @PathVariable Integer activityId) {
//		todo 暂时屏蔽校验
//		Activity activity = activityValidationService.manageAble(activityId, operateUid);
        Activity activity = activityValidationService.activityExist(activityId);

        model.addAttribute("activityId", activityId);
        model.addAttribute("openSignUp", templateQueryService.exitSignUpComponent(activity.getTemplateId()));
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
        model.addAttribute("templateComponents", activityEngineQueryService.listBasicInfoTemplateComponents(activity.getTemplateId(), activity.getCreateFid()));
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
        model.addAttribute("templateComponents", activityEngineQueryService.listBasicInfoTemplateComponents(activity.getTemplateId(), activity.getCreateFid()));
        List<String> participateScopes = activityScopeQueryService.listByActivityId(activityId).stream().map(WfwAreaDTO::getName).collect(Collectors.toList());
        model.addAttribute("participateScopes", participateScopes);
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
        model.addAttribute("templateComponents", activityEngineQueryService.listSignUpTemplateComponents(activity.getTemplateId()));
        model.addAttribute("sign", sign);
        // 报名范围
        // 微服务组织架构
        model.addAttribute("wfwGroups", WfwGroupDTO.perfectWfwGroups(wfwGroupApiService.listGroupByFid(loginUser.getFid())));
        // 通讯录组织架构
        model.addAttribute("contactGroups", WfwGroupDTO.perfectWfwGroups(wfwContactApiService.listUserContactOrgsByFid(loginUser.getFid())));
        String activityFlag = activity.getActivityFlag();
        model.addAttribute("activityFlag", activityFlag);
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
        return "pc/activity/setting/menu";
    }
}
