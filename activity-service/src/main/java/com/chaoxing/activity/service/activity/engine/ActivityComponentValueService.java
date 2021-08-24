package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.activity.ActivityComponentValueDTO;
import com.chaoxing.activity.mapper.ActivityComponentValueMapper;
import com.chaoxing.activity.model.ActivityComponentValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
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

    public List<ActivityComponentValueDTO> listActivityComponentValuesByActivity(Integer activityId) {
        return activityComponentValueMapper.selectList(new QueryWrapper<ActivityComponentValue>()
                .lambda().eq(ActivityComponentValue::getActivityId, activityId))
                .stream()
                .map(v -> ActivityComponentValueDTO.builder()
                        .id(v.getId())
                        .value(v.getValue())
                        .componentId(v.getComponentId())
                        .templateComponentId(v.getTemplateComponentId())
                        .templateId(v.getTemplateId())
                        .build())
                .collect(Collectors.toList());
    }
}
