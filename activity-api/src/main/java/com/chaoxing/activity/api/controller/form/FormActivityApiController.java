package com.chaoxing.activity.api.controller.form;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.form.FormCreateActivity;
import com.chaoxing.activity.service.queue.activity.FormActivityCreateQueueService;
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
    private FormActivityCreateQueueService formActivityCreateQueueService;

    /**创建活动
     * @Description 通过活动申报来创建活动
     * 根据fid、表单id、表单记录id查询表单的数据
     * 判断是审核通过则创建一个活动（当活动不存在的时候）
     * @author wwb
     * @Date 2021-05-10 16:26:47
     * @param deptId
     * @param formId
     * @param formUserId
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @RequestMapping("create")
    public RestRespDTO formCreateActivity(@RequestParam Integer deptId, Integer formId, @RequestParam(value = "indexID") Integer formUserId, String flag) {
        FormCreateActivity formCreateActivity = FormCreateActivity.builder()
                .fid(deptId)
                .formId(formId)
                .formUserId(formUserId)
                .flag(flag)
                .build();
        formActivityCreateQueueService.push(formCreateActivity);
        return RestRespDTO.success();
    }

}
