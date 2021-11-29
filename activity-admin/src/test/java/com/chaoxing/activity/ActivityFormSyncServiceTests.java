package com.chaoxing.activity;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.queue.activity.handler.WfwFormSyncActivityQueueService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityFormSyncServiceTests
 * @description
 * @blame wwb
 * @date 2021-09-03 16:29:58
 */
@SpringBootTest
public class ActivityFormSyncServiceTests {

    @Resource
    private WfwFormSyncActivityQueueService activityFormSyncService;

    @Test
    public void getActivityFromFormInfo() {
        //http://api.hd.chaoxing.com/redirect/activity-portal/from/wfw-form?formId=100965&formUserId=98101926&fid=177512&uid=22651866
        Integer fid = 177512;
        Integer formId = 100965;
        Integer formUserId = 98101926;
        activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
    }

    @Test
    public void syncCreateActivity() {
        Integer fid = 177512;
        Integer formId = 100965;
        Integer formUserId = 98105033;
        activityFormSyncService.add(fid, formId, formUserId, 30, Activity.ActivityFlagEnum.THREE_CONFERENCE_ONE_LESSON.getValue());
    }

}
