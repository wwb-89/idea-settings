package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ActivityManagerMapper;
import com.chaoxing.activity.model.ActivityManager;
import com.chaoxing.activity.util.Pagination;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class ActivityManagerService {

    @Resource
    private ActivityManagerMapper activityManagerMapper;

    public boolean add(ActivityManager activityManager){
        ActivityManager manager = activityManagerMapper.selectOne(new LambdaQueryWrapper<ActivityManager>()
                .eq(ActivityManager::getActivityId, activityManager.getActivityId())
                .eq(ActivityManager::getUid, activityManager.getUid()));
        if(manager == null) {
            activityManagerMapper.insert(activityManager);
            return true;
        }
        return false;
    }

    public List<ActivityManager> getActivityManagers(Integer activityId, Pagination pagination){
        ActivityManager query = new ActivityManager();
        query.setActivityId(activityId);
        QueryWrapper<ActivityManager> queryWrapper = new QueryWrapper<>(query);
        queryWrapper.orderByDesc("create_time");
        PageHelper.startPage(pagination.getPage(),pagination.getPageSize());
        List<ActivityManager> activityManagers = activityManagerMapper.selectList(queryWrapper);
        return activityManagers;
    }

    public void delete(Integer activityId,Integer uid){
        activityManagerMapper.delete(new LambdaQueryWrapper<ActivityManager>()
                .eq(ActivityManager::getActivityId,activityId)
                .eq(ActivityManager::getUid,uid));
    }

    public void deleteBatch(Integer activityId,List<Integer> uids){
        if(!CollectionUtils.isEmpty(uids)) {
            activityManagerMapper.delete(new LambdaQueryWrapper<ActivityManager>()
                    .eq(ActivityManager::getActivityId, activityId)
                    .in(ActivityManager::getUid, uids));
        }
    }

}
