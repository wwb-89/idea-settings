package com.chaoxing.activity;

import com.chaoxing.activity.service.manager.FormApprovalApiService;
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
    private FormApprovalApiService formApprovalApiService;

    @Test
    public void getFormData() {
        Integer fid = 139378;
        Integer formId = 17251;
        Integer formUserId = 1603876;
        formApprovalApiService.getFormData(fid, formId, formUserId);
    }

    @Test
    public void listFormData() {
        Integer fid = 139378;
        Integer formId = 17251;
        formApprovalApiService.listFormData(fid, formId);
    }

    @Test
    public void createActivity() {
        Integer fid = 139378;
        Integer formId = 17251;
        Integer formUserId = 1861393;
        formApprovalApiService.createActivity(fid, formId, formUserId, null);
    }

}
