package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.engine.ActivityEngineDTO;
import com.chaoxing.activity.mapper.ComponentFieldMapper;
import com.chaoxing.activity.mapper.ComponentMapper;
import com.chaoxing.activity.mapper.TemplateComponentMapper;
import com.chaoxing.activity.mapper.TemplateMapper;
import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.model.TemplateComponent;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/7/6 2:29 下午
 * <p>
 */
@Slf4j
@Service
public class ActivityEngineHandleService {

    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private TemplateComponentMapper templateComponentMapper;
    @Autowired
    private ComponentMapper componentMapper;
    @Autowired
    private ComponentFieldMapper componentFieldMapper;

    private final Integer ROOT_ID = 0;


    /**处理引擎模板组件相关数据(新增/更新)
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 16:21:07
    * @param activityEngineDTO
    * @return void
    */
    public void handleEngineTemplate(Integer fid, Integer uid, ActivityEngineDTO activityEngineDTO) {
        Template template = activityEngineDTO.getTemplate();
        if (template.getSystem() && template.getFid() == null) {
            // todo 临时测试，默认新建一个template
            Template newTemplate = Template.builder().name("自建测试模板").fid(fid).createUid(uid).updateUid(uid).build();
            activityEngineDTO.setTemplate(newTemplate);
            saveOperation(newTemplate, activityEngineDTO.getTemplateComponents());
            return;
        }
        updateOperation(template, activityEngineDTO.getTemplateComponents());
    }


    /**新增操作(新增模板、新增组件、新增模板组件关联关系)
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 16:21:44
    * @param template
    * @param templateComponents
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void saveOperation(Template template, List<TemplateComponent> templateComponents) {
        // 保存模板
        templateMapper.insert(template);
        // 保存模板组件关联关系
        saveTemplateComponent(template.getId(), templateComponents);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOperation(Template template, List<TemplateComponent> templateComponents) {
        // 更新模板
        templateMapper.updateById(template);
        // 保存模板组件关联关系
        updateTemplateComponent(template.getId(), templateComponents);
    }

    /**新增自定义组件
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 18:50:00
    * @param fid
    * @param components
    * @return java.util.Map<java.lang.Integer,com.chaoxing.activity.model.Component>
    */
    public Map<Integer, Component> saveCustomComponent(Integer fid, List<Component> components) {
        return null;
    }


    /**新增组件
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 11:52:12
    * @param
    * @return void
    */
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public Map<Integer, Component> saveComponent(Integer fid, List<Component> components) {
        Map<Integer, Component> nonPidComponentMap = Maps.newHashMap();
        Map<Integer, Component> hasPidComponentMap = Maps.newHashMap();
        List<Component> customComponents = Lists.newArrayList();
        for (Component component : components) {
            component.setFid(fid);
            component.setSystem(Boolean.FALSE);
            if (component.getId() == null) {
                customComponents.add(component);
            } else if (Objects.equals(component.getPid(), ROOT_ID)) {
                nonPidComponentMap.put(component.getId(), component);
            } else {
                hasPidComponentMap.put(component.getId(), component);
            }
        }
        if (!nonPidComponentMap.isEmpty()) {
            // 先存 pid为空的
            componentMapper.batchAdd(nonPidComponentMap.values());
            // 更新子节点对应的父节点id
            for (Component component : hasPidComponentMap.values()) {
                Component parentComponent = nonPidComponentMap.get(component.getPid());
                if (parentComponent != null) {
                    component.setPid(parentComponent.getId());
                }
            }
        }

        if (!hasPidComponentMap.isEmpty()) {
            // 再存 子节点
            componentMapper.batchAdd(hasPidComponentMap.values());
        }
        Map<Integer, Component> result = Stream.concat(nonPidComponentMap.entrySet().stream(), hasPidComponentMap.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));

        if (CollectionUtils.isNotEmpty(customComponents)) {
            // 最后存  自定义组件
            componentMapper.batchAdd(customComponents);
            for (Component customComponent : customComponents) {
                result.put(customComponent.getId(), customComponent);
            }
        }
        return result;
    }

    /**更新自定义组件
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 14:53:14
    * @param
    * @return void
    */
    public Map<Integer, Component> updateComponent(Integer fid, List<Component> components) {
        List<Component> waitSaveComponents = Lists.newArrayList();
        Map<Integer, Component> result = Maps.newHashMap();
        for (Component component : components) {
            if (component.getId() == null) {
                waitSaveComponents.add(component);
            } else {
                // 更新
                componentMapper.updateById(component);
                result.put(component.getId(), component);
            }
        }

        // 处理新增的自定义组件
        if (CollectionUtils.isNotEmpty(waitSaveComponents)) {
            Map<Integer, Component> savedComponentMap = saveComponent(fid, waitSaveComponents);
            savedComponentMap.forEach(
                    (key, value) -> result.merge(key, value, (v1, v2) -> v2));
        }

        return result;
    }

    /**新增模板组件关联
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 14:52:34
    * @param templateId 新模板id
    * @param
    * @return void
    */
    public void saveTemplateComponent(Integer templateId, List<TemplateComponent> templateComponents) {
        for (TemplateComponent templateComponent : templateComponents) {
            templateComponent.setTemplateId(templateId);
        }
        if (CollectionUtils.isNotEmpty(templateComponents)) {
            templateComponentMapper.batchAdd(templateComponents);
        }
        for (TemplateComponent templateComponent : templateComponents) {
            if (CollectionUtils.isNotEmpty(templateComponent.getChildren())) {
                templateComponent.getChildren().forEach(item -> item.setPid(templateComponent.getId()));
                templateComponentMapper.batchAdd(templateComponent.getChildren());
            }
        }
    }
    
    /**更新模板组件关联
    * @Description 
    * @author huxiaolong
    * @Date 2021-07-08 15:54:00
    * @param 
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplateComponent(Integer templateId, List<TemplateComponent> templateComponents) {
        // 删除模板对应的组件关联关系
        templateComponentMapper.delete(new QueryWrapper<TemplateComponent>()
                .lambda()
                .eq(TemplateComponent::getTemplateId, templateId));
        // 重新建立关联关系
        saveTemplateComponent(templateId, templateComponents);
    }
}
