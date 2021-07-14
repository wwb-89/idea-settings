package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.engine.ActivityEngineDTO;
import com.chaoxing.activity.mapper.*;
import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.model.ComponentField;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.model.TemplateComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
    @Autowired
    private SignUpConditionMapper signUpConditionMapper;

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
        List<TemplateComponent> templateComponents = activityEngineDTO.getTemplateComponents();
        if (template.getSystem() && template.getFid() == null) {
            // todo 临时测试，默认新建一个template
            Template newTemplate = Template.builder().name("自建测试模板").fid(fid).createUid(uid).updateUid(uid).build();
            activityEngineDTO.setTemplate(newTemplate);
            saveOperation(newTemplate, templateComponents, activityEngineDTO.getCustomComponentIds());
            return;
        }
        updateOperation(template, templateComponents, activityEngineDTO.getDelTemplateComponentIds());
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
    public void saveOperation(Template template, List<TemplateComponent> templateComponents, List<Integer> customComponentIds) {
        // 保存模板
        templateMapper.insert(template);
        // 保存模板组件关联关系
        saveTemplateComponent(template.getId(), templateComponents);
        // 更新自定义组件关联templateId
        if (CollectionUtils.isNotEmpty(customComponentIds)) {
            componentMapper.update(null, new UpdateWrapper<Component>()
                    .lambda()
                    .in(Component::getId, customComponentIds)
                    .set(Component::getTemplateId, template.getId()));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOperation(Template template, List<TemplateComponent> templateComponents, List<Integer> delTemplateComponentIds) {
        // 更新模板
        templateMapper.updateById(template);
        // 保存模板组件关联关系
        updateTemplateComponent(template.getId(), templateComponents);

        if (CollectionUtils.isNotEmpty(delTemplateComponentIds)) {
            templateComponentMapper.update(null, new UpdateWrapper<TemplateComponent>()
                    .lambda()
                    .in(TemplateComponent::getId, delTemplateComponentIds)
                    .set(TemplateComponent::getDeleted, Boolean.TRUE));
        }

    }


    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-07-14 10:30:36
    * @param uid
    * @param component
    * @return com.chaoxing.activity.model.Component
    */
    @Transactional(rollbackFor = Exception.class)
    public Component handleCustomComponent(Integer uid, Component component) {
        if (component.getId() == null) {
            return saveCustomComponent(uid, component);
        }
        return updateCustomComponent(uid, component);
    }


    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-07-14 10:29:27
    * @param uid
    * @param component
    * @return com.chaoxing.activity.model.Component
    */
    @Transactional(rollbackFor = Exception.class)
    public Component updateCustomComponent(Integer uid, Component component) {
        component.setUpdateUid(uid);
        componentMapper.updateById(component);
        if (Objects.equals(component.getDataOrigin(), Component.DataOriginEnum.CUSTOM.getValue())
                && CollectionUtils.isNotEmpty(component.getFieldList())) {
            componentFieldMapper.delete(new QueryWrapper<ComponentField>().lambda().eq(ComponentField::getComponentId, component.getId()));

            component.getFieldList().forEach(v -> {
                v.setCreateUid(uid);
                v.setUpdateUid(uid);
                v.setComponentId(component.getId());
            });
            componentFieldMapper.batchAdd(component.getFieldList());
        }
        return component;
    }


    /**新增自定义组件
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 18:50:00
    * @param uid
    * @param component
    * @return java.util.Map<java.lang.Integer,com.chaoxing.activity.model.Component>
    */
    @Transactional(rollbackFor = Exception.class)
    public Component saveCustomComponent(Integer uid, Component component) {
        component.setCreateUid(uid);
        component.setUpdateUid(uid);
        componentMapper.insert(component);
        List<ComponentField> fieldList = component.getFieldList();
        component = componentMapper.selectById(component.getId());
        if (Objects.equals(component.getDataOrigin(), Component.DataOriginEnum.CUSTOM.getValue())
                && CollectionUtils.isNotEmpty(fieldList)) {
            for (ComponentField field : fieldList) {
                field.setCreateUid(uid);
                field.setUpdateUid(uid);
                field.setComponentId(component.getId());
            }
            componentFieldMapper.batchAdd(fieldList);
        }
        return component;
    }

    /**新增模板组件关联
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 14:52:34
    * @param templateId 新模板id
    * @param
    * @return void
    */
    public void saveTemplateComponent(Integer templateId, Collection<TemplateComponent> templateComponents) {
        for (TemplateComponent templateComponent : templateComponents) {
            templateComponent.setTemplateId(templateId);
        }
        if (CollectionUtils.isNotEmpty(templateComponents)) {
            templateComponentMapper.batchAdd(templateComponents);
        }
        for (TemplateComponent templateComponent : templateComponents) {
            if (templateComponent.getSignUpCondition() != null) {
                templateComponent.getSignUpCondition().setTemplateComponentId(templateComponent.getId());
                signUpConditionMapper.insert(templateComponent.getSignUpCondition());
            }

            if (CollectionUtils.isNotEmpty(templateComponent.getChildren())) {
                templateComponent.getChildren().forEach(item -> {
                    item.setPid(templateComponent.getId());
                    item.setTemplateId(templateId);
                });
                templateComponentMapper.batchAdd(templateComponent.getChildren());

                for (TemplateComponent child : templateComponent.getChildren()) {
                    if (child.getSignUpCondition() != null) {
                        child.getSignUpCondition().setTemplateComponentId(child.getId());
                        signUpConditionMapper.insert(child.getSignUpCondition());
                    }
                }
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
        List<TemplateComponent> waitUpdateTemplateComponents = Lists.newArrayList();
        List<TemplateComponent> waitSaveTemplateComponents = Lists.newArrayList();

        for (TemplateComponent templateComponent : templateComponents) {
            if (templateComponent.getId() == null) {
                waitSaveTemplateComponents.add(templateComponent);
            } else {
                waitUpdateTemplateComponents.add(templateComponent);
                if (CollectionUtils.isNotEmpty(templateComponent.getChildren())) {
                    for (TemplateComponent child : templateComponent.getChildren()) {
                        if (child.getId() == null) {
                            waitSaveTemplateComponents.add(child);
                        } else {
                            child.setPid(templateComponent.getId());
                            waitUpdateTemplateComponents.add(child);
                        }
                    }
                }
            }
        }
        for (TemplateComponent tplComponent : waitUpdateTemplateComponents) {
            templateComponentMapper.updateById(tplComponent);
            if (tplComponent.getSignUpCondition() != null) {
                tplComponent.getSignUpCondition().setTemplateComponentId(tplComponent.getId());
                if (tplComponent.getSignUpCondition().getId() == null) {
                    signUpConditionMapper.insert(tplComponent.getSignUpCondition());
                } else {
                    signUpConditionMapper.updateById(tplComponent.getSignUpCondition());
                }
            }
        }

        saveTemplateComponent(templateId, waitSaveTemplateComponents);
    }
}
