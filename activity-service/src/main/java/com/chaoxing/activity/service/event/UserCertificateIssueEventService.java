package com.chaoxing.activity.service.event;

import com.chaoxing.activity.dto.event.user.UserCertificateIssueEventOrigin;
import com.chaoxing.activity.service.queue.event.user.UserCertificateIssueEventQueue;
import com.chaoxing.activity.util.DateUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**用户证书发放事件服务
 * @author wwb
 * @version ver 1.0
 * @className UserCertificateIssueEventService
 * @description
 * @blame wwb
 * @date 2021-12-16 16:06:48
 */
@Slf4j
@Service
public class UserCertificateIssueEventService {

    @Resource
    private UserCertificateIssueEventQueue userCertificateIssueEventQueue;

    public void issue(Integer uid, Integer activityId) {
        UserCertificateIssueEventOrigin userCertificateIssueEventOrigin = UserCertificateIssueEventOrigin.builder()
                .uid(uid)
                .activityId(activityId)
                .timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
                .build();
        userCertificateIssueEventQueue.push(userCertificateIssueEventOrigin);

    }

    public void issue(List<Integer> uids, Integer activityId) {
        if (CollectionUtils.isNotEmpty(uids)) {
            for (Integer uid : uids) {
                issue(uid, activityId);
            }
        }
    }

}