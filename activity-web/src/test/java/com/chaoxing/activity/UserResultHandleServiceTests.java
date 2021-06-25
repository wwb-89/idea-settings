package com.chaoxing.activity;

import com.chaoxing.activity.service.user.result.UserResultHandleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserResultHandleServiceTests
 * @description
 * @blame wwb
 * @date 2021-06-25 16:54:27
 */
@SpringBootTest
public class UserResultHandleServiceTests {

    @Resource
    private UserResultHandleService userResultHandleService;

    @Test
    public void updateUserResult() {
        Integer uid = 25418810;
        Integer activityId = 1;
        userResultHandleService.updateUserResult(uid, activityId);
    }

}
