package com.chaoxing.activity.task;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.ActivityWebsiteIdSyncQueueService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className QueueApiController
 * @description
 * @blame wwb
 * @date 2021-05-27 10:35:52
 */
@RestController
@RequestMapping("queue")
public class QueueApiController {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityWebsiteIdSyncQueueService activityWebsiteIdSyncQueueService;

    @RequestMapping("activity-website-id-sync")
    public RestRespDTO activityWebsiteIdSync() {
        List<Integer> activityIds = activityQueryService.listEmptyWebsiteIdActivityId();
        if (CollectionUtils.isNotEmpty(activityIds)) {
            for (Integer activityId : activityIds) {
                activityWebsiteIdSyncQueueService.add(activityId);
            }
        }
        return RestRespDTO.success();
    }

}
