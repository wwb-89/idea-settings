package com.chaoxing.activity.service.activity.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.manager.ActivityCreatePermissionDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpParticipateScopeDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwDepartmentDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.mapper.ActivityCreatePermissionMapper;
import com.chaoxing.activity.model.ActivityCreatePermission;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.manager.MoocApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.service.manager.wfw.WfwGroupApiService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/2 10:01 上午
 * <p>
 */
@Slf4j
@Service
public class ActivityCreatePermissionService {

    @Resource
    private WfwGroupApiService wfwGroupApiService;
    @Resource
    private WfwContactApiService wfwContactApiService;
    @Resource
    private ClassifyQueryService classifyQueryService;
    @Resource
    private MoocApiService moocApiService;
    @Resource
    private ActivityCreatePermissionMapper activityCreatePermissionMapper;

    /**查询机构fid下roleId的角色权限
    * @Description
    * @author huxiaolong
    * @Date 2021-06-02 16:19:10
    * @param fid
    * @param roleId
    * @return com.chaoxing.activity.model.ActivityCreatePermission
    */
    public ActivityCreatePermission getPermissionByFidRoleId(Integer fid, Integer marketId, Integer roleId) {
        List<ActivityCreatePermission> activityCreatePermissions = activityCreatePermissionMapper.selectList(new QueryWrapper<ActivityCreatePermission>()
                .lambda()
                .eq(marketId == null, ActivityCreatePermission::getFid, fid)
                .eq(marketId != null, ActivityCreatePermission::getMarketId, marketId)
                .eq(ActivityCreatePermission::getRoleId, roleId));
        if (CollectionUtils.isNotEmpty(activityCreatePermissions)) {
            return activityCreatePermissions.get(0);
        }
        return null;
    }

    /**新增角色活动创建权限
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-02 16:18:29
    * @param activityCreatePermission
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void add(ActivityCreatePermission activityCreatePermission) {
            activityCreatePermissionMapper.insert(activityCreatePermission);
    }

    /**修改角色活动创建权限
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-02 16:18:09
    * @param activityCreatePermission
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void edit(ActivityCreatePermission activityCreatePermission) {
        activityCreatePermissionMapper.updateById(activityCreatePermission);
    }

    /**批量配置角色权限
    * @Description
    * @author huxiaolong
    * @Date 2021-06-02 16:17:46
    * @param uid
    * @param roleIds
    * @param permissionConfig
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void batchConfigPermission(Integer uid, List<Integer> roleIds, ActivityCreatePermission permissionConfig) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }
        Integer fid = permissionConfig.getFid();
        Integer marketId = permissionConfig.getMarketId();
        List<ActivityCreatePermission> existPermissions = activityCreatePermissionMapper.selectList(new QueryWrapper<ActivityCreatePermission>()
                .lambda()
                .eq(marketId == null, ActivityCreatePermission::getFid, fid)
                .eq(marketId != null, ActivityCreatePermission::getMarketId, marketId)
                .in(ActivityCreatePermission::getRoleId, roleIds)
                .eq(ActivityCreatePermission::getDeleted, Boolean.FALSE));

        // 更新数据
        List<Integer> existIds = Lists.newArrayList();
        List<Integer> existRoleIds = Lists.newArrayList();
        existPermissions.forEach(v -> {
            existIds.add(v.getId());
            existRoleIds.add(v.getRoleId());
        });

        // 批量更新数据
        if (!existIds.isEmpty()) {
            activityCreatePermissionMapper.update(null, new UpdateWrapper<ActivityCreatePermission>()
                    .lambda()
                    .in(ActivityCreatePermission::getId, existIds)
                    .set(ActivityCreatePermission::getAllActivityClassify, permissionConfig.getAllActivityClassify())
                    .set(ActivityCreatePermission::getActivityClassifyScope, permissionConfig.getActivityClassifyScope())
                    .set(ActivityCreatePermission::getWfwSignUpScope, permissionConfig.getWfwSignUpScope())
                    .set(ActivityCreatePermission::getWfwSignUpScopeType, permissionConfig.getWfwSignUpScopeType())
                    .set(ActivityCreatePermission::getContactsSignUpScope, permissionConfig.getContactsSignUpScope())
                    .set(ActivityCreatePermission::getContactsSignUpScopeType, permissionConfig.getContactsSignUpScopeType())
                    .set(ActivityCreatePermission::getUpdateUid, uid)
                    .set(ActivityCreatePermission::getUpdateTime, LocalDateTime.now()));
        }

        roleIds.removeAll(existRoleIds);
        if (!roleIds.isEmpty()) {
            List<ActivityCreatePermission> waitSavePermissions = roleIds.stream().map(v -> {
                ActivityCreatePermission acp = ActivityCreatePermission.buildActivityCreatePermission(permissionConfig);
                acp.setRoleId(v);
                acp.setCreateUid(uid);
                acp.setUpdateUid(uid);
                return acp;
            }).collect(Collectors.toList());
            activityCreatePermissionMapper.batchAdd(waitSavePermissions);
        }
    }

    /**根据用户权限，设置组织架构列表、活动分类到model
    * @Description
    * @author huxiaolong
    * @Date 2021-06-03 15:40:25
    * @param fid
    * @param uid
    * @return void
    */
    public ActivityCreatePermissionDTO getActivityCreatePermission(Integer fid, Integer marketId, Integer uid) {
        ActivityCreatePermissionDTO activityCreatePermission = ActivityCreatePermissionDTO.buildDefault();
        // 查询用户的角色id
        List<Integer> userRoleIds = moocApiService.getUserRoleIds(fid, uid);
        // 无角色返回空
        if (CollectionUtils.isEmpty(userRoleIds)) {
            return activityCreatePermission;
        }
        List<ActivityCreatePermission> createPermissions = activityCreatePermissionMapper.selectList(new QueryWrapper<ActivityCreatePermission>()
                .lambda()
                .eq(marketId == null, ActivityCreatePermission::getFid, fid)
                .eq(marketId != null, ActivityCreatePermission::getMarketId, marketId)
                .in(ActivityCreatePermission::getRoleId, userRoleIds)
                .eq(ActivityCreatePermission::getDeleted, Boolean.FALSE));

        List<WfwGroupDTO> wfwGroups = wfwGroupApiService.listGroupByFid(fid);
        List<WfwGroupDTO> contactsGroups = wfwContactApiService.listUserContactOrgsByFid(fid);

        List<Classify> classifies;
        if (marketId != null) {
            classifies = classifyQueryService.listMarketClassifies(marketId);
        } else {
            classifies = classifyQueryService.listOrgClassifies(fid);
        }
        // 若查出的角色配置权限数量少于userRoleIds的数量 证明有未配置权限的角色，则以该角色最大权限返回数据
        if (createPermissions.size() < userRoleIds.size()) {
            activityCreatePermission.setClassifies(classifies);
            activityCreatePermission.setWfwGroups(WfwGroupDTO.perfectWfwGroups(wfwGroups));
            activityCreatePermission.setContactsGroups(WfwGroupDTO.perfectWfwGroups(contactsGroups));
            return activityCreatePermission;
        }

        activityCreatePermission.setWfwGroups(buildWfwReleaseScopes(fid, uid, createPermissions, wfwGroups));
        activityCreatePermission.setContactsGroups(buildContactsReleaseScopes(fid, uid, createPermissions, wfwGroups));
        activityCreatePermission.setClassifies(buildClassifies(classifies, createPermissions));
        return activityCreatePermission;
    }

    private List<WfwGroupDTO> buildWfwReleaseScopes(Integer fid, Integer uid, List<ActivityCreatePermission> permissions, List<WfwGroupDTO> wfwGroups) {
        boolean hasSetManageGroupScope = Boolean.FALSE;
        List<Integer> manageGroupIds = Lists.newArrayList();
        Set<SignUpParticipateScopeDTO> wfwReleaseScopes = Sets.newHashSet();
        for (ActivityCreatePermission permission : permissions) {
            if (Objects.equals(permission.getWfwSignUpScopeType(), ActivityCreatePermission.SignUpScopeType.NO_LIMIT.getValue())) {
                return WfwGroupDTO.perfectWfwGroups(wfwGroups);
            }
            if (!hasSetManageGroupScope && Objects.equals(permission.getWfwSignUpScopeType(), ActivityCreatePermission.SignUpScopeType.COMPETENT_RANGE.getValue())) {
                manageGroupIds = wfwGroupApiService.listUserManageGroupIdByFid(fid, uid);
                hasSetManageGroupScope = Boolean.TRUE;
            }
            // 获取用户角色发布范围并集
            if (StringUtils.isNotBlank(permission.getWfwSignUpScope())) {
                wfwReleaseScopes.addAll(JSON.parseArray(permission.getWfwSignUpScope(), SignUpParticipateScopeDTO.class));
            }
        }
        // 如果其中角色有配置了作为管理的发布范围，则需要补充管理的组织架构
        if (hasSetManageGroupScope) {
            List<SignUpParticipateScopeDTO> additionalReleaseScopes = packageParticipateScopes(manageGroupIds, wfwGroups);
            wfwReleaseScopes.addAll(additionalReleaseScopes);
        }
        return buildWfwGroups(wfwReleaseScopes, wfwGroups);
    }

    private List<WfwGroupDTO> buildContactsReleaseScopes(Integer fid, Integer uid, List<ActivityCreatePermission> permissions, List<WfwGroupDTO> contactsGroups) {
        boolean hasSetManageGroupScope = Boolean.FALSE;
        List<Integer> manageGroupIds = Lists.newArrayList();
        Set<SignUpParticipateScopeDTO> contactsReleaseScopes = Sets.newHashSet();
        for (ActivityCreatePermission permission : permissions) {
            if (Objects.equals(permission.getContactsSignUpScopeType(), ActivityCreatePermission.SignUpScopeType.NO_LIMIT.getValue())) {
                return WfwGroupDTO.perfectWfwGroups(contactsGroups);
            }
            if (!hasSetManageGroupScope && Objects.equals(permission.getContactsSignUpScopeType(), ActivityCreatePermission.SignUpScopeType.COMPETENT_RANGE.getValue())) {
                List<WfwDepartmentDTO> departments = wfwContactApiService.listManagerDepartment(fid, uid);
                manageGroupIds = departments.stream().map(WfwDepartmentDTO::getId).collect(Collectors.toList());
                hasSetManageGroupScope = Boolean.TRUE;
            }
            // 获取用户角色发布范围并集
            if (StringUtils.isNotBlank(permission.getContactsSignUpScope())) {
                contactsReleaseScopes.addAll(JSON.parseArray(permission.getContactsSignUpScope(), SignUpParticipateScopeDTO.class));
            }
        }
        // 如果其中角色有配置了作为管理的发布范围，则需要补充管理的组织架构
        if (hasSetManageGroupScope) {
            List<SignUpParticipateScopeDTO> additionalReleaseScopes = packageParticipateScopes(manageGroupIds, contactsGroups);
            contactsReleaseScopes.addAll(additionalReleaseScopes);
        }
        return buildWfwGroups(contactsReleaseScopes, contactsGroups);
    }

    /**根据权限中的活动类型范围并集，获取过滤权限下的活动类型列表
    * @Description
    * @author huxiaolong
    * @Date 2021-08-05 18:39:50
    * @param classifies
    * @param permissions
    * @return java.util.List<com.chaoxing.activity.model.Classify>
    */
    private List<Classify> buildClassifies(List<Classify> classifies, List<ActivityCreatePermission> permissions) {
        Set<Integer> classifyIdSet = Sets.newHashSet();
        for (ActivityCreatePermission permission : permissions) {
            if (permission.getAllActivityClassify()) {
                return classifies;
            }
            // 获取用户角色活动类型范围并集
            if (StringUtils.isNotBlank(permission.getActivityClassifyScope())) {
                List<String> splitIds = Arrays.asList(permission.getActivityClassifyScope().split(","));
                List<Integer> ids = Lists.newArrayList();
                CollectionUtils.collect(splitIds, Integer::valueOf, ids);
                classifyIdSet.addAll(ids);
            }
        }
        return classifies.stream().filter(v -> classifyIdSet.contains(v.getId())).collect(Collectors.toList());
    }

    /**根据管理的机构id，构建发布范围实体
    * @Description
    * @author huxiaolong
    * @Date 2021-06-04 18:12:52
    * @param manageGroupIds
    * @param wfwGroups
    * @return java.util.List<com.chaoxing.activity.dto.sign.SignUpParticipateScopeDTO>
    */
    private List<SignUpParticipateScopeDTO> packageParticipateScopes(List<Integer> manageGroupIds, List<WfwGroupDTO> wfwGroups) {
        List<SignUpParticipateScopeDTO> scopes = Lists.newArrayList();
        for (WfwGroupDTO group : wfwGroups) {
            Integer groupId = Integer.valueOf(group.getId());
            Integer gid = StringUtils.isBlank(group.getGid()) ? null : Integer.valueOf(group.getGid());
            Boolean isLeaf = group.getSoncount() == null || group.getSoncount() != 0;
            if (manageGroupIds.contains(groupId)) {
                scopes.add(SignUpParticipateScopeDTO.builder()
                        .externalId(groupId)
                        .externalPid(gid)
                        .externalName(group.getGroupname())
                        .leaf(isLeaf)
                        .build());
            }
        }
        return scopes;
    }


    /**根据权限中的发布范围并集，获取过滤权限下的组织架构列表
    * @Description
    * @author huxiaolong
    * @Date 2021-06-03 14:29:03
    * @param releaseScopes
    * @param wfwGroups
    * @return java.util.List<com.chaoxing.activity.dto.manager.WfwGroupDTO>
    */
    private List<WfwGroupDTO> buildWfwGroups(Set<SignUpParticipateScopeDTO> releaseScopes, List<WfwGroupDTO> wfwGroups) {
        // wfwGroups 转换为map
        Map<Integer, WfwGroupDTO> wfwGroupMap = Maps.newHashMap();
        for (WfwGroupDTO group : wfwGroups) {
            group.setVirtualId(group.getId());
            wfwGroupMap.put(Integer.valueOf(group.getId()), group);
        }
        // 对角色发布范围并集做处理，过滤筛选
        // scopeGroupIdSet: 不重复的发布范围groupId集, leafGroupIdSet: 发布范围叶子节点groupId集， parentGroupIdSet:发布范围groupId交集
        Set<Integer> scopeGroupIdSet = Sets.newHashSet();
        Map<Integer, Boolean> scopeGroupMap = Maps.newHashMap();

        for (SignUpParticipateScopeDTO scope : releaseScopes) {
            Integer externalId = scope.getExternalId();
            scopeGroupIdSet.add(externalId);
            scopeGroupMap.put(externalId, scope.getLeaf());
        }
        Set<WfwGroupDTO> result = Sets.newHashSet();
        Set<Integer> resultIdSet = Sets.newHashSet();

        for (Integer groupId : scopeGroupIdSet) {
            WfwGroupDTO group = wfwGroupMap.get(groupId);
            if (group == null) {
                continue;
            }
            result.add(group);
            resultIdSet.add(groupId);
            // 递归查找父级节点
            recursionSearchParentGroup(group, wfwGroups, result, resultIdSet);
            // 递归查找子节点
            recursionSearchChildGroup(group, wfwGroups, result, resultIdSet);
        }
        List<WfwGroupDTO> additionalGroups = Lists.newArrayList();
        for (WfwGroupDTO group : result) {
            Integer groupId = Integer.valueOf(group.getId());
            Boolean leaf = scopeGroupMap.get(groupId);
            // 非叶子结点
            if (leaf != null) {
                if (!leaf || group.getSoncount() > 0) {
                    // copy 本身作为叶子节点
                    WfwGroupDTO item = new WfwGroupDTO();
                    BeanUtils.copyProperties(group, item);
                    item.setVirtualId(UUID.randomUUID().toString().trim().replaceAll("-", ""));
                    item.setSoncount(0);
                    item.setGid(item.getId());
                    additionalGroups.add(item);
                }
            }
        }
        result.addAll(additionalGroups);
        // 对result排序存放
        List<WfwGroupDTO> resultWfwGroups = Lists.newArrayList();
        if (result.size() > 0) {
            for (WfwGroupDTO wfwGroup : wfwGroups) {
                String groupId = wfwGroup.getId();
                for (WfwGroupDTO wfwGroupDTO : result) {
                    if (Objects.equals(groupId, wfwGroupDTO.getId())) {
                        resultWfwGroups.add(wfwGroupDTO);
                    }
                }
            }
        }

        return resultWfwGroups;
    }

    /**递归查找父级结果
    * @Description
    * @author huxiaolong
    * @Date 2021-06-07 11:53:09
    * @param group
    * @param wfwGroups
    * @param result
    * @param resultIdSet
    * @return void
    */
    private void recursionSearchParentGroup(WfwGroupDTO group, List<WfwGroupDTO> wfwGroups, Set<WfwGroupDTO> result, Set<Integer> resultIdSet) {
        if (StringUtils.isBlank(group.getGid()) || resultIdSet.contains(Integer.valueOf(group.getGid()))) {
            return;
        }
        WfwGroupDTO pGroup = wfwGroups.stream().filter(x -> Objects.equals(x.getId(), group.getGid())).findFirst().orElse(null);
        if (pGroup == null) {
            return;
        }
        result.add(pGroup);
        resultIdSet.add(Integer.valueOf(pGroup.getId()));
        recursionSearchParentGroup(pGroup, wfwGroups, result, resultIdSet);
    }

    /**递归查找子节点
    * @Description
    * @author huxiaolong
    * @Date 2021-06-07 11:54:13
    * @param group
    * @param wfwGroups
    * @param result
    * @param resultIdSet
    * @return void
    */
    private void recursionSearchChildGroup(WfwGroupDTO group, List<WfwGroupDTO> wfwGroups, Set<WfwGroupDTO> result, Set<Integer> resultIdSet) {
        WfwGroupDTO pGroup = wfwGroups.stream().filter(x -> Objects.equals(x.getGid(), group.getId())).findFirst().orElse(null);
        if (pGroup == null) {
            return;
        }
        result.add(pGroup);
        resultIdSet.add(Integer.valueOf(pGroup.getId()));
        recursionSearchChildGroup(pGroup, wfwGroups, result, resultIdSet);
    }
}
