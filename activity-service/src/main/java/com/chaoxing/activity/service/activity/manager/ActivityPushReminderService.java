package com.chaoxing.activity.service.activity.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.dto.manager.sign.SignDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpParticipateScopeDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwRoleDTO;
import com.chaoxing.activity.mapper.ActivityPushReminderMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.engine.TemplatePushReminderConfigService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeService;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.service.queue.notice.ActivityReminderNoticeQueue;
import com.chaoxing.activity.service.queue.notice.NoticeRecordSaveQueue;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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

    @Resource
    private ActivityPushReminderMapper activityPushReminderMapper;
    @Resource
    private ActivityReminderNoticeQueue activityReminderNoticeQueue;
    @Resource
    private WfwContactApiService wfwContactApiService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private XxtNoticeApiService xxtNoticeApiService;
    @Resource
    private NoticeRecordSaveQueue noticeRecordSaveQueue;
    @Resource
    private TemplatePushReminderConfigService templatePushReminderConfigService;
    @Resource
    private ActivityScopeService activityScopeService;
    @Resource
    private SignApiService signApiService;

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
        String receiveScope;
        if (CollectionUtils.isNotEmpty(waitHandleData.getReceiveScopes())) {
            receiveScope = JSON.toJSONString(waitHandleData.getReceiveScopes());
        } else {
            receiveScope = "";
        }
        ActivityPushReminder existData = getByActivityId(waitHandleData.getActivityId());
        if (existData == null) {
            waitHandleData.setReceiveScope(receiveScope);
            activityPushReminderMapper.insert(waitHandleData);
        } else {
            activityPushReminderMapper.update(null, new LambdaUpdateWrapper<ActivityPushReminder>()
                    .eq(ActivityPushReminder::getActivityId, waitHandleData.getActivityId())
                    .set(ActivityPushReminder::getReceiveScope, receiveScope)
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


    /**推送提醒通知
     * @Description
     * @author huxiaolong
     * @Date 2021-12-22 16:50:36
     * @param activity
     * @return
     */
    public void sendNotice(Activity activity) {
        Boolean openPushReminder = activity.getOpenPushReminder();
        if (!openPushReminder) {
            return;
        }
        activityReminderNoticeQueue.push(ActivityReminderNoticeQueue.QueueParamDTO.builder().activityId(activity.getId()).build());
    }

    /**推送提醒通知
     * @Description
     * @author huxiaolong
     * @Date 2021-12-22 16:50:36
     * @param activityId
     * @return
     */
    public void sendNotice(Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        Boolean openPushReminder = Optional.ofNullable(activity.getOpenPushReminder()).orElse(false);
        if (!openPushReminder) {
            return;
        }
        ActivityPushReminder activityPushReminder = getByActivityId(activityId);
        if (activityPushReminder == null) {
            log.error("活动不存在对应的活动推送提醒");
            return;
        }
        TemplatePushReminderConfig config = templatePushReminderConfigService.getByTemplateId(activity.getTemplateId());
        Boolean remindWithinRoleScope = Optional.ofNullable(config).map(TemplatePushReminderConfig::getRemindWithinRoleScope).orElse(false);
        if (remindWithinRoleScope) {
            sendNoticeToScopeRecipients(activity, activityPushReminder);
        } else {
            sendNoticeToManualAddRecipients(activity, activityPushReminder);
        }

    }

    /**
     * @Description
     * @author huxiaolong
     * @Date 2022-02-28 11:20:35
     * @param activity
     * @param activityPushReminder
     * @return
     */
    private void sendNoticeToScopeRecipients(Activity activity, ActivityPushReminder activityPushReminder) {
        Integer activityId = activity.getId();
        // 获取活动的发布范围
        List<ActivityScope> activityScopes = activityScopeService.listByActivity(activityId);
        if (CollectionUtils.isEmpty(activityScopes)) {
            return;
        }
        // 获取发布范围的所有角色id
        SignCreateParamDTO sign = signApiService.getCreateById(activity.getSignId());
        List<SignUpCreateParamDTO> signUps = Optional.ofNullable(sign).map(SignCreateParamDTO::getSignUps).orElse(Lists.newArrayList());
        Set<WfwRoleDTO> wfwRoles = Sets.newHashSet();
        signUps.forEach(v -> {
            Boolean openRoleLimit = v.getOpenRoleLimit();
            if (openRoleLimit && CollectionUtils.isNotEmpty(v.getRoleParticipateScopes())) {
                wfwRoles.addAll(v.getRoleParticipateScopes());
            }
        });
        if (CollectionUtils.isEmpty(wfwRoles)) {
            return;
        }
        Map<Integer, String> scopeMap = activityScopes.stream().collect(Collectors.toMap(ActivityScope::getFid, ActivityScope::getName, (v1, v2) -> v2));
        Map<Integer, String> roleMap = wfwRoles.stream().collect(Collectors.toMap(WfwRoleDTO::getRole, WfwRoleDTO::getName, (v1, v2) -> v2));
        Map<String, String> releaseScopeRoleMap = Maps.newHashMap();
        //   releaseScopeRoleMap 主要防止fid-roleId重复
        for (Map.Entry<Integer, String> scopeEntry : scopeMap.entrySet()) {
            for (Map.Entry<Integer, String> roleEntry : roleMap.entrySet()) {
                String id = scopeEntry.getKey() + "-" + roleEntry.getKey();
                String name = scopeEntry.getValue();
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(roleEntry.getValue())) {
                    name += "-";
                }
                name += roleEntry;
                releaseScopeRoleMap.put(id, name);
            }
        }
        // 封装通知范围对象
        List<NoticeDTO.Togen> noticeTogens = Lists.newArrayList();
        for (Map.Entry<String, String> entry : releaseScopeRoleMap.entrySet()) {
            noticeTogens.add(NoticeDTO.Togen.builder()
                    .id(entry.getKey())
                    .name(entry.getValue())
                    .type(14)
                    .ext("")
                    .build());
        }

        String content = activityPushReminder.getContent();
        String attachment = NoticeDTO.generateActivityAttachment(activity.getName(), activity.getPreviewUrl());

        xxtNoticeApiService.sendNoticeToTogens(activity.getName(), content, attachment, CommonConstant.NOTICE_SEND_UID, noticeTogens);
        // 将通知保存
        NoticeRecordSaveQueue.QueueParamDTO queueParam = NoticeRecordSaveQueue.QueueParamDTO.builder()
                .activityId(activityId)
                .type(NoticeRecord.TypeEnum.ACTIVITY_RELEASE)
                .title(activity.getName())
                .content(content)
                .timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
                .build();
        noticeRecordSaveQueue.push(queueParam);

    }

    /**
     * @Description
     * @author huxiaolong
     * @Date 2022-02-28 11:20:51
     * @param activity
     * @param activityPushReminder
     * @return
     */
    private void sendNoticeToManualAddRecipients(Activity activity, ActivityPushReminder activityPushReminder) {
        Integer activityId = activity.getId();
        Integer fid = activity.getCreateFid();
        if (StringUtils.isBlank(activityPushReminder.getReceiveScope())) {
            log.error("活动推送提醒无对应的推送范围");
            return;
        }
        String content = activityPushReminder.getContent();
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
        // 将通知保存
        NoticeRecordSaveQueue.QueueParamDTO queueParam = NoticeRecordSaveQueue.QueueParamDTO.builder()
                .activityId(activityId)
                .type(NoticeRecord.TypeEnum.ACTIVITY_RELEASE)
                .title(activity.getName())
                .content(content)
                .timestamp(DateUtils.date2Timestamp(LocalDateTime.now()))
                .build();
        noticeRecordSaveQueue.push(queueParam);
    }
}
