package com.chaoxing.activity.service.activity.scope;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ActivityClassMapper;
import com.chaoxing.activity.model.ActivityClass;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/2 17:20
 * <p>
 */
@Slf4j
@Service
public class ActivityClassService {

    @Autowired
    private ActivityClassMapper activityClassMapper;

    /**根据活动id查询发布班级id列表
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-02 18:47:33
    * @param activityId
    * @return java.util.List<java.lang.Integer>
    */
    public List<Integer> listClassIdsByActivity(Integer activityId) {
        if (activityId == null) {
            return Lists.newArrayList();
        }
        return activityClassMapper.selectList(new LambdaQueryWrapper<ActivityClass>()
                .eq(ActivityClass::getActivityId, activityId))
                .stream()
                .map(ActivityClass::getClassId)
                .collect(Collectors.toList());
    }

    /**批量新增或修改活动关联发布班级
    * @Description
    * @author huxiaolong
    * @Date 2021-09-02 17:21:32
    * @param activityId
    * @param classIds
    * @return
    */
    @Transactional(rollbackFor = Exception.class)
    public void batchAddOrUpdate(Integer activityId, List<Integer> classIds) {
        if (activityId == null || CollectionUtils.isEmpty(classIds)) {
            return;
        }
        List<Integer> existActivityClassIds = listClassIdsByActivity(activityId);
        if (CollectionUtils.isNotEmpty(existActivityClassIds)) {
            // 获取交集，即不进行处理班级id
            List<Integer> retainPartIds = Lists.newCopyOnWriteArrayList(existActivityClassIds);
            retainPartIds.retainAll(classIds);
            // 移除交集部分，则剩余部分已删除
            existActivityClassIds.removeAll(retainPartIds);
            if (CollectionUtils.isNotEmpty(existActivityClassIds)) {
                activityClassMapper.delete(new LambdaQueryWrapper<ActivityClass>()
                        .eq(ActivityClass::getActivityId, activityId)
                        .in(ActivityClass::getClassId, existActivityClassIds));
            }
            // 移除交集部分，则剩余部分待新增
            classIds.removeAll(retainPartIds);
        }
        
        if (CollectionUtils.isNotEmpty(classIds)) {
            List<ActivityClass> waitSaveList = Lists.newArrayList();
            classIds.forEach(v -> {
                waitSaveList.add(ActivityClass.builder()
                        .activityId(activityId)
                        .classId(v)
                        .build());
            });
            activityClassMapper.batchAdd(waitSaveList);
        }
    }
}
