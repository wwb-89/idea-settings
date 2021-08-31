package com.chaoxing.activity.service.activity.component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.ComponentFieldMapper;
import com.chaoxing.activity.mapper.ComponentMapper;
import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.model.ComponentField;
import com.chaoxing.activity.service.manager.WfwFormApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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
    @Resource
    private WfwFormApiService wfwFormApiService;


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
        component.setUpdateUid(uid);
        componentMapper.updateById(component);
        if (CollectionUtils.isNotEmpty(component.getComponentFields())) {
            // 先删后加
            componentFieldMapper.delete(new QueryWrapper<ComponentField>().lambda().eq(ComponentField::getComponentId, component.getId()));
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


}
