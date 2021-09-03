package com.chaoxing.activity;

import com.chaoxing.activity.service.manager.WfwFormCreateApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className WfwFormCreateApiServiceTests
 * @description
 * @blame wwb
 * @date 2021-08-17 14:44:19
 */
@SpringBootTest
public class WfwFormCreateApiServiceTests {

    @Resource
    private WfwFormCreateApiService wfwFormCreateApiService;

    @Test
    public void create() {
        String url = wfwFormCreateApiService.buildCreateFormUrl(117211, 25418810, null, "normal");
        System.out.println(url);
    }

    @Test
    public void getFormAdminUrl() {
        Integer formId = 94570;
        Integer fid = 117211;
        Integer uid = 25418810;
        String adminUrl = wfwFormCreateApiService.getFormAdminUrl(formId, fid, uid);
        System.out.println(adminUrl);
    }

}
