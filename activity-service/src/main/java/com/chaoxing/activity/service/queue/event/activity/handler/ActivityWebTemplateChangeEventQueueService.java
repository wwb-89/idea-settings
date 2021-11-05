package com.chaoxing.activity.service.queue.event.activity.handler;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.event.activity.ActivityWebTemplateChangeEventOrigin;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.MhApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityWebTemplateChangeEventQueueService
 * @description
 * @blame wwb
 * @date 2021-10-27 18:46:36
 */
@Slf4j
@Service
public class ActivityWebTemplateChangeEventQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private MhApiService mhApiService;

    @Resource
    private ActivityMapper activityMapper;

    public void handle(ActivityWebTemplateChangeEventOrigin eventOrigin) {
        if (eventOrigin == null) {
            return;
        }
        Integer activityId = eventOrigin.getActivityId();
        Activity activity = activityQueryService.getById(activityId);
        if (activity != null) {
            Integer pageId = activity.getPageId();
            if (pageId != null) {
                Integer websiteId = mhApiService.getWebsiteIdByPageId(pageId);
                activityMapper.update(null, new UpdateWrapper<Activity>()
                        .lambda()
                        .eq(Activity::getId, activityId)
                        .set(Activity::getWebsiteId, websiteId)
                );
            }
        }
    }

}
