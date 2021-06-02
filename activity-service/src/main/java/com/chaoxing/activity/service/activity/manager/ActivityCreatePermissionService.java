package com.chaoxing.activity.service.activity.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.ActivityCreatePermissionMapper;
import com.chaoxing.activity.model.ActivityCreatePermission;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
}
