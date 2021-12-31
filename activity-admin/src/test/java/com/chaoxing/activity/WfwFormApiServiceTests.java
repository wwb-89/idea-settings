package com.chaoxing.activity;

import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className WfwFormApiServiceTests
 * @description
 * @blame wwb
 * @date 2021-08-17 14:44:19
 */
@SpringBootTest
public class WfwFormApiServiceTests {

    @Resource
    private WfwFormApiService wfwFormApiService;

    @Test
    public void getCreateUrl() {
        String url = wfwFormApiService.buildEditFormUrl(117211, 25418810, null, 1);
        System.out.println(url);
    }

    @Test
    public void getFormAdminUrl() {
        Integer formId = 132105;
        Integer fid = 117211;
        Integer uid = 25418810;
        String adminUrl = wfwFormApiService.getFormAdminUrl(formId, fid, uid);
        System.out.println(adminUrl);
    }

    @Test
    public void listData() {
        Integer formId = 100965;
        Integer fid = 177512;
        List<FormDataDTO> formDatas = wfwFormApiService.listFormRecord(formId, fid);
    }

    @Test
    public void getData() {
        Integer formUserId = 139704360;
        Integer formId = 100965;
        Integer fid = 177512;
        FormDataDTO formRecord = wfwFormApiService.getFormRecord(formUserId, formId, fid);
    }

}
