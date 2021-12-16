package com.chaoxing.activity.service.event;

import com.chaoxing.activity.dto.event.user.UserQualifiedEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserQualifiedEventQueue;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**用户合格状态变更事件服务
 * @author wwb
 * @version ver 1.0
 * @className UserQualifiedStatusChangeEventService
 * @description
 * @blame wwb
 * @date 2021-12-16 17:47:38
 */
@Slf4j
@Service
public class UserQualifiedStatusChangeEventService {

    @Resource
    private UserQualifiedEventQueue userQualifiedEventQueue;

    public void handle(Integer uid, Integer activityId) {
        UserQualifiedEventOrigin userQualifiedEventOrigin = UserQualifiedEventOrigin.builder()
                .uid(uid)
                .activityId(activityId)
                .timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
                .build();
        userQualifiedEventQueue.push(userQualifiedEventOrigin);
    }

    public void handle(List<Integer> uids, Integer activityId) {
        if (CollectionUtils.isNotEmpty(uids)) {
            for (Integer uid : uids) {
                handle(uid, activityId);
            }
        }
    }

}