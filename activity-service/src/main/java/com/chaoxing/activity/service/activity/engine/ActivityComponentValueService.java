package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    /**根据模板id，活动id查询自定义组件的值列表
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-26 15:22:47
    * @param activityId
    * @return java.util.List<com.chaoxing.activity.dto.activity.ActivityComponentValueDTO>
    */
    public List<ActivityComponentValueDTO> listActivityComponentValues(Integer activityId, Integer templateId) {
        // 根据模板查询自定义组件列表
        List<TemplateComponent> customTplComponents = templateComponentService.listCustomTemplateComponent(templateId);
        if (CollectionUtils.isEmpty(customTplComponents)) {
            return Lists.newArrayList();
        }
        List<Integer> customTplComponentIds = customTplComponents.stream().map(TemplateComponent::getId).collect(Collectors.toList());

        // 自定义组件tplComponentIds +  模板id + 活动activityId查询自定义组件数据
        List<ActivityComponentValue> activityComponentValues = activityComponentValueMapper.selectList(new QueryWrapper<ActivityComponentValue>()
                .lambda()
                .eq(ActivityComponentValue::getActivityId, activityId)
                .eq(ActivityComponentValue::getTemplateId, templateId)
                .in(ActivityComponentValue::getTemplateComponentId, customTplComponentIds));
        Map<Integer, ActivityComponentValue> tplComponentValueMap = activityComponentValues.stream()
                .collect(Collectors.toMap(ActivityComponentValue::getTemplateComponentId, v -> v, (v1, v2) -> v2));
        // 返回结果
        return packageActivityComponentValues(customTplComponents, tplComponentValueMap, activityId);
    }

    /**批量查询活动<activityId，template>集合的自定义组件的值列表
    * @Description
    * @author huxiaolong
    * @Date 2021-09-26 15:35:59
    * @param activityTemplateMap
    * @return java.util.Map<java.lang.Integer,java.util.List<com.chaoxing.activity.dto.activity.ActivityComponentValueDTO>>
    */
    public Map<Integer, List<ActivityComponentValueDTO>> listActivityComponentValues(Map<Integer, Integer> activityTemplateMap) {
        if (activityTemplateMap == null || activityTemplateMap.isEmpty()) {
            return Maps.newHashMap();
        }
        // 获取模板id集合
        List<Integer> templateIds = activityTemplateMap.values().stream().distinct().collect(Collectors.toList());
        // 查询模板id集合下的自定义组件
        List<TemplateComponent> customTplComponents = templateComponentService.listCustomTemplateComponent(templateIds);
        if (CollectionUtils.isEmpty(customTplComponents)) {
            return Maps.newHashMap();
        }
        // 封装模板 -> 自定义组件集合映射
        Map<Integer, List<TemplateComponent>> templateCustomTplComponentsMap = Maps.newHashMap();
        customTplComponents.forEach(v -> {
            Integer templateId = v.getTemplateId();
            templateCustomTplComponentsMap.computeIfAbsent(templateId, k -> Lists.newArrayList());
            templateCustomTplComponentsMap.get(templateId).add(v);

        });
        // 封装模板+自定义组件id集合查询条件
        LambdaQueryWrapper<ActivityComponentValue> wrapper = new LambdaQueryWrapper<>();
        templateCustomTplComponentsMap.forEach((key, value) -> {
            if (CollectionUtils.isNotEmpty(value)) {
                List<Integer> tplCmptIds = value.stream().map(TemplateComponent::getId).collect(Collectors.toList());
                wrapper.or(j -> j.eq(ActivityComponentValue::getTemplateId, key).in(ActivityComponentValue::getTemplateComponentId, tplCmptIds));
            }
        });
        List<ActivityComponentValue> activityComponentValues = activityComponentValueMapper.selectList(wrapper);

        Map<Integer, ActivityComponentValue> tplComponentValueMap = activityComponentValues.stream()
                .collect(Collectors.toMap(ActivityComponentValue::getTemplateComponentId, v -> v, (v1, v2) -> v2));

        Map<Integer, List<ActivityComponentValueDTO>> result = Maps.newHashMap();
        activityTemplateMap.forEach((activityId, templateId) -> {
            // 获取活动模板对应的自定义组件id集合，用于封装活动自定义字段数据
            List<TemplateComponent> customTplCmpts = templateCustomTplComponentsMap.get(templateId);
            if (CollectionUtils.isNotEmpty(customTplCmpts)) {
                List<ActivityComponentValueDTO> activityComponentValueRes = packageActivityComponentValues(customTplCmpts, tplComponentValueMap, activityId);
                result.put(activityId, activityComponentValueRes);
            }
        });
        return result;
    }

    /**包装活动自定义组件值的名称，并进行实体类型转换
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-27 18:20:00
    * @param customTplComponents
    * @param tplComponentValueMap
    * @param activityId
    * @return java.util.List<com.chaoxing.activity.dto.activity.ActivityComponentValueDTO>
    */
    private List<ActivityComponentValueDTO> packageActivityComponentValues(List<TemplateComponent> customTplComponents,
                                                                           Map<Integer, ActivityComponentValue> tplComponentValueMap,
                                                                           Integer activityId) {
        List<ActivityComponentValueDTO> activityComponentValueRes = Lists.newArrayList();
        customTplComponents.forEach(v -> {
            ActivityComponentValue item = tplComponentValueMap.get(v.getId());
            if (item == null || !Objects.equals(item.getActivityId(), activityId)) {
                activityComponentValueRes.add(ActivityComponentValueDTO.builder()
                        .templateComponentName(v.getName())
                        .value("")
                        .build());
            } else {
                activityComponentValueRes.add(ActivityComponentValueDTO.builder()
                        .id(item.getId())
                        .value(item.getValue())
                        .componentId(item.getComponentId())
                        .templateComponentId(item.getTemplateComponentId())
                        .templateComponentName(v.getName())
                        .templateId(item.getTemplateId())
                        .build());
            }
        });
        return activityComponentValueRes;
    }

    /** 根据活动ids查询活动自定义组件值
     * @Description
     * @author huxiaolong
     * @Date 2021-11-29 14:20:00
     * @param activityIds
     * @return java.util.List<com.chaoxing.activity.model.ActivityComponentValue>
     */
    public List<ActivityComponentValue> listByActivityIds(List<Integer> activityIds) {
        if (CollectionUtils.isEmpty(activityIds)) {
            return Lists.newArrayList();
        }
        return activityComponentValueMapper.selectList(new LambdaQueryWrapper<ActivityComponentValue>()
                .in(ActivityComponentValue::getActivityId, activityIds));
    }

    /**获取活动某个组件的值
     * @Description 
     * @author wwb
     * @Date 2021-11-30 19:36:55
     * @param activityId
     * @param componentId
     * @return java.lang.String
    */
    public String getActivityComponentValue(Integer activityId, Integer componentId) {
        List<ActivityComponentValue> activityComponentValues = activityComponentValueMapper.selectList(new LambdaQueryWrapper<ActivityComponentValue>()
                .eq(ActivityComponentValue::getActivityId, activityId)
                .eq(ActivityComponentValue::getComponentId, componentId)
        );
        return activityComponentValues.stream().findFirst().map(ActivityComponentValue::getValue).orElse("");
    }

}