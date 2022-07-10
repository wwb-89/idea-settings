package com.chaoxing.activity.service.event;

import com.chaoxing.activity.dto.event.user.UserStatSummaryChangeEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserStatSummaryChangeEventQueue;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**用户汇总数据改变事件服务
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryChangeEventService
 * @description
 * @blame wwb
 * @date 2021-11-03 14:52:39
 */
@Slf4j
@Service
public class UserStatSummaryChangeEventService {

    @Resource
    private UserStatSummaryChangeEventQueue userStatSummaryChangeEventQueue;

    public void handle(Integer uid, Integer activityId) {
        Long timestamp = DateUtils.date2Timestamp(LocalDateTime.now());
        UserStatSummaryChangeEventOrigin userStatSummaryChangeEventOrigin = UserStatSummaryChangeEventOrigin.builder()
                .activityId(activityId)
                .uid(uid)
                .timestamp(timestamp)
                .build();
        userStatSummaryChangeEventQueue.push(userStatSummaryChangeEventOrigin);
    }

}