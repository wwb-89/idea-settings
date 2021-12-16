package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.util.constant.DomainConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**证书
 * @author wwb
 * @version ver 1.0
 * @className CertificateController
 * @description
 * @blame wwb
 * @date 2021-12-16 11:10:39
 */
@Slf4j
@Controller
@RequestMapping("activity/{activityId}/certificate")
public class CertificateController {

    @Resource
    private ActivityValidationService activityValidationService;

    /**证书管理主页
     * @Description 
     * @author wwb
     * @Date 2021-12-16 11:14:20
     * @param request
     * @param model
     * @param activityId
     * @return java.lang.String
    */
    @RequestMapping
    public String index(HttpServletRequest request, Model model, @PathVariable Integer activityId) {
        Activity activity = activityValidationService.activityExist(activityId);
        model.addAttribute("activity", activity);
        model.addAttribute("mainDomain", DomainConstant.MAIN);
        return "pc/certificate/certificate-index";
    }

}