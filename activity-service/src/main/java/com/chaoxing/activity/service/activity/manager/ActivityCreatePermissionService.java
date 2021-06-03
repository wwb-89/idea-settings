package com.chaoxing.activity.service.activity.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.manager.WfwGroupDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpParticipateScope;
import com.chaoxing.activity.mapper.ActivityCreatePermissionMapper;
import com.chaoxing.activity.model.ActivityCreatePermission;
import com.chaoxing.activity.service.manager.MoocApiService;
import com.chaoxing.activity.service.manager.WfwGroupApiService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private MoocApiService moocApiService;

    @Autowired
    private ActivityCreatePermissionMapper activityCreatePermissionMapper;

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

    /**根据fid和用户id，查找用户角色权限下的group集合
    * @Description
    * @author huxiaolong
    * @Date 2021-06-03 14:29:03
    * @param fid
    * @param uid
    * @return java.util.List<com.chaoxing.activity.dto.manager.WfwGroupDTO>
    */
    public List<WfwGroupDTO> listGroupByFid(Integer fid, Integer uid) {
        // 查询用户的角色id
        List<Integer> userRoleIds = moocApiService.getUserRoleIds(fid, uid);
        List<WfwGroupDTO> wfwGroups = wfwGroupApiService.listGroupByFid(fid);
        if (CollectionUtils.isNotEmpty(userRoleIds)) {
            List<ActivityCreatePermission> createPermissions = activityCreatePermissionMapper.selectList(new QueryWrapper<ActivityCreatePermission>()
                    .lambda()
                    .eq(ActivityCreatePermission::getFid, fid)
                    .in(ActivityCreatePermission::getRoleId, userRoleIds)
                    .eq(ActivityCreatePermission::getDeleted, Boolean.FALSE));
            if (CollectionUtils.isNotEmpty(createPermissions)) {
                Set<SignUpParticipateScope> releaseScopes = Sets.newHashSet();
                for (ActivityCreatePermission permission : createPermissions) {
                    if (StringUtils.isNotBlank(permission.getSignUpScope())) {
                        releaseScopes.addAll(JSON.parseArray(permission.getSignUpScope(), SignUpParticipateScope.class));
                    }
                }
                // scopeGroupIdSet: 不重复的发布范围groupId集, leafGroupIdSet: 发布范围叶子节点groupId集， intersectionSet:发布范围groupId交集
                Set<Integer> scopeGroupIdSet = Sets.newHashSet();
                Set<Integer> leafGroupIdSet = Sets.newHashSet();
                Set<Integer> intersectionSet = Sets.newHashSet();
                for (SignUpParticipateScope scope : releaseScopes) {
                    Integer externalId = scope.getExternalId();
                    scopeGroupIdSet.add(externalId);
                    if (scope.getLeaf()) {
                        leafGroupIdSet.add(externalId);
                    } else {
                        intersectionSet.add(externalId);
                    }
                }
                // 得到交集
                intersectionSet.retainAll(leafGroupIdSet);
                Map<Integer, WfwGroupDTO> wfwGroupMap = Maps.newHashMap();
                for (WfwGroupDTO group : wfwGroups) {
                    group.setVirtualId(group.getId());
                    wfwGroupMap.put(Integer.valueOf(group.getId()), group);
                }
                Set<WfwGroupDTO> result = Sets.newHashSet();
                Set<Integer> resultIdSet = Sets.newHashSet();
                for (Integer groupId : scopeGroupIdSet) {
                    findReleaseWfwGroups(groupId, wfwGroupMap, intersectionSet, result, resultIdSet);
                }
                return new ArrayList<>(result);
            }
        }
        // 根据角色id
        return wfwGroups;
    }

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-06-03 14:25:03
    * @param groupId 当前需添加节点groupId
    * @param wfwGroupMap { groupId: wfwGroup }Map映射
    * @param intersectionSet 发布范围子节点和非子节点groupId交集部分
    * @param result 结果集
    * @param resultIdSet 结果集Id集合， 用于判断是否需要继续递归
    * @return void
    */
    private void findReleaseWfwGroups(Integer groupId, Map<Integer, WfwGroupDTO> wfwGroupMap, Set<Integer> intersectionSet,
                                     Set<WfwGroupDTO> result, Set<Integer> resultIdSet) {
        if (groupId == null) {
            return;
        }
        // 获取当前节点
        WfwGroupDTO group = wfwGroupMap.get(groupId);
        if (group == null) {
            return;
        }
        if (resultIdSet.contains(groupId)) {
            return;
        }
        if (intersectionSet.contains(groupId)) {
            WfwGroupDTO item = new WfwGroupDTO();
            BeanUtils.copyProperties(group, item);
            item.setVirtualId(UUID.randomUUID().toString().trim().replaceAll("-", ""));
            item.setGid(item.getId());
            result.add(item);
        }
        result.add(group);
        resultIdSet.add(groupId);
        // 判断是否有pid, 有的话递归去查找
        groupId = StringUtils.isBlank(group.getGid()) ? null : Integer.valueOf(group.getGid());
        findReleaseWfwGroups(groupId, wfwGroupMap, intersectionSet, result, resultIdSet);

    }
}
