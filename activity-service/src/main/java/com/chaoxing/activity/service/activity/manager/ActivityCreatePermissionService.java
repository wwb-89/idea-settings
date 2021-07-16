package com.chaoxing.activity.service.activity.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.manager.ActivityCreatePermissionDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwDepartmentDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpParticipateScopeDTO;
import com.chaoxing.activity.mapper.ActivityCreatePermissionMapper;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.model.ActivityCreatePermission;
import com.chaoxing.activity.model.OrgConfig;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import com.chaoxing.activity.service.manager.MoocApiService;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.chaoxing.activity.service.org.OrgConfigService;
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
    private ActivityClassifyQueryService activityClassifyQueryService;

    @Resource
    private MoocApiService moocApiService;

    @Resource
    private ActivityCreatePermissionMapper activityCreatePermissionMapper;

    @Resource
    private OrgConfigService orgConfigService;

    /**查询机构fid下roleId的角色权限
    * @Description
    * @author huxiaolong
    * @Date 2021-06-02 16:19:10
    * @param fid
    * @param roleId
    * @return com.chaoxing.activity.model.ActivityCreatePermission
    */
    public ActivityCreatePermission getPermissionByFidRoleId(Integer fid, Integer roleId) {
        List<ActivityCreatePermission> activityCreatePermissions = activityCreatePermissionMapper.selectList(new QueryWrapper<ActivityCreatePermission>()
                .lambda()
                .eq(ActivityCreatePermission::getFid, fid)
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
    * @param activityCreatePermissionList
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void batchConfigPermission(Integer uid, List<ActivityCreatePermission> activityCreatePermissionList) {
        if (CollectionUtils.isEmpty(activityCreatePermissionList)) {
            return;
        }
        ActivityCreatePermission commonConfig = activityCreatePermissionList.get(0);
        Integer fid = commonConfig.getFid();

        List<Integer> roleIds = activityCreatePermissionList.stream().map(ActivityCreatePermission::getRoleId).collect(Collectors.toList());
        List<ActivityCreatePermission> existPermissions = activityCreatePermissionMapper.selectList(new QueryWrapper<ActivityCreatePermission>()
                .lambda()
                .eq(ActivityCreatePermission::getFid, fid)
                .in(ActivityCreatePermission::getRoleId, roleIds)
                .eq(ActivityCreatePermission::getDeleted, Boolean.FALSE));

        // 更新数据
        List<Integer> existIds = Lists.newArrayList();
        List<Integer> existRoleIds = Lists.newArrayList();

        for (ActivityCreatePermission permission : existPermissions) {
            existIds.add(permission.getId());
            existRoleIds.add(permission.getRoleId());
        }

        OrgConfig orgConfig = orgConfigService.getByFid(fid);
        int count = activityCreatePermissionMapper.selectCount(new QueryWrapper<ActivityCreatePermission>().lambda().ne(ActivityCreatePermission::getGroupType, orgConfig.getSignUpScopeType()));
        // 若存在与机构类型不匹配的权限配置，则清空并修改机构类型
        if (count != 0) {
            clearOtherDiffGroupPermission(uid, fid);
        }
        // 批量更新数据
        if (!existIds.isEmpty()) {
            activityCreatePermissionMapper.update(null, new UpdateWrapper<ActivityCreatePermission>()
                    .lambda()
                    .in(ActivityCreatePermission::getId, existIds)
                    .set(ActivityCreatePermission::getAllActivityClassify, commonConfig.getAllActivityClassify())
                    .set(ActivityCreatePermission::getActivityClassifyScope, commonConfig.getActivityClassifyScope())
                    .set(ActivityCreatePermission::getSignUpScopeType, commonConfig.getSignUpScopeType())
                    .set(ActivityCreatePermission::getSignUpScope, commonConfig.getSignUpScope())
                    .set(ActivityCreatePermission::getUpdateUid, uid)
                    .set(ActivityCreatePermission::getUpdateTime, LocalDateTime.now()));
        }

        roleIds.removeAll(existRoleIds);
        if (!roleIds.isEmpty()) {
            List<ActivityCreatePermission> waitSavePermissions = Lists.newArrayList();
            for (Integer roleId : roleIds) {
                waitSavePermissions.add(ActivityCreatePermission.builder()
                        .fid(fid)
                        .roleId(roleId)
                        .allActivityClassify(commonConfig.getAllActivityClassify())
                        .activityClassifyScope(commonConfig.getActivityClassifyScope())
                        .signUpScopeType(commonConfig.getSignUpScopeType())
                        .signUpScope(commonConfig.getSignUpScope())
                        .createUid(uid)
                        .updateUid(uid)
                        .build());
            }
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
    public ActivityCreatePermissionDTO getGroupClassifyByUserPermission(Integer fid, Integer uid) {
        ActivityCreatePermissionDTO activityCreatePermission = ActivityCreatePermissionDTO.buildDefault();
        // 查询用户的角色id
        List<Integer> userRoleIds = moocApiService.getUserRoleIds(fid, uid);
        // 无角色返回空
        if (CollectionUtils.isEmpty(userRoleIds)) {
            return activityCreatePermission;
        }
        OrgConfig orgConfig = orgConfigService.getByFid(fid);
        List<ActivityCreatePermission> createPermissions = activityCreatePermissionMapper.selectList(new QueryWrapper<ActivityCreatePermission>()
                .lambda()
                .eq(ActivityCreatePermission::getFid, fid)
                .in(ActivityCreatePermission::getRoleId, userRoleIds)
                .eq(ActivityCreatePermission::getDeleted, Boolean.FALSE));
        String groupType = "";
        if (CollectionUtils.isNotEmpty(createPermissions)) {
            groupType = createPermissions.get(0).getGroupType();
        } else if (orgConfig != null) {
            groupType = orgConfig.getSignUpScopeType();
        }
        if (StringUtils.isBlank(groupType)) {
            groupType = OrgConfig.SignUpScopeType.WFW.getValue();
        }
        activityCreatePermission.setGroupType(groupType);

        List<WfwGroupDTO> wfwGroups = Lists.newArrayList();
        if (Objects.equals(OrgConfig.SignUpScopeType.WFW.getValue(), groupType)) {
            wfwGroups = wfwGroupApiService.listGroupByFid(fid);
        } else if (Objects.equals(OrgConfig.SignUpScopeType.CONTACTS.getValue(), groupType)) {
            wfwGroups = wfwContactApiService.listUserContactOrgsByFid(fid);
        }

        List<ActivityClassify> activityClassifies = activityClassifyQueryService.listOrgOptional(fid);
        // 若查出的角色配置权限数量少于userRoleIds的数量 证明有未配置权限的角色，则以该角色最大权限返回数据
        if (createPermissions.size() < userRoleIds.size()) {
            activityCreatePermission.setActivityClassifies(activityClassifies);
            activityCreatePermission.setWfwGroups(wfwGroupApiService.buildWfwGroups(wfwGroups));
            return activityCreatePermission;
        }

        // 标识，判断是否设置全量权限范围, setManageGroupScope: 是否已经查找设置主管管理权限，若设置了置位true， 限制只查询一次
        boolean setAllReleaseScope = Boolean.FALSE;
        boolean setManageGroupScope = Boolean.FALSE;
        boolean setClassifyScope = Boolean.FALSE;
        Set<SignUpParticipateScopeDTO> releaseScopes = Sets.newHashSet();
        List<Integer> manageGroupIds = Lists.newArrayList();
        Set<Integer> classifyIdSet = Sets.newHashSet();
        for (ActivityCreatePermission permission : createPermissions) {
            if (setAllReleaseScope && setClassifyScope) {
                return activityCreatePermission;
            }
            // 若有角色配置为不限，则以该配置返回角色权限数据
            if (!setAllReleaseScope) {
                if (Objects.equals(permission.getSignUpScopeType(), ActivityCreatePermission.SignUpScopeType.NO_LIMIT.getValue())) {
                    activityCreatePermission.setWfwGroups(wfwGroupApiService.buildWfwGroups(wfwGroups));
                    setAllReleaseScope = Boolean.TRUE;
                }
                if (!setAllReleaseScope) {
                    if (!setManageGroupScope && Objects.equals(permission.getSignUpScopeType(), ActivityCreatePermission.SignUpScopeType.COMPETENT_RANGE.getValue())) {
                        if (Objects.equals(permission.getGroupType(), OrgConfig.SignUpScopeType.WFW.getValue())) {
                            manageGroupIds = wfwGroupApiService.listUserManageGroupIdByFid(fid, uid);
                        } else if (Objects.equals(permission.getGroupType(), OrgConfig.SignUpScopeType.CONTACTS.getValue())) {
                            List<WfwDepartmentDTO> departments = wfwContactApiService.listManagerDepartment(fid, uid);
                            manageGroupIds = departments.stream().map(WfwDepartmentDTO::getId).collect(Collectors.toList());
                        }
                        setManageGroupScope = Boolean.TRUE;
                    }
                    // 获取用户角色发布范围并集
                    if (StringUtils.isNotBlank(permission.getSignUpScope())) {
                        releaseScopes.addAll(JSON.parseArray(permission.getSignUpScope(), SignUpParticipateScopeDTO.class));
                    }
                }
            }
            if (!setClassifyScope) {
                if (permission.getAllActivityClassify()) {
                    activityCreatePermission.setActivityClassifies(activityClassifies);
                    setClassifyScope = Boolean.TRUE;
                }
                // 获取用户角色活动类型范围并集
                if (StringUtils.isNotBlank(permission.getActivityClassifyScope())) {
                    List<String> splitIds = Arrays.asList(permission.getActivityClassifyScope().split(","));
                    List<Integer> ids = Lists.newArrayList();
                    CollectionUtils.collect(splitIds, Integer::valueOf, ids);
                    classifyIdSet.addAll(ids);
                }
            }
        }
        if (!setAllReleaseScope) {
            // 基于不存在角色有不限的发布范围，若有设置基于管理的角色，查询管理的组织架构，构建和发布范围一致的实体
            if (setManageGroupScope) {
                List<SignUpParticipateScopeDTO> additionalReleaseScopes = packageReleaseScopes(manageGroupIds, wfwGroups);
                releaseScopes.addAll(additionalReleaseScopes);
            }
            activityCreatePermission.setWfwGroups(buildWfwGroups(releaseScopes, wfwGroups));
        }
        if (!setClassifyScope) {
            activityCreatePermission.setActivityClassifies(listActivityClassify(classifyIdSet, activityClassifies));
        }
        activityCreatePermission.setExistNoLimitPermission(Boolean.FALSE);
        return activityCreatePermission;
    }

    /**根据管理的机构id，构建发布范围实体
    * @Description
    * @author huxiaolong
    * @Date 2021-06-04 18:12:52
    * @param manageGroupIds
    * @param wfwGroups
    * @return java.util.List<com.chaoxing.activity.dto.sign.SignUpParticipateScopeDTO>
    */
    private List<SignUpParticipateScopeDTO> packageReleaseScopes(List<Integer> manageGroupIds, List<WfwGroupDTO> wfwGroups) {
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

    /**根据权限中的活动类型范围并集，获取过滤权限下的活动类型列表
     * @Description 
    * @author huxiaolong
    * @Date 2021-06-03 15:44:28
    * @param classifyIdSet
    * @param activityClassifies
    * @return java.util.List<com.chaoxing.activity.model.ActivityClassify>
    */
    private List<ActivityClassify> listActivityClassify(Set<Integer> classifyIdSet, List<ActivityClassify> activityClassifies) {
        List<ActivityClassify> result = Lists.newArrayList();
        for (ActivityClassify classify: activityClassifies) {
            if (classifyIdSet.contains(classify.getId())) {
                result.add(classify);
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public void clearOtherDiffGroupPermission(Integer uid, Integer fid) {
        OrgConfig orgConfig = orgConfigService.getByFid(fid);
        activityCreatePermissionMapper.update(null, new UpdateWrapper<ActivityCreatePermission>()
                .lambda()
                .eq(ActivityCreatePermission::getFid, fid)
                .set(ActivityCreatePermission::getGroupType, orgConfig.getSignUpScopeType())
                .set(ActivityCreatePermission::getSignUpScopeType, ActivityCreatePermission.SignUpScopeType.NO_LIMIT.getValue())
                .set(ActivityCreatePermission::getSignUpScope, null)
                .set(ActivityCreatePermission::getUpdateUid, uid)
                .set(ActivityCreatePermission::getUpdateTime, LocalDateTime.now()));
    }
}
