package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.service.activity.engine.ActivityEngineQueryService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.WfwFormApiService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.chaoxing.activity.vo.manager.WfwFormVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

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

//    @LoginRequired
//    @RequestMapping()
//    public String index(HttpServletRequest request, Model model, @PathVariable Integer marketId, Integer wfwfid, Integer unitId, Integer state, Integer fid) {
//        Integer realFid = Optional.ofNullable(wfwfid).
//                orElse(
//                        Optional.ofNullable(unitId).orElse(
//                                Optional.ofNullable(state).orElse(
//                                        Optional.ofNullable(fid).orElse(
//                                                LoginUtils.getLoginUser(request).getFid()))));
//        List<Template> templates = activityEngineQueryService.listTemplateByFid(realFid, marketId);
//        List<WfwFormVO> wfwForms = formApiService.listOrgForm(realFid);
//        model.addAttribute("fid", realFid);
//        model.addAttribute("marketId", marketId);
//        model.addAttribute("templates", templates);
//        model.addAttribute("wfwForms", wfwForms);
//        return "pc/engine/index";
//    }

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

    @RequestMapping("first-template")
    public String firstTemplateIndex(Model model, @PathVariable Integer marketId, Integer fid) {
        Template template = templateQueryService.getMarketFirstTemplate(marketId);
        Integer templateId = Optional.ofNullable(template).map(Template::getId).orElse(null);
        return index(model, marketId, templateId, Optional.ofNullable(template).map(Template::getFid).orElse(null));
    }

    private String index(Model model, Integer marketId, Integer templateId, Integer fid) {
        List<WfwFormVO> wfwForms = formApiService.listOrgForm(fid);
        model.addAttribute("fid", fid);
        model.addAttribute("marketId", marketId);
        model.addAttribute("templateId", templateId);
        model.addAttribute("templateInfo", activityEngineQueryService.findEngineTemplateInfo(templateId));
        model.addAttribute("wfwForms", wfwForms);
        return "pc/engine/index";
    }
}
