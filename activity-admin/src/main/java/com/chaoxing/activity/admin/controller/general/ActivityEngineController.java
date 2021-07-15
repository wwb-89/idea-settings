package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.service.activity.engine.ActivityEngineQueryService;
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
@RequestMapping("activity/engine/market/{marketId}")
public class ActivityEngineController {

    @Resource
    private ActivityEngineQueryService activityEngineQueryService;
    @Resource
    private WfwFormApiService formApiService;

    @LoginRequired
    @RequestMapping()
    public String index(HttpServletRequest request, Model model, @PathVariable Integer marketId, Integer wfwfid, Integer unitId, Integer state, Integer fid) {
        Integer realFid = Optional.ofNullable(wfwfid).
                orElse(
                        Optional.ofNullable(unitId).orElse(
                                Optional.ofNullable(state).orElse(
                                        Optional.ofNullable(fid).orElse(
                                                LoginUtils.getLoginUser(request).getFid()))));
        List<Template> templates = activityEngineQueryService.listTemplateByFid(realFid, marketId);
        List<WfwFormVO> wfwForms = formApiService.listOrgForm(realFid);
        model.addAttribute("fid", realFid);
        model.addAttribute("marketId", marketId);
        model.addAttribute("templates", templates);
        model.addAttribute("wfwForms", wfwForms);
        return "pc/engine/index";
    }
}
