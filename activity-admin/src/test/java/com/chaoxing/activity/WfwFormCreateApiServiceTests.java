package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateResultDTO;
import com.chaoxing.activity.model.SignUpWfwFormTemplate;
import com.chaoxing.activity.service.activity.engine.SignUpWfwFormTemplateService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

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
    private WfwFormApiService wfwFormApiService;
    @Resource
    private SignUpWfwFormTemplateService signUpWfwFormTemplateService;

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
    public void create() {
        Integer uid = 25418810;
        Integer fid = 117211;
        List<SignUpWfwFormTemplate> signUpWfwFormTemplates = signUpWfwFormTemplateService.listSystem();
        SignUpWfwFormTemplate signUpWfwFormTemplate = signUpWfwFormTemplates.get(0);
        WfwFormCreateParamDTO wfwFormCreateParam = WfwFormCreateParamDTO.builder()
                .formId(signUpWfwFormTemplate.getFormId())
                .originalFid(signUpWfwFormTemplate.getFid())
                .uid(uid)
                .fid(fid)
                .build();
        WfwFormCreateResultDTO wfwFormCreateResult = wfwFormApiService.create(wfwFormCreateParam);
        System.out.println(JSON.toJSONString(wfwFormCreateResult));
    }

}
