package com.chaoxing.activity.admin.controller.general;

import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**机构统计
 * @author wwb
 * @version ver 1.0
 * @className OrgStatController
 * @description
 * @blame wwb
 * @date 2021-05-27 16:53:22
 */
@Controller
@RequestMapping("stat/org")
public class OrgStatController {

    @Resource
    private TableFieldQueryService tableFieldQueryService;

    /**机构下的用户统计
     * @Description 
     * @author wwb
     * @Date 2021-05-27 17:03:15
     * @param request
     * @param model
     * @param wfwfid
     * @param unitId
     * @param state
     * @param fid
     * @return java.lang.String
    */
    @LoginRequired
    @RequestMapping("user")
    public String index(HttpServletRequest request, Model model, Integer wfwfid, Integer unitId, Integer state, Integer fid) {
        Integer realFid = Optional.ofNullable(wfwfid).orElse(Optional.ofNullable(unitId).orElse(Optional.ofNullable(state).orElse(fid)));
//        tableFieldQueryService.listOrgTableField(realFid, )
        return "pc/stat/org-user-stat";
    }

}