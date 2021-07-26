package com.chaoxing.activity.admin.controller.general;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/7/26 4:07 下午
 * <p>
 */
@Slf4j
@Controller
@RequestMapping()
public class RuleSettingController {


    /**规则配置主页
    * @Description
    * @author huxiaolong
    * @Date 2021-07-26 16:09:36
    * @param
    * @return java.lang.String
    */
    @RequestMapping("rule/setting")
    public String index(HttpServletRequest request, Model model, Integer marketId) {
        model.addAttribute("marketId", marketId);
        return "pc/rule/setting";
    }

}
