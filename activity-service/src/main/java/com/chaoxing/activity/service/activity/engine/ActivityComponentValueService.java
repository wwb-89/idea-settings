package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.activity.ActivityComponentValueDTO;
import com.chaoxing.activity.mapper.ActivityComponentValueMapper;
import com.chaoxing.activity.model.ActivityComponentValue;
import com.chaoxing.activity.model.TemplateComponent;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/7/19 3:43 下午
 * <p>
 */

@Slf4j
@Service
public class ActivityComponentValueService {

    @Resource
    private ActivityComponentValueMapper activityComponentValueMapper;
    @Resource
    private TemplateComponentService templateComponentService;

    @Transactional(rollbackFor = Exception.class)
    public void saveActivityComponentValues(Integer activityId, List<ActivityComponentValueDTO> activityComponentValueDTOList) {
        if (CollectionUtils.isEmpty(activityComponentValueDTOList)) {
            return;
        }
        List<ActivityComponentValue> activityComponentValues = Lists.newArrayList();
        activityComponentValueDTOList.forEach(v -> {
            activityComponentValues.add(ActivityComponentValue
                    .builder()
                    .activityId(activityId)
                    .componentId(v.getComponentId())
                    .templateId(v.getTemplateId())
                    .templateComponentId(v.getTemplateComponentId())
                    .value(v.getValue())
                    .build());
        });
        activityComponentValueMapper.batchAdd(activityComponentValues);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateActivityComponentValues(Integer activityId, List<ActivityComponentValueDTO> activityComponentValues) {
        List<ActivityComponentValueDTO> waitSaveData = Lists.newArrayList();
        if (CollectionUtils.isEmpty(activityComponentValues)) {
            return;
        }
        activityComponentValues.forEach(v -> {
            if (v.getId() == null) {
                waitSaveData.add(v);
            } else {
                activityComponentValueMapper.updateById(ActivityComponentValue.builder()
                        .id(v.getId())
                        .activityId(v.getActivityId())
                        .templateId(v.getTemplateId())
                        .templateComponentId(v.getTemplateComponentId())
                        .componentId(v.getComponentId())
                        .value(v.getValue())
                        .build());
            }
        });
        saveActivityComponentValues(activityId, waitSaveData);

    }

    /**根据活动id查询自定义组件的值列表
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-26 15:22:47
    * @param activityId
    * @return java.util.List<com.chaoxing.activity.dto.activity.ActivityComponentValueDTO>
    */
    public List<ActivityComponentValueDTO> listActivityComponentValuesByActivity(Integer activityId) {
        List<ActivityComponentValue> activityComponentValues = activityComponentValueMapper.selectList(new QueryWrapper<ActivityComponentValue>()
                .lambda().eq(ActivityComponentValue::getActivityId, activityId));
        if (CollectionUtils.isEmpty(activityComponentValues)) {
            return Lists.newArrayList();
        }
        List<Integer> tplComponentIds = activityComponentValues.stream().map(ActivityComponentValue::getTemplateComponentId).collect(Collectors.toList());

        Map<Integer, String> idNameMap = templateComponentService.listByTplComponentIds(tplComponentIds).stream()
                .collect(Collectors.toMap(TemplateComponent::getId, TemplateComponent::getName, (v1, v2) -> v2));

        return activityComponentValues.stream().map(v -> ActivityComponentValueDTO.builder()
                        .id(v.getId())
                        .value(v.getValue())
                        .componentId(v.getComponentId())
                        .templateComponentId(v.getTemplateComponentId())
                        .templateComponentName(Optional.ofNullable(v.getTemplateComponentId()).map(idNameMap::get).orElse(""))
                        .templateId(v.getTemplateId())
                        .build())
                .collect(Collectors.toList());
    }

    /**查询活动activityIds列表的自定义组件的值列表
    * @Description
    * @author huxiaolong
    * @Date 2021-09-26 15:35:59
    * @param activityIds
    * @return java.util.Map<java.lang.Integer,java.util.List<com.chaoxing.activity.dto.activity.ActivityComponentValueDTO>>
    */
    public Map<Integer, List<ActivityComponentValueDTO>> listActivityComponentValuesByActivities(List<Integer> activityIds) {
        if (CollectionUtils.isEmpty(activityIds)) {
            return Maps.newHashMap();
        }
        List<ActivityComponentValue> activityComponentValues = activityComponentValueMapper.selectList(new QueryWrapper<ActivityComponentValue>()
                .lambda().in(ActivityComponentValue::getActivityId, activityIds)
                .orderByAsc(ActivityComponentValue::getActivityId));

        Set<Integer> tplComponentIds = activityComponentValues.stream().map(ActivityComponentValue::getTemplateComponentId).collect(Collectors.toSet());

        Map<Integer, String> idNameMap = templateComponentService.listByTplComponentIds(new ArrayList<>(tplComponentIds)).stream()
                .collect(Collectors.toMap(TemplateComponent::getId, TemplateComponent::getName, (v1, v2) -> v2));

        Map<Integer, List<ActivityComponentValueDTO>> result = Maps.newHashMap();
        activityComponentValues.forEach(v -> {
            Integer activityId = v.getActivityId();
            ActivityComponentValueDTO item = ActivityComponentValueDTO.builder()
                    .id(v.getId())
                    .activityId(activityId)
                    .value(v.getValue())
                    .componentId(v.getComponentId())
                    .templateComponentId(v.getTemplateComponentId())
                    .templateComponentName(Optional.ofNullable(v.getTemplateComponentId()).map(idNameMap::get).orElse(""))
                    .templateId(v.getTemplateId())
                    .build();
            result.computeIfAbsent(activityId, k -> Lists.newArrayList());
            result.get(activityId).add(item);
        });
        return result;
    }
}
