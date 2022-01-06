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
import com.chaoxing.activity.model.OrgConfig;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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

    /**处理来自表单创建活动中添加的推送提醒范围
     * @Description
     * @author huxiaolong
     * @Date 2022-01-05 14:28:47
     * @param activityPushReminder
     * @return
     */
    public ActivityPushReminder handleReminderScopesFromWfwForm(Integer fid, ActivityPushReminder activityPushReminder) {
        if (activityPushReminder == null) {
            return null;
        }
        List<SignUpParticipateScopeDTO> remindScopes = activityPushReminder.getReceiveScopes();
        if (CollectionUtils.isEmpty(remindScopes)) {
            return activityPushReminder;
        }
        // 查询机构下的通讯录部门列表
        List<WfwGroupDTO> wfwGroups = wfwContactApiService.listUserContactOrgsByFid(fid);
        List<Integer> scopeIds = remindScopes.stream().map(SignUpParticipateScopeDTO::getExternalId).collect(Collectors.toList());
        List<WfwGroupDTO> matchGroups = wfwGroups.stream().filter(v -> scopeIds.contains(Integer.valueOf(v.getId()))).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchGroups)) {
            activityPushReminder.setReceiveScopes(Lists.newArrayList());
            return activityPushReminder;
        }
        List<SignUpParticipateScopeDTO> realScopes = Lists.newArrayList();
        matchGroups.forEach(v -> {
            boolean isLeaf = Optional.ofNullable(v.getSoncount()).orElse(0) == 0;
            Integer pid = Optional.ofNullable(v.getGid()).map(Integer::valueOf).orElse(Integer.valueOf(v.getId()));
            realScopes.add(SignUpParticipateScopeDTO.builder()
                    .externalId(Integer.valueOf(v.getId()))
                    .externalPid(pid)
                    .externalName(v.getGroupname())
                    .leaf(isLeaf)
                    .groupType(OrgConfig.SignUpScopeType.CONTACTS.getValue())
                    .build());
        });
        activityPushReminder.setReceiveScopes(realScopes);
        activityPushReminder.setReceiveScope(JSON.toJSONString(realScopes));
        return activityPushReminder;
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
            // 查询机构下的通讯录部门列表
            List<WfwGroupDTO> wfwGroups = wfwContactApiService.listUserContactOrgsByFid(fid);
            // 非叶子节点
            List<SignUpParticipateScopeDTO> participateScopes = JSON.parseArray(activityPushReminder.getReceiveScope(), SignUpParticipateScopeDTO.class);
            List<Integer> nonLeafDeptIds = participateScopes.stream().filter(v -> Objects.equals(v.getLeaf(), false)).map(SignUpParticipateScopeDTO::getExternalId).collect(Collectors.toList());
            Set<Integer> allGroupIds = Sets.newHashSet();
            if (CollectionUtils.isNotEmpty(nonLeafDeptIds)) {
                // 获取非叶子节点的子节点部门列表
                List<WfwGroupDTO> matchWfwGroups = wfwGroups.stream().filter(v -> nonLeafDeptIds.contains(Integer.parseInt(v.getId()))).collect(Collectors.toList());
                for (WfwGroupDTO matchWfwGroup : matchWfwGroups) {
                    allGroupIds.add(Integer.valueOf(matchWfwGroup.getId()));
                    List<WfwGroupDTO> children = wfwContactApiService.listAllSubWfwGroups(matchWfwGroup, wfwGroups);
                    allGroupIds.addAll(children.stream().map(WfwGroupDTO::getId).map(Integer::valueOf).collect(Collectors.toSet()));
                }
            }
            // 获取非叶子节点
            List<Integer> leafDeptIds = participateScopes.stream().filter(SignUpParticipateScopeDTO::getLeaf).map(SignUpParticipateScopeDTO::getExternalId).collect(Collectors.toList());
            allGroupIds.addAll(leafDeptIds);
            // 获取推送人员列表
            Set<Integer> uidSet = Sets.newHashSet();
            allGroupIds.forEach(v -> {
                List<Integer> uids = wfwContactApiService.listDepartmentUid(v);
                uidSet.addAll(uids);
            });
            xxtNoticeApiService.sendNotice(activity.getName(), content, attachment, CommonConstant.NOTICE_SEND_UID, new ArrayList<>(uidSet));
        }
    }
}
