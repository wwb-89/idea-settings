package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.dto.event.activity.ActivityAboutEndEventOrigin;
import com.chaoxing.activity.dto.notice.MarketNoticeTemplateDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.SystemNoticeTemplate;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.notice.MarketNoticeTemplateService;
import com.chaoxing.activity.service.queue.IDelayedQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**活动即将结束事件任务队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityAboutEndEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-26 18:59:09
 */
@Slf4j
@Service
public class ActivityAboutEndEventQueue implements IDelayedQueue<ActivityAboutEndEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_about_end";
    private static final Integer ABOUT_START_HOURS = 24;

    @Resource
    private MarketNoticeTemplateService marketNoticeTemplateService;
    @Resource
    private ActivityQueryService activityQueryService;

    @Resource
    private RedissonClient redissonClient;


    public void push(ActivityAboutEndEventOrigin eventOrigin) {
        remove(eventOrigin);
        // 根据活动id，查询活动市场id及对应的市场通知模板，若市场通知模板不存在，则采用对应的系统通知模板
        Activity activity = activityQueryService.getById(eventOrigin.getActivityId());
        MarketNoticeTemplateDTO noticeTemplate = marketNoticeTemplateService.getMarketOrSystemNoticeTemplate(activity.getMarketId(), SystemNoticeTemplate.NoticeTypeEnum.ACTIVITY_ABOUT_START.getValue());

        LocalDateTime endTime = eventOrigin.getEndTime();
        Integer delayHour = Optional.ofNullable(noticeTemplate).map(MarketNoticeTemplateDTO::getDelayHour).orElse(ABOUT_START_HOURS);
        Integer delayMinute = Optional.ofNullable(noticeTemplate).map(MarketNoticeTemplateDTO::getDelayMinute).orElse(0);
        LocalDateTime noticeTime = endTime.minusHours(delayHour).minusMinutes(delayMinute);
        if (noticeTime.isBefore(LocalDateTime.now())) {
            // 通知时间已经过了，忽略
            return;
        }
        push(redissonClient, KEY, eventOrigin, noticeTime);
    }

    public ActivityAboutEndEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

    private void remove(ActivityAboutEndEventOrigin eventOrigin) {
        List<ActivityAboutEndEventOrigin> eventOrigins = list(redissonClient, KEY);
        if (CollectionUtils.isEmpty(eventOrigins)) {
            return;
        }
        for (ActivityAboutEndEventOrigin origin : eventOrigins) {
            if (Objects.equals(origin.getActivityId(), eventOrigin.getActivityId())) {
                remove(redissonClient, KEY, origin);
            }
        }
    }

}