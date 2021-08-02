package com.chaoxing.activity;

import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className WfwRegionalArchitectureApiServiceTests
 * @description
 * @blame wwb
 * @date 2021-06-30 17:55:21
 */
@SpringBootTest
public class WfwAreaApiServiceTests {

    @Resource
    private WfwAreaApiService wfwAreaApiService;

    @Test
    public void listCodeByFid() {
        Integer fid = 2885;
        List<String> codes = wfwAreaApiService.listCodeByFid(fid);
        System.out.println(codes);
    }

}
