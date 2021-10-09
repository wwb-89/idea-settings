package com.chaoxing.activity.service.activity.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import com.chaoxing.activity.mapper.ActivityManagerMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityManager;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuService;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** 管理员服务
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
    @Resource
    private ActivityMenuService activityMenuService;

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
        Integer activityId = activityManager.getActivityId();
        boolean creator = activityValidationService.isCreator(activityId, loginUser.getUid());
        if (!creator) {
            throw new BusinessException("无权限");
        }
        activityManager.setCreateUid(loginUser.getUid());
        List<ActivityManager> activityManagers = activityManagerMapper.selectList(new LambdaQueryWrapper<ActivityManager>()
                .eq(ActivityManager::getActivityId, activityId)
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
        Integer activityId = activityManagers.get(0).getActivityId();
        for (ActivityManager activityManager : activityManagers) {
            activityManager.setActivityId(activityId);
        }
        boolean creator = activityValidationService.isCreator(activityId, loginUser.getUid());
        if (!creator) {
            throw new BusinessException("无权限");
        }
        List<Integer> uids = activityManagers.stream().map(ActivityManager::getUid).collect(Collectors.toList());
        List<ActivityManager> existActivityManagers = activityManagerMapper.selectList(new LambdaQueryWrapper<ActivityManager>()
                .eq(ActivityManager::getActivityId, activityId)
                .in(ActivityManager::getUid, uids));
        List<Integer> existUids = existActivityManagers.stream().map(ActivityManager::getUid).collect(Collectors.toList());
        List<ActivityManager> addActivityManagers = Lists.newArrayList();
        List<String> activityMenus = activityMenuService.listMenus(activityId).stream().map(ActivityMenuDTO::getValue).collect(Collectors.toList());
        for (ActivityManager activityManager : activityManagers) {
            Integer uid = activityManager.getUid();
            if (!existUids.contains(uid)) {
                activityManager.setCreateUid(loginUser.getUid());
                activityManager.setMenu(StringUtils.join(activityMenus, ","));
                addActivityManagers.add(activityManager);
            }
        }
        if (CollectionUtils.isNotEmpty(addActivityManagers)) {
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
        Activity activity = activityValidationService.creator(activityId, loginUser.getUid());
        // 不能删除创建者
        if (Objects.equals(uid, activity.getCreateUid())) {
            throw new BusinessException("不能删除创建者");
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
        Activity activity = activityValidationService.creator(activityId, loginUser.getUid());
        // 排除活动创建者
        Integer createUid = activity.getCreateUid();
        uids.remove(createUid);
        if (!CollectionUtils.isEmpty(uids)) {
            activityManagerMapper.delete(new LambdaQueryWrapper<ActivityManager>()
                    .eq(ActivityManager::getActivityId, activityId)
                    .in(ActivityManager::getUid, uids));
        }
    }

    /**查询活动的管理员uid列表
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

    /**更新活动管理者菜单权限
    * @Description
    * @author huxiaolong
    * @Date 2021-09-28 14:16:13
    * @param activityId
    * @param activityManager
    * @param loginUser
    * @return void
    */
    public void updateActivityManageMenu(Integer activityId, ActivityManager activityManager, LoginUserDTO loginUser) {
        // 活动创建者权限校验
        activityValidationService.creator(activityId, loginUser.getUid());

        activityManagerMapper.update(null, new UpdateWrapper<ActivityManager>()
                .lambda()
                .eq(ActivityManager::getActivityId, activityId)
                .eq(ActivityManager::getUid, activityManager.getUid())
                .set(ActivityManager::getMenu, activityManager.getMenu()));
    }

    /**根据活动id、uid查询活动管理者信息
    * @Description
    * @author huxiaolong
    * @Date 2021-09-28 14:16:31
    * @param activityId
    * @param uid
    * @return com.chaoxing.activity.model.ActivityManager
    */
    public ActivityManager getByActivityUid(Integer activityId, Integer uid) {
        return activityManagerMapper.selectList(new LambdaQueryWrapper<ActivityManager>()
                .eq(ActivityManager::getActivityId, activityId)
                .eq(ActivityManager::getUid, uid))
                .stream().findFirst().orElse(null);
    }
}
