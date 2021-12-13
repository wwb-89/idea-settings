package com.chaoxing.activity.service.queue.notice.handler;

import com.chaoxing.activity.dto.notice.MarketNoticeTemplateDTO;
import com.chaoxing.activity.dto.notice.NoticeTemplateFieldDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Blacklist;
import com.chaoxing.activity.model.SystemNoticeTemplate;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.blacklist.BlacklistQueryService;
import com.chaoxing.activity.service.notice.MarketNoticeTemplateService;
import com.chaoxing.activity.service.notice.SystemNoticeTemplateService;
import com.chaoxing.activity.service.queue.notice.BlacklistUserNoticeQueue;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: huxiaolong
 * @date: 2021/12/7 12:03 下午
 * @version: 1.0
 */
@Service
public class BlacklistUserNoticeHandleService {

    @Resource
    private SystemNoticeTemplateService systemNoticeTemplateService;
    @Resource
    private MarketNoticeTemplateService marketNoticeTemplateService;
    @Resource
    private MarketQueryService marketQueryService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private BlacklistQueryService blacklistQueryService;
    @Resource
    private BlacklistUserNoticeQueue blacklistUserNoticeQueue;

    private NoticeTemplateFieldDTO buildNoticeField(Integer marketId) {
        return buildNoticeField(marketId, null);
    }

    private NoticeTemplateFieldDTO buildNoticeField(Integer marketId, Integer activityId) {
        NoticeTemplateFieldDTO noticeTemplateField = NoticeTemplateFieldDTO.builder().build();
        if (activityId != null) {
            Activity activity = activityQueryService.getById(activityId);
            if (activity != null) {
                noticeTemplateField = systemNoticeTemplateService.buildNoticeField(activity);
            }
        }
        if (marketId == null) {
            return noticeTemplateField;
        }
        if (StringUtils.isBlank(noticeTemplateField.getActivityOrganisers())) {
            noticeTemplateField.setActivityOrganisers(marketQueryService.getMarketBelongOrgName(marketId));
        }
        return noticeTemplateField;
    }

    /**处理黑名单手动添加通知，将通知放入黑名单通知队列
     * @Description
     * @author huxiaolong
     * @Date 2021-12-07 15:12:45
     * @param uids
     * @param marketId
     * @return
     */
    public void handleManualAddBlacklistNotice(List<Integer> uids, Integer marketId) {
        if (CollectionUtils.isEmpty(uids) || marketId == null) {
            return;
        }
        LocalDateTime addBlacklistTime = LocalDateTime.now();
        NoticeTemplateFieldDTO noticeTemplateField = buildNoticeField(marketId);
        noticeTemplateField.setBlacklistAddTime(addBlacklistTime.format(CommonConstant.NOTICE_ACTIVITY_TIME_FORMATTER));
        String organisers = noticeTemplateField.getActivityOrganisers();
        // 封装移出黑名单通知
        MarketNoticeTemplateDTO noticeTemplate = marketNoticeTemplateService.getMarketOrSystemNoticeTemplate(marketId, SystemNoticeTemplate.NoticeTypeEnum.MANUAL_ADD_TO_BLACKLIST.getValue());
        String defaultTitle = generateManualAddBlacklistTitle(organisers);
        String defaultContent = generateManualAddBlacklistTitle(organisers);
        pushNoticeInQueue(defaultTitle, defaultContent, noticeTemplateField, noticeTemplate, uids);
    }

    /**处理黑名单自动添加通知，将通知放入黑名单通知队列
     * @Description
     * @author huxiaolong
     * @Date 2021-12-07 15:04:20
     * @param activityId
     * @return
     */
    public void handleAutoAddBlackListNotice(Integer activityId) {
        LocalDateTime addBlacklistTime = LocalDateTime.now();
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer marketId = activity.getMarketId();
        if (marketId == null) {
            return;
        }
        List<Integer> uids = blacklistQueryService.listNeedNoticeAutoAddBlacklistUids(marketId, activityId);
        if (CollectionUtils.isEmpty(uids)) {
            return;
        }
        // 封装通知模板字段值对象
        NoticeTemplateFieldDTO noticeTemplateField = buildNoticeField(marketId, activityId);
        noticeTemplateField.setBlacklistAddTime(addBlacklistTime.format(CommonConstant.NOTICE_ACTIVITY_TIME_FORMATTER));
        String organisers = noticeTemplateField.getActivityOrganisers();
        String activityName = noticeTemplateField.getActivityName();
        String activityTime = noticeTemplateField.getActivityTime();
        Boolean enableAutoRemove = Optional.ofNullable(noticeTemplateField.getEnableAutoRemove()).orElse(false);
        Integer autoRemoveHours = Optional.ofNullable(noticeTemplateField.getAutoRemoveHours()).orElse(null);
        // 封装移出黑名单通知
        MarketNoticeTemplateDTO noticeTemplate = marketNoticeTemplateService.getMarketOrSystemNoticeTemplate(marketId, SystemNoticeTemplate.NoticeTypeEnum.AUTO_ADD_TO_BLACKLIST.getValue());
        String defaultTitle = generateAutoAddBlacklistTitle(organisers);
        String defaultContent = generateAutoAddBlacklistContent(activityName, activityTime, enableAutoRemove, autoRemoveHours);
        pushNoticeInQueue(defaultTitle, defaultContent, noticeTemplateField, noticeTemplate, uids);
    }

    /**处理黑名单自动移除通知，将通知放入黑名单通知队列
     * @Description
     * @author huxiaolong
     * @Date 2021-12-07 11:35:56
     * @param marketId
     * @param blacklists
     * @return
     */
    public void handleBlacklistRemoveNotice(Integer marketId, List<Blacklist> blacklists) {
        if (marketId == null || CollectionUtils.isEmpty(blacklists)) {
            return;
        }
        LocalDateTime removeTime = LocalDateTime.now();
        Map<Integer, LocalDateTime> userAddBlacklistTimeMap = blacklists.stream().collect(Collectors.toMap(Blacklist::getUid, Blacklist::getCreateTime, (v1, v2) -> v2));

        NoticeTemplateFieldDTO noticeTemplateField = buildNoticeField(marketId);
        String organisers = noticeTemplateField.getActivityOrganisers();
        // 封装移出黑名单通知
        MarketNoticeTemplateDTO noticeTemplate = marketNoticeTemplateService.getMarketOrSystemNoticeTemplate(marketId, SystemNoticeTemplate.NoticeTypeEnum.REMOVE_FROM_BLACKLIST.getValue());
        for (Map.Entry<Integer, LocalDateTime> entry : userAddBlacklistTimeMap.entrySet()) {
            Integer uid = entry.getKey();
            LocalDateTime addTime = entry.getValue();
            String defaultTitle = generateRemoveBlacklistTitle(organisers);
            String defaultContent = generateRemoveBlacklistContent(organisers, addTime, removeTime);
            pushNoticeInQueue(defaultTitle, defaultContent, noticeTemplateField, noticeTemplate, Lists.newArrayList(uid));
        }

    }

    private void pushNoticeInQueue(String defaultTitle, String defaultContent,
                                   NoticeTemplateFieldDTO noticeTemplateField, MarketNoticeTemplateDTO noticeTemplate,
                                   List<Integer> uids) {
        String waitConvertTitle;
        String waitConvertContent;
        if (noticeTemplate == null) {
            waitConvertTitle = defaultTitle;
            waitConvertContent = defaultContent;
        } else {
            waitConvertTitle = Optional.ofNullable(noticeTemplate.getCodeTitle()).orElse("");
            waitConvertContent = Optional.ofNullable(noticeTemplate.getCodeContent()).orElse("");
        }
        String title = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertTitle, noticeTemplateField);
        String content = SystemNoticeTemplate.NoticeFieldEnum.convertNoticeField(waitConvertContent, noticeTemplateField);
        BlacklistUserNoticeQueue.QueueParamDTO queueParam = BlacklistUserNoticeQueue.QueueParamDTO.builder()
                .uids(uids)
                .title(title)
                .content(content)
                .build();
        blacklistUserNoticeQueue.push(queueParam);
    }

    /**手动添加黑名单通知默认title生成
     * @Description
     * @author huxiaolong
     * @Date 2021-12-07 12:15:26
     * @param organisers
     * @return
     */
    public String generateManualAddBlacklistTitle(String organisers) {
        return "您已被" + organisers + "移入黑名单！";
    }

    /**手动添加黑名单通知默认content生成
     * @Description
     * @author huxiaolong
     * @Date 2021-12-07 12:15:19
     * @param organisers
     * @return
     */
    public String generateManualAddBlacklistContent(String organisers) {
        return "您好，您已被" + organisers + "拉入黑名单，若有疑问请联系单位管理员。 点击此处联系管理员>>";
    }

    /**自动添加黑名单通知默认title生成
     * @Description
     * @author huxiaolong
     * @Date 2021-12-07 12:14:19
     * @param organisers
     * @return
     */
    private String generateAutoAddBlacklistTitle(String organisers) {
        return "您已被移入" + organisers + "黑名单！";
    }

    /**自动添加黑名单通知默认content生成
     * @Description
     * @author huxiaolong
     * @Date 2021-12-07 12:13:15
     * @param activityName
     * @param activityTime
     * @return
     */
    private String generateAutoAddBlacklistContent(String activityName, String activityTime, Boolean enableAutoRemove, Integer autoRemoveHours) {
        enableAutoRemove = Optional.ofNullable(enableAutoRemove).orElse(false);
        String content = "您好，由于您未签到（签退）次数达到上限，已自动进入黑名单，";
        if (enableAutoRemove && autoRemoveHours != null) {
            content += "将在" + autoRemoveHours + "小时后自动解除，";
        } else {
            content += "需管理员手动解除黑名单，";
        }
        content += "在此期间内将无法参加任何该单位活动，" +
                "若有疑问请联系单位管理员。 点击此处联系管理员>>" + CommonConstant.NEW_LINE_CHAR +
                "未签到/签退活动名称：" + activityName + CommonConstant.NEW_LINE_CHAR +
                "活动时间：" + activityTime;
        return content;
    }

    /**自动移除通知默认title生成
     * @Description
     * @author huxiaolong
     * @Date 2021-12-07 12:12:24
     * @param organisers
     * @return
     */
    private String generateRemoveBlacklistTitle(String organisers) {
        return "您已被移出" + organisers + "黑名单！";
    }

    /**自动移除通知默认content生成
     * @Description
     * @author huxiaolong
     * @Date 2021-12-07 12:12:35
     * @param organisers
     * @param addTime
     * @param removeTime
     * @return
     */
    private String generateRemoveBlacklistContent(String organisers, LocalDateTime addTime, LocalDateTime removeTime) {
        String addTimeStr = Optional.ofNullable(addTime).map(v -> v.format(CommonConstant.NOTICE_ACTIVITY_TIME_FORMATTER)).orElse("");
        String removeTimeStr = Optional.ofNullable(removeTime).map(v -> v.format(CommonConstant.NOTICE_ACTIVITY_TIME_FORMATTER)).orElse("");

        return "您好，您已被" + organisers + "移出黑名单，可参加该单位的活动。" + CommonConstant.NEW_LINE_CHAR + CommonConstant.NEW_LINE_CHAR +
                "移入黑名单时间：" + addTimeStr + CommonConstant.NEW_LINE_CHAR +
                "移出黑名单时间：" + removeTimeStr ;
    }
}
