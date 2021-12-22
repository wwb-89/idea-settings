package com.chaoxing.activity.service.activity.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpParticipateScopeDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.mapper.ActivityPushReminderMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityPushReminder;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: huxiaolong
 * @date: 2021/12/21 4:48 下午
 * @version: 1.0
 */
@Service
@Slf4j
public class ActivityPushReminderService {

    @Autowired
    private ActivityPushReminderMapper activityPushReminderMapper;
    @Resource
    private WfwContactApiService wfwContactApiService;
    @Resource
    private XxtNoticeApiService xxtNoticeApiService;

    /**根据活动id查询活动的推送提醒
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-21 17:03:09
     * @param activityId
     * @return 
     */
    public ActivityPushReminder getByActivityId(Integer activityId) {
        List<ActivityPushReminder> pushReminders = activityPushReminderMapper.selectList(new LambdaQueryWrapper<ActivityPushReminder>().eq(ActivityPushReminder::getActivityId, activityId));
        return pushReminders.stream().findFirst().orElse(null);
    }

    /**新增或更新活动的推送提醒
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-21 17:02:43
     * @param waitHandleData
     * @return 
     */
    public void addOrUpdate(ActivityPushReminder waitHandleData) {
        if (waitHandleData == null || waitHandleData.getActivityId() == null) {
            return;
        }
        ActivityPushReminder existData = getByActivityId(waitHandleData.getActivityId());
        if (CollectionUtils.isNotEmpty(waitHandleData.getReceiveScopes())) {
            waitHandleData.setReceiveScope(JSON.toJSONString(waitHandleData.getReceiveScopes()));
        } else {
            existData.setReceiveScope("");
        }
        if (existData == null) {
            activityPushReminderMapper.insert(waitHandleData);
        } else {
            activityPushReminderMapper.update(null, new LambdaUpdateWrapper<ActivityPushReminder>()
                    .eq(ActivityPushReminder::getActivityId, waitHandleData.getActivityId())
                    .set(ActivityPushReminder::getReceiveScope, waitHandleData.getReceiveScope())
                    .set(ActivityPushReminder::getContent, waitHandleData.getContent()));
        }
    }

    /**
     * @Description
     * @author huxiaolong
     * @Date 2021-12-22 16:50:36
     * @param activity
     * @return
     */
    public void sendNotice(Activity activity) {
        Integer activityId = activity.getId();
        Integer fid = activity.getCreateFid();
        // 活动开启了消息提醒推送才进行通知
        if (activity.getOpenPushReminder()) {
            ActivityPushReminder activityPushReminder = getByActivityId(activityId);
            if (activityPushReminder == null || StringUtils.isBlank(activityPushReminder.getReceiveScope())) {
                String errMsg = activityPushReminder == null ? "活动不存在对应的活动推送提醒" : "活动推送提醒无对应的推送范围";
                log.error(errMsg);
                return;
            }
            String content = StringUtils.isNotBlank(activityPushReminder.getContent()) ? activityPushReminder.getContent() : "请查看";
            String attachment = NoticeDTO.generateActivityAttachment(activity.getName(), activity.getPreviewUrl());
            // 获取推送人员列表
            List<WfwGroupDTO> wfwGroups = wfwContactApiService.listUserContactOrgsByFid(fid);
            // 非叶子节点
            List<SignUpParticipateScopeDTO> participateScopes = JSON.parseArray(activityPushReminder.getReceiveScope(), SignUpParticipateScopeDTO.class);
            List<Integer> nonLeafDeptIds = participateScopes.stream().filter(v -> Objects.equals(v.getLeaf(), false)).map(SignUpParticipateScopeDTO::getExternalId).collect(Collectors.toList());
            Set<Integer> allGroupIds = Sets.newHashSet();
            if (CollectionUtils.isNotEmpty(nonLeafDeptIds)) {
                List<WfwGroupDTO> matchWfwGroups = wfwGroups.stream().filter(v -> nonLeafDeptIds.contains(Integer.parseInt(v.getId()))).collect(Collectors.toList());
                for (WfwGroupDTO matchWfwGroup : matchWfwGroups) {
                    allGroupIds.add(Integer.valueOf(matchWfwGroup.getId()));
                    List<WfwGroupDTO> children = wfwContactApiService.listAllSubWfwGroups(matchWfwGroup, wfwGroups);
                    allGroupIds.addAll(children.stream().map(WfwGroupDTO::getId).map(Integer::valueOf).collect(Collectors.toSet()));
                }
            }
            List<Integer> leafDeptIds = participateScopes.stream().filter(SignUpParticipateScopeDTO::getLeaf).map(SignUpParticipateScopeDTO::getExternalId).collect(Collectors.toList());
            allGroupIds.addAll(leafDeptIds);
            Set<Integer> uidSet = Sets.newHashSet();
            allGroupIds.forEach(v -> {
                List<Integer> uids = wfwContactApiService.listDepartmentUid(v);
                uidSet.addAll(uids);
            });
            xxtNoticeApiService.sendNotice(activity.getName(), content, attachment, CommonConstant.NOTICE_SEND_UID, new ArrayList<>(uidSet));
        }
    }
}
