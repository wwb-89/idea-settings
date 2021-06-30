package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.MarketTableField;
import com.chaoxing.activity.model.OrgTableField;
import com.chaoxing.activity.service.tablefield.TableFieldHandleService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrgTableFieldApiController
 * @description
 * @blame wwb
 * @date 2021-05-28 14:40:16
 */
@RestController
@RequestMapping("api/org/{fid}/table-field/{tableFieldId}")
public class OrgTableFieldApiController {

    @Resource
    private TableFieldHandleService tableFieldHandleService;

    /**机构配置表格字段
     * @Description 
     * @author wwb
     * @Date 2021-05-28 14:53:31
     * @param request
     * @param fid
     * @param tableFieldId
     * @param orgTableFieldsStr
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @LoginRequired
    @RequestMapping("config")
    public RestRespDTO config(HttpServletRequest request, @PathVariable Integer fid, @PathVariable Integer tableFieldId, @RequestParam String orgTableFieldsStr) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        List<OrgTableField> orgTableFields = JSON.parseArray(orgTableFieldsStr, OrgTableField.class);
        tableFieldHandleService.orgConfig(fid, tableFieldId, orgTableFields, loginUser);
        return RestRespDTO.success();
    }

    /**机构活动市场表格配置字段
     * @Description
     * @author huxiaolong
     * @Date 2021-06-24 14:48:49
     * @param request
     * @param fid
     * @param tableFieldId
     * @param marketTableFieldsStr
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @LoginRequired
    @RequestMapping("/market/config")
    public RestRespDTO config(HttpServletRequest request,
                              @PathVariable Integer fid,
                              @PathVariable Integer tableFieldId,
                              @RequestParam String marketTableFieldsStr,
                              String activityFlag) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        List<MarketTableField> marketTableFields = JSON.parseArray(marketTableFieldsStr, MarketTableField.class);
        tableFieldHandleService.marketTableFieldConfig(fid, activityFlag, tableFieldId, marketTableFields, loginUser);
        return RestRespDTO.success();
    }
}