package com.chaoxing.activity.admin.controller.api.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.ActivityTableField;
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
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/24 10:14 上午
 * <p>
 */
@RestController
@RequestMapping("api/activity/{activityId}/table-field/{tableFieldId}")
public class ActivityTableFieldApiController {

    @Resource
    private TableFieldHandleService tableFieldHandleService;

   /**
   * @Description
   * @author huxiaolong
   * @Date 2021-06-24 14:48:49
   * @param request
   * @param activityId
   * @param tableFieldId
   * @param activityTableFieldsStr
   * @return com.chaoxing.activity.dto.RestRespDTO
   */
    @LoginRequired
    @RequestMapping("config")
    public RestRespDTO config(HttpServletRequest request, @PathVariable Integer activityId, @PathVariable Integer tableFieldId, @RequestParam String activityTableFieldsStr) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        List<ActivityTableField> activityTableFields = JSON.parseArray(activityTableFieldsStr, ActivityTableField.class);
        tableFieldHandleService.activityConfig(activityId, tableFieldId, activityTableFields, loginUser);
        return RestRespDTO.success();
    }

}