package com.chaoxing.activity.service.queue.event.activity;

import com.chaoxing.activity.dto.event.activity.ActivityAboutStartEventOrigin;
import com.chaoxing.activity.dto.notice.MarketNoticeTemplateDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.SystemNoticeTemplate;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.notice.MarketNoticeTemplateService;
import com.chaoxing.activity.service.queue.IDelayedQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

/**活动即将开始事件队列
 * @author wwb
 * @version ver 1.0
 * @className ActivityAboutStartEventQueue
 * @description
 * @blame wwb
 * @date 2021-10-26 11:35:17
 */
@Slf4j
@Service
public class ActivityAboutStartEventQueue implements IDelayedQueue<ActivityAboutStartEventOrigin> {

    private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "event" + CacheConstant.CACHE_KEY_SEPARATOR + "activity_about_start";
    private static final Integer ABOUT_START_HOURS = 24;

    @Resource
    private MarketNoticeTemplateService marketNoticeTemplateService;
    @Resource
    private ActivityQueryService activityQueryService;

    @Resource
    private RedissonClient redissonClient;

    public void push(ActivityAboutStartEventOrigin eventOrigin) {
        LocalDateTime delayTime = getDelayTime(eventOrigin);
        if (delayTime.isBefore(LocalDateTime.now())) {
            // 通知时间已经过了，忽略
            return;
        }
        push(redissonClient, KEY, eventOrigin, delayTime);
    }

    private LocalDateTime getDelayTime(ActivityAboutStartEventOrigin eventOrigin) {
        // 根据活动id，查询活动市场id及对应的市场通知模板，若市场通知模板不存在，则采用对应的系统通知模板
        Activity activity = activityQueryService.getById(eventOrigin.getActivityId());
        MarketNoticeTemplateDTO noticeTemplate = marketNoticeTemplateService.getMarketOrSystemNoticeTemplate(activity.getMarketId(), SystemNoticeTemplate.NoticeTypeEnum.ACTIVITY_ABOUT_START.getValue());
        LocalDateTime startTime = eventOrigin.getStartTime();
        Integer delayHour = Optional.ofNullable(noticeTemplate).map(MarketNoticeTemplateDTO::getDelayHour).orElse(ABOUT_START_HOURS);
        Integer delayMinute = Optional.ofNullable(noticeTemplate).map(MarketNoticeTemplateDTO::getDelayMinute).orElse(0);
        LocalDateTime noticeTime = startTime.minusHours(delayHour).minusMinutes(delayMinute);
        return noticeTime;
    }

    public void rePush(ActivityAboutStartEventOrigin eventOrigin) {
        LocalDateTime delayTime = getDelayTime(eventOrigin);
        rePush(redissonClient, KEY, eventOrigin, delayTime);
    }

    public ActivityAboutStartEventOrigin pop() throws InterruptedException {
        return pop(redissonClient, KEY);
    }

}