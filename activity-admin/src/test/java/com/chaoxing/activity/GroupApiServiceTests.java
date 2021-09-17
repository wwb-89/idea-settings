package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.manager.group.GroupCreateParamDTO;
import com.chaoxing.activity.dto.manager.group.GroupCreateResultDTO;
import com.chaoxing.activity.dto.manager.group.GroupDTO;
import com.chaoxing.activity.service.manager.GroupApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className GroupApiServiceTests
 * @description
 * @blame wwb
 * @date 2021-09-17 14:10:06
 */
@SpringBootTest
public class GroupApiServiceTests {

    @Resource
    private GroupApiService groupApiService;

    @Test
    public void create() {
        GroupCreateParamDTO groupCreateParamDto = new GroupCreateParamDTO("活动引擎小组", 25418810);
        GroupCreateResultDTO groupCreateResultDto = groupApiService.create(groupCreateParamDto);
        System.out.println(JSON.toJSONString(groupCreateResultDto));
    }

    @Test
    public void get() {
        Integer id = 22126650;
        Integer createrPuid = 25418810;
        GroupDTO group = groupApiService.getGroup(id, createrPuid);
        System.out.println(JSON.toJSONString(group));
    }

}
