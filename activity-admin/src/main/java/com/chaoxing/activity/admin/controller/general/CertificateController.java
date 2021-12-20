package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityTableField;
import com.chaoxing.activity.model.TableField;
import com.chaoxing.activity.model.TableFieldDetail;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

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
    @Resource
    private TableFieldQueryService tableFieldQueryService;

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
        List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listTableFieldDetail(TableField.Type.CERTIFICATE_ISSUE, TableField.AssociatedType.ACTIVITY);
        List<ActivityTableField> activityTableFields = tableFieldQueryService.listActivityTableField(activityId, TableField.Type.CERTIFICATE_ISSUE, TableField.AssociatedType.ACTIVITY);
        Integer tableFieldId = Optional.ofNullable(tableFieldDetails).orElse(Lists.newArrayList()).stream().findFirst().map(TableFieldDetail::getTableFieldId).orElse(null);

        model.addAttribute("tableFieldDetails", tableFieldDetails);
        model.addAttribute("activityTableFields", activityTableFields);
        model.addAttribute("tableFieldId", tableFieldId);
        model.addAttribute("activity", activity);
        model.addAttribute("mainDomain", DomainConstant.MAIN);
        return "pc/certificate/certificate-index";
    }

}