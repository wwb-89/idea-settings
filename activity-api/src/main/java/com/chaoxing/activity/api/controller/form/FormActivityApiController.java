package com.chaoxing.activity.api.controller.form;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.manager.FormApprovalApiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className FormActivityApiController
 * @description
 * @blame wwb
 * @date 2021-05-11 14:51:41
 */
@RestController
@RequestMapping("form/activity")
public class FormActivityApiController {

    @Resource
    private FormApprovalApiService formApprovalApiService;

    /**创建活动
     * @Description
     * 根据fid、表单id、表单记录id查询表单的数据
     * 判断是审核通过则创建一个活动（当活动不存在的时候）
     * @author wwb
     * @Date 2021-05-10 16:26:47
     * @param deptId
     * @param formId
     * @param indexID
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @RequestMapping("create")
    public RestRespDTO formCreateActivity(@RequestParam Integer deptId, Integer formId, Integer indexID, String flag) {
        formApprovalApiService.addActivity(deptId, formId, indexID, flag);
        return RestRespDTO.success();
    }

}
