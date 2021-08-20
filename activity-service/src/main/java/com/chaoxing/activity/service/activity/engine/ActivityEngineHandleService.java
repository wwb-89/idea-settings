package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.engine.ActivityEngineDTO;
import com.chaoxing.activity.mapper.*;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.component.ComponentHandleService;
import com.chaoxing.activity.service.manager.WfwFormApiService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    @Resource
    private ComponentHandleService componentHandleService;
    @Autowired
    private SignUpConditionMapper signUpConditionMapper;
    @Autowired
    private SignUpFillInfoTypeMapper signUpFillInfoTypeMapper;
    @Autowired
    private WfwFormApiService wfwFormApiService;

    /**处理引擎模板组件相关数据(新增/更新)
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 16:21:07
    * @param activityEngineDTO
    * @return void
    */
    public void handleEngineTemplate(Integer fid, Integer marketId, Integer uid, ActivityEngineDTO activityEngineDTO) {
        Template template = activityEngineDTO.getTemplate();
        List<TemplateComponent> templateComponents = activityEngineDTO.getTemplateComponents();
        if (template.getSystem() && template.getFid() == null) {
            // todo 临时测试，默认新建一个template
            Template newTemplate = Template.builder()
                    .name("通用组件模板")
                    .fid(fid)
                    .marketId(marketId)
                    .originTemplateId(template.getOriginTemplateId())
                    .activityFlag(template.getActivityFlag())
                    .createUid(uid)
                    .updateUid(uid)
                    .build();
            activityEngineDTO.setTemplate(newTemplate);
            saveOperation(newTemplate, templateComponents, activityEngineDTO.getCustomComponentIds(), activityEngineDTO.getDelCustomComponentIds());
            return;
        }
        updateOperation(template, templateComponents, activityEngineDTO.getDelTemplateComponentIds(), activityEngineDTO.getDelTemplateComponentIds());
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
    public void saveOperation(Template template, List<TemplateComponent> templateComponents, List<Integer> customComponentIds, List<Integer> delCustomComponentIds) {
        // 保存模板
        templateMapper.insert(template);
        // 保存模板组件关联关系
        saveTemplateComponent(template.getId(), templateComponents);
        // 更新自定义组件关联templateId
        componentHandleService.relatedComponentWithTemplateId(template.getId(), customComponentIds);
        // 删除指定自定义组件
        componentHandleService.deleteCustomComponents(delCustomComponentIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOperation(Template template, List<TemplateComponent> templateComponents, List<Integer> delTemplateComponentIds, List<Integer> delCustomComponentIds) {
        // 更新模板
        templateMapper.updateById(template);
        // 保存模板组件关联关系
        updateTemplateComponent(template.getId(), templateComponents);
        // 取消组件关联关系
        if (CollectionUtils.isNotEmpty(delTemplateComponentIds)) {
            templateComponentMapper.update(null, new UpdateWrapper<TemplateComponent>()
                    .lambda()
                    .in(TemplateComponent::getId, delTemplateComponentIds)
                    .set(TemplateComponent::getDeleted, Boolean.TRUE));
        }
        // 删除指定自定义组件
        componentHandleService.deleteCustomComponents(delCustomComponentIds);
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
    public Component handleCustomComponent(Integer uid, Integer fid, Component component) {
        if (component.getId() == null) {
            return saveCustomComponent(uid, fid, component);
        }
        return updateCustomComponent(uid, fid, component);
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
    public Component updateCustomComponent(Integer uid, Integer fid, Component component) {
        return componentHandleService.updateCustomComponent(uid, fid, component);
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
    public Component saveCustomComponent(Integer uid, Integer fid, Component component) {
        return componentHandleService.saveCustomComponent(uid, fid, component);
    }

    /**新增模板组件关联
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 14:52:34
    * @param templateId 新模板id
    * @param
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void saveTemplateComponent(Integer templateId, Collection<TemplateComponent> templateComponents) {
        if (CollectionUtils.isEmpty(templateComponents)) {
            return;
        }
        templateComponents.forEach(v -> v.setTemplateId(templateId));
        templateComponentMapper.batchAdd(templateComponents);
        templateComponents.forEach(v -> {
            handleSignUpConditionFillInfoType(v);
            if (CollectionUtils.isNotEmpty(v.getChildren())) {
                v.getChildren().forEach(v1 -> {
                    v1.setPid(v.getId());
                    v1.setTemplateId(templateId);
                });
                templateComponentMapper.batchAdd(v.getChildren());
                v.getChildren().forEach(this::handleSignUpConditionFillInfoType);
            }
        });
    }

    /**
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-17 14:58:58
    * @param templateComponent
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void handleSignUpConditionFillInfoType (TemplateComponent templateComponent) {
        if (templateComponent.getSignUpCondition() != null) {
            templateComponent.getSignUpCondition().setTemplateComponentId(templateComponent.getId());
            signUpConditionMapper.insert(templateComponent.getSignUpCondition());
        }
        if (templateComponent.getSignUpFillInfoType() != null) {
            templateComponent.getSignUpFillInfoType().setTemplateComponentId(templateComponent.getId());
            signUpFillInfoTypeMapper.insert(templateComponent.getSignUpFillInfoType());
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
        List<TemplateComponent> waitSaveTplComponent = Lists.newArrayList();
        List<TemplateComponent> waitUpdateTplComponent = Lists.newArrayList();
        buildSaveUpdateList(templateComponents, waitSaveTplComponent, waitUpdateTplComponent);

        // 新增
        saveTemplateComponent(templateId, waitSaveTplComponent);
        // 更新
        waitUpdateTplComponent.forEach(v -> {
            TemplateComponent tplComponent = new TemplateComponent();
            BeanUtils.copyProperties(v, tplComponent);
            templateComponentMapper.updateById(tplComponent);
            SignUpCondition suc = v.getSignUpCondition();
            SignUpFillInfoType signUpFillInfoType = v.getSignUpFillInfoType();
            if (suc != null) {
                suc.setTemplateComponentId(tplComponent.getId());
                if (suc.getId() == null) {
                    signUpConditionMapper.insert(suc);
                } else {
                    signUpConditionMapper.updateById(suc);
                }
            }
            if (signUpFillInfoType != null) {
                signUpFillInfoType.setTemplateComponentId(tplComponent.getId());
                if (signUpFillInfoType.getId() == null) {
                    signUpFillInfoTypeMapper.insert(signUpFillInfoType);
                } else {
                    signUpFillInfoTypeMapper.updateById(signUpFillInfoType);
                }
            }
        });
    }

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-07-15 15:26:34
    * @param tplComponents
    * @param waitSaveTplComponent
    * @param waitUpdateTplComponent
    * @return void
    */
    private void buildSaveUpdateList(List<TemplateComponent> tplComponents,
                     List<TemplateComponent> waitSaveTplComponent,
                     List<TemplateComponent> waitUpdateTplComponent) {
        tplComponents.forEach(v -> {
            if (v.getId() == null) {
                waitSaveTplComponent.add(v);
            } else {
                waitUpdateTplComponent.add(v);
                if (CollectionUtils.isNotEmpty(v.getChildren())) {
                    buildSaveUpdateList(v.getChildren(), waitSaveTplComponent, waitUpdateTplComponent);
                }
            }
        });
    }
}
