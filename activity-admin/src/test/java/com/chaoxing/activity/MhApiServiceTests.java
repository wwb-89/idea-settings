package com.chaoxing.activity;

import com.chaoxing.activity.service.manager.MhApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className MhApiServiceTests
 * @description
 * @blame wwb
 * @date 2021-12-28 18:22:56
 */
@SpringBootTest
public class MhApiServiceTests {

    @Resource
    private MhApiService mhApiService;

    @Test
    public void getEvaluationUrl() {
        Integer formUserId = 1;
        Integer formId = 1;
        String url = "https://www.baidu.com";
        String evaluationUrl = mhApiService.getEvaluationUrl(formUserId, formId, url);
        System.out.println(evaluationUrl);
    }

}
