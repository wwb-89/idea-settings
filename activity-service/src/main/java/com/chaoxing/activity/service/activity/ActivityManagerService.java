package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.ActivityManagerMapper;
import com.chaoxing.activity.model.ActivityManager;
import com.chaoxing.activity.util.Pagination;
import com.chaoxing.activity.util.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/** 组织者管理服务
 * @className ActivityManagerService
 * @description 
 * @author wwb
 * @blame wwb
 * @date 2021-03-27 12:43:38
 * @version ver 1.0
 */
@Slf4j
@Service
public class ActivityManagerService {

    @Resource
    private ActivityManagerMapper activityManagerMapper;

    @Resource
    private ActivityValidationService activityValidationService;

    /**分页查询管理员列表
     * @Description 
     * @author wwb
     * @Date 2021-03-27 12:31:58
     * @param page
     * @param activityId
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.ActivityManager>
    */
    public Page<ActivityManager> paging(Page<ActivityManager> page, Integer activityId){
        return activityManagerMapper.paging(page, activityId);
    }

    /**新增
     * @Description
     * @author wwb
     * @Date 2021-03-28 21:29:24
     * @param activityManager
     * @param loginUser
     * @return boolean
     */
    public boolean add(ActivityManager activityManager, LoginUserDTO loginUser){
        boolean creator = activityValidationService.isCreator(activityManager.getActivityId(), loginUser);
        if (!creator) {
            throw new BusinessException("无权限");
        }
        activityManager.setCreateUid(loginUser.getUid());
        List<ActivityManager> activityManagers = activityManagerMapper.selectList(new LambdaQueryWrapper<ActivityManager>()
                .eq(ActivityManager::getActivityId, activityManager.getActivityId())
                .eq(ActivityManager::getUid, activityManager.getUid()));
        if (CollectionUtils.isEmpty(activityManagers)) {
            activityManagerMapper.insert(activityManager);
            return true;
        }
        return false;
    }

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2021-03-28 21:33:27
     * @param activityManagers
     * @param loginUser
     * @return void
    */
    public void batchAdd(List<ActivityManager> activityManagers, LoginUserDTO loginUser) {
        if (CollectionUtils.isEmpty(activityManagers)) {
            return;
        }
        Integer activityId = activityManagers.get(0).getUid();
        boolean creator = activityValidationService.isCreator(activityId, loginUser);
        if (!creator) {
            throw new BusinessException("无权限");
        }
        List<Integer> uids = activityManagers.stream().map(ActivityManager::getUid).collect(Collectors.toList());
        List<ActivityManager> existActivityManagers = activityManagerMapper.selectList(new LambdaQueryWrapper<ActivityManager>()
                .eq(ActivityManager::getActivityId, activityId)
                .in(ActivityManager::getUid, uids));
        List<Integer> existUids = existActivityManagers.stream().map(ActivityManager::getUid).collect(Collectors.toList());
        List<ActivityManager> addActivityManagers = Lists.newArrayList();
        for (ActivityManager activityManager : activityManagers) {
            Integer uid = activityManager.getUid();
            if (!existUids.contains(uid)) {
                activityManager.setCreateUid(loginUser.getUid());
                addActivityManagers.add(activityManager);
            }
        }
        if (CollectionUtils.isEmpty(addActivityManagers)) {
            activityManagerMapper.batchAdd(addActivityManagers);
        }
    }

    /**删除
     * @Description 
     * @author wwb
     * @Date 2021-03-28 21:29:43
     * @param activityId
     * @param uid
     * @param loginUser
     * @return void
    */
    public void delete(Integer activityId, Integer uid, LoginUserDTO loginUser) {
        boolean creator = activityValidationService.isCreator(activityId, loginUser);
        if (!creator) {
            throw new BusinessException("无权限");
        }
        activityManagerMapper.delete(new LambdaQueryWrapper<ActivityManager>()
                .eq(ActivityManager::getActivityId, activityId)
                .eq(ActivityManager::getUid, uid));
    }

    /**批量删除
     * @Description 
     * @author wwb
     * @Date 2021-03-28 21:29:59
     * @param activityId
     * @param uids
     * @param loginUser
     * @return void
    */
    public void batchDelete(Integer activityId, List<Integer> uids, LoginUserDTO loginUser) {
        boolean creator = activityValidationService.isCreator(activityId, loginUser);
        if (!creator) {
            throw new BusinessException("无权限");
        }
        if (!CollectionUtils.isEmpty(uids)) {
            activityManagerMapper.delete(new LambdaQueryWrapper<ActivityManager>()
                    .eq(ActivityManager::getActivityId, activityId)
                    .in(ActivityManager::getUid, uids));
        }
    }

    /**查询活动的组织者uid列表
     * @Description 
     * @author wwb
     * @Date 2021-03-28 20:38:19
     * @param activityId
     * @return java.util.List<java.lang.Integer>
    */
    public List<Integer> listUid(Integer activityId) {
        List<ActivityManager> activityManagers = activityManagerMapper.selectList(new QueryWrapper<ActivityManager>()
                .lambda()
                .eq(ActivityManager::getActivityId, activityId)
                .select(ActivityManager::getUid)
        );
        return activityManagers.stream().map(ActivityManager::getUid).collect(Collectors.toList());
    }
    
}
