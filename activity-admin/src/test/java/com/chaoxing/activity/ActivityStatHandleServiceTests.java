package com.chaoxing.activity;

import com.chaoxing.activity.service.activity.stat.ActivityStatHandleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatHandleServiceTests
 * @description
 * @blame wwb
 * @date 2021-12-22 20:01:32
 */
@SpringBootTest
public class ActivityStatHandleServiceTests {

    @Resource
    private ActivityStatHandleService activityStatHandleService;

    @Test
    public void handleTask() {
        Integer taskId = 644;
        activityStatHandleService.handleTask(taskId);
    }

}
