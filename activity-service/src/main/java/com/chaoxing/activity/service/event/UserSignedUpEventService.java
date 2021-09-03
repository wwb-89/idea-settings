package com.chaoxing.activity.service.event;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.UserSignedUpNoticeHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Optional;

/**用户报名成功事件
 * @author wwb
 * @version ver 1.0
 * @className UserSignedUpEventService
 * @description
 * @blame wwb
 * @date 2021-09-01 20:01:11
 */
@Slf4j
@Service
public class UserSignedUpEventService {

    @Resource
    private UserSignedUpNoticeHandleService activityIsAboutStartHandleService;

    public void handle(Activity activity, Integer uid) {
        // 活动即将开始通知
        activityIsAboutStartHandleService.sendSignedUpActivityIsAboutStartNotice(activity, new ArrayList(){{add(uid);}});
        // 报名成功通知
        Boolean signedUpNotice = Optional.ofNullable(activity.getSignedUpNotice()).orElse(false);
        if (signedUpNotice) {
            activityIsAboutStartHandleService.userSignedUpNotice(activity, uid);
        }
    }

}
