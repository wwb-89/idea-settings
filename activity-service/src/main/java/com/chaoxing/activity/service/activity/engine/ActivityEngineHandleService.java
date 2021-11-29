package com.chaoxing.activity.service.activity.engine;

import com.chaoxing.activity.dto.engine.ActivityEngineDTO;
import com.chaoxing.activity.mapper.TemplateMapper;
import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.model.TemplateComponent;
import com.chaoxing.activity.service.activity.component.ComponentHandleService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
    @Resource
    private ComponentHandleService componentHandleService;
    @Resource
    private TemplateComponentService templateComponentService;

    /**处理引擎模板组件相关数据(新增/更新)
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 16:21:07
    * @param activityEngineDTO
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void handleEngineTemplate(Integer fid, Integer marketId, Integer uid, ActivityEngineDTO activityEngineDTO) {
        Template template = activityEngineDTO.getTemplate();
        List<TemplateComponent> templateComponents = TemplateComponent.buildFromDTO(activityEngineDTO.getTemplateComponents());
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
            saveOperation(newTemplate, templateComponents, activityEngineDTO.getCustomComponentIds());
        } else {
            updateOperation(template, templateComponents, activityEngineDTO.getDelTemplateComponentIds());
        }
    }

    /**新增操作(新增模板、新增组件、新增模板组件关联关系)
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 16:21:44
    * @param template
    * @param templateComponents
    * @return void
    */
    public void saveOperation(Template template, List<TemplateComponent> templateComponents, List<Integer> customComponentIds) {
        // 保存模板
        templateMapper.insert(template);
        // 保存模板组件关联关系
        templateComponentService.saveTemplateComponent(template.getId(), templateComponents);
        // 更新自定义组件关联templateId
        componentHandleService.relatedComponentWithTemplateId(template.getId(), customComponentIds);
    }

    public void updateOperation(Template template, List<TemplateComponent> templateComponents, List<Integer> delTemplateComponentIds) {
        // 更新模板
        templateMapper.updateById(template);
        // 保存模板组件关联关系
        updateTemplateComponent(template.getId(), templateComponents);
        // 取消组件关联关系
        templateComponentService.cancelTemplateComponent(delTemplateComponentIds);
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
        return componentHandleService.updateCustomComponent(uid, component);
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
        return componentHandleService.saveCustomComponent(uid, component);
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
        templateComponentService.saveTemplateComponent(templateId, waitSaveTplComponent);
        // 更新
        templateComponentService.updateTemplateComponents(waitUpdateTplComponent);
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
