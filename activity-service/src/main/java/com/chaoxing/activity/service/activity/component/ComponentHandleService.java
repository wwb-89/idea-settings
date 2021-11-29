package com.chaoxing.activity.service.activity.component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.ComponentFieldMapper;
import com.chaoxing.activity.mapper.ComponentMapper;
import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.model.ComponentField;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/20 20:25
 * <p>
 */
@Slf4j
@Service
public class ComponentHandleService {

    @Autowired
    private ComponentMapper componentMapper;
    @Autowired
    private ComponentFieldMapper componentFieldMapper;


    /**新增自定义组件
    * @Description
    * @author huxiaolong
    * @Date 2021-08-20 20:37:48
    * @param uid
    * @param component
    * @return com.chaoxing.activity.model.Component
    */
    @Transactional(rollbackFor = Exception.class)
    public Component saveCustomComponent(Integer uid, Component component) {
        component.setCreateUid(uid);
        component.setUpdateUid(uid);
        componentMapper.insert(component);
        List<ComponentField> componentFields = component.getComponentFields();
        component = componentMapper.selectById(component.getId());
        if (CollectionUtils.isNotEmpty(componentFields)) {
            for (ComponentField field : componentFields) {
                field.setCreateUid(uid);
                field.setUpdateUid(uid);
                field.setComponentId(component.getId());
            }
            componentFieldMapper.batchAdd(componentFields);
            component.setComponentFields(componentFields);
        }
        return component;
    }

    /**更新自定义组件
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-20 20:37:25
    * @param uid
    * @param component
    * @return com.chaoxing.activity.model.Component
    */
    @Transactional(rollbackFor = Exception.class)
    public Component updateCustomComponent(Integer uid, Component component) {
        componentMapper.update(null, new UpdateWrapper<Component>().lambda()
                .eq(Component::getId, component.getId())
                .set(Component::getName, component.getName())
                .set(Component::getIntroduction, component.getIntroduction())
                .set(Component::getCode, component.getCode())
                .set(Component::getRequired, component.getRequired())
                .set(Component::getType, component.getType())
                .set(Component::getDataOrigin, component.getDataOrigin())
                .set(Component::getOriginIdentify, component.getOriginIdentify())
                .set(Component::getFieldFlag, component.getFieldFlag())
                .set(Component::getUpdateUid, uid)
                .set(Component::getUpdateTime, LocalDateTime.now())
        );
        // 先删后加
        componentFieldMapper.delete(new QueryWrapper<ComponentField>().lambda().eq(ComponentField::getComponentId, component.getId()));
        if (CollectionUtils.isNotEmpty(component.getComponentFields())) {
            component.getComponentFields().forEach(v -> {
                v.setCreateUid(uid);
                v.setUpdateUid(uid);
                v.setComponentId(component.getId());
            });
            componentFieldMapper.batchAdd(component.getComponentFields());
        }
        return component;
    }

    /**给自定义组件关联模板
    * @Description
    * @author huxiaolong
    * @Date 2021-08-20 20:27:44
    * @param templateId
    * @param customComponentIds
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void relatedComponentWithTemplateId(Integer templateId, List<Integer> customComponentIds) {
        if (templateId == null || CollectionUtils.isEmpty(customComponentIds)) {
            return;
        }
        componentMapper.update(null, new UpdateWrapper<Component>()
                .lambda()
                .in(Component::getId, customComponentIds)
                .set(Component::getTemplateId, templateId));
    }

    /**批量删除自定义组件
    * @Description
    * @author huxiaolong
    * @Date 2021-08-20 20:30:05
    * @param customComponentIds
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomComponents(List<Integer> customComponentIds) {
        if (CollectionUtils.isEmpty(customComponentIds)) {
            return;
        }
        componentMapper.delete(new QueryWrapper<Component>()
                .lambda()
                .eq(Component::getSystem, Boolean.FALSE)
                .in(Component::getId, customComponentIds));
    }

    /**批量新增自定义组件
     * @Description
     * @author huxiaolong
     * @Date 2021-11-29 13:43:05
     * @param components
     * @param uid
     * @return void
     */
    public void batchAdd(List<Component> components, Integer uid) {
       if (CollectionUtils.isNotEmpty(components)) {
           components.forEach(v -> saveCustomComponent(uid, v));
       }
    }

}
