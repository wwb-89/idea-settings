package com.chaoxing.activity;

import com.chaoxing.activity.service.manager.wfw.WfwApprovalApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className FormApprovalApiServiceTests
 * @description
 * @blame wwb
 * @date 2021-05-10 18:09:21
 */
@SpringBootTest
public class FormApprovalApiServiceTests {

    @Resource
    private WfwApprovalApiService formApprovalApiService;

    @Test
    public void getFormData() {
        Integer fid = 139378;
        Integer formId = 17251;
        Integer formUserId = 1959060;
        formApprovalApiService.getFormRecord(formUserId, formId, fid);
    }

    @Test
    public void listFormData() {
        Integer fid = 139378;
        Integer formId = 17251;
        formApprovalApiService.listFormRecord(fid, formId);
    }

}
