package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.engine.ActivityEngineDTO;
import com.chaoxing.activity.dto.engine.TemplateComponentDTO;
import com.chaoxing.activity.mapper.*;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.WfwFormApiService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/7/6 2:29 下午
 * <p>
 */
@Slf4j
@Service
public class ActivityEngineQueryService {

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
    @Autowired
    private SignUpFillInfoTypeMapper signUpFillInfoTypeMapper;
    @Resource
    private WfwFormApiService wfwFormApiService;
    @Resource
    private TemplateQueryService templateQueryService;


    public ActivityEngineDTO findEngineTemplateInfo(Integer templateId) {
        // 查询模板数据
        Template template = templateMapper.selectById(templateId);
        // 查询组件数据
        List<Component> components = listComponentByTemplateId(templateId);
        // 查询模板组件关联关系 todo 后续对前端进行处理，会移除templateComponents，直接使用showTemplateComponents
        List<TemplateComponentDTO> showTplComponents = listTemplateComponentByTemplateId(templateId);
        List<TemplateComponent> templateComponents = Lists.newArrayList();
        CollectionUtils.collect(showTplComponents, o -> TemplateComponent.builder()
                .id(o.getId())
                .pid(o.getPid())
                .name(o.getName())
                .introduction(o.getIntroduction())
                .componentId(o.getComponentId())
                .templateId(o.getTemplateId())
                .signUpCondition(o.getSignUpCondition())
                .sequence(o.getSequence())
                .build(), templateComponents);
        return ActivityEngineDTO.builder()
                .template(template)
                .components(components)
                .templateComponents(templateComponents)
                .showTemplateComponents(showTplComponents)
                .build();
    }


    /**根据机构fid，查询除系统模板外，其他模板
    * @Description
    * @author huxiaolong
    * @Date 2021-07-06 14:35:58
    * @param fid
    * @return java.util.List<com.chaoxing.activity.model.Template>
    */
    public List<Template> listTemplateByFid(Integer fid, Integer marketId) {
        return templateMapper.selectList(new QueryWrapper<Template>()
                .lambda()
                .eq(Template::getSystem, Boolean.TRUE)
                .or(j -> j.eq(Template::getFid, fid).eq(Template::getMarketId, marketId))
                .orderByAsc(Template::getSequence));
    }

    /**系统组件 + templateId 的自定义组件 = 组件集合
    * @Description
    * @author huxiaolong
    * @Date 2021-07-07 15:31:05
    * @param templateId
    * @return void
    */
    public List<Component> listComponentByTemplateId(Integer templateId) {
        // 系统组件(isSystem: true, templateId: null) + templateId 自身的组件
        List<Component> components = componentMapper.selectList(new QueryWrapper<Component>()
                .lambda()
                .eq(Component::getTemplateId, templateId).or().eq(Component::getSystem, Boolean.TRUE));
        for (Component component : components) {
            if (component.isSystemComponent()) {
                continue;
            }
            if (Objects.equals(component.getDataOrigin(), Component.DataOriginEnum.FORM.getValue())) {
                // 表单
                String originIdentify = component.getOriginIdentify();
                String fieldFlag = component.getFieldFlag();
                Template template = templateQueryService.getById(templateId);
                List<String> fieldValues = wfwFormApiService.listFormFieldValue(template.getFid(), Integer.parseInt(originIdentify), fieldFlag);
                component.setFormFieldValues(fieldValues);
            } else {
                // 自定义
                List<ComponentField> componentFields = componentFieldMapper.selectList(new QueryWrapper<ComponentField>()
                        .lambda()
                        .eq(ComponentField::getComponentId, component.getId()));
                component.setFieldList(componentFields);
            }
        }
        return components;
    }

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-07-07 15:30:53
    * @param templateId
    * @return void
    */
    public List<TemplateComponentDTO> listTemplateComponentByTemplateId(Integer templateId) {
        List<TemplateComponentDTO> templateComponents = templateComponentMapper.listTemplateComponentInfo(templateId);
        // 报名条件templateComponentIds
        List<Integer> sucTplComponentIds = templateComponents.stream()
                .filter(v -> v.getPid() != 0 && Objects.equals(v.getCode(), "sign_up_condition"))
                .map(TemplateComponentDTO::getId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(sucTplComponentIds)) {
            Map<Integer, SignUpCondition> signUpConditionMap = signUpConditionMapper.selectList(new QueryWrapper<SignUpCondition>()
                    .lambda()
                    .in(SignUpCondition::getTemplateComponentId, sucTplComponentIds))
                    .stream()
                    .collect(Collectors.toMap(SignUpCondition::getTemplateComponentId, v -> v, (v1, v2) -> v2));
            templateComponents.forEach(v -> {
                v.setSignUpCondition(Optional.ofNullable(signUpConditionMap.get(v.getId())).orElse(null));
            });
        }
        return templateComponents;
    }

    public List<TemplateComponentDTO> listTemplateComponentTree(Integer templateId) {
        List<TemplateComponentDTO> templateComponents = templateComponentMapper.listTemplateComponentInfo(templateId);

        List<Integer> chooseComponentIds = templateComponents.stream()
                .filter(v -> StringUtils.isNotBlank(v.getType()) && Objects.equals(v.getDataOrigin(), Component.DataOriginEnum.CUSTOM.getValue()))
                .map(TemplateComponentDTO::getComponentId)
                .collect(Collectors.toList());
        Map<Integer, List<ComponentField>> componentFieldMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(chooseComponentIds)) {
            List<ComponentField> componentFields = componentFieldMapper.selectList(new QueryWrapper<ComponentField>().lambda().in(ComponentField::getComponentId, chooseComponentIds));
            componentFields.forEach(v -> {
                componentFieldMap.computeIfAbsent(v.getComponentId(), k -> Lists.newArrayList());
                componentFieldMap.get(v.getComponentId()).add(v);
            });
            templateComponents.forEach(v -> {
                if (CollectionUtils.isNotEmpty(componentFieldMap.get(v.getComponentId()))) {
                    v.setComponentFields(componentFieldMap.get(v.getComponentId()));
                }
            });
        }

        List<TemplateComponentDTO> trees = Lists.newArrayList();
        templateComponents.forEach(v -> {
            if (Objects.equals(v.getPid(), 0)) {
                trees.add(v);
            }
            templateComponents.forEach(v1 -> {
                if (!Objects.equals(v1.getPid(), 0) && Objects.equals(v1.getPid(), v.getId())) {
                    if (v.getChildren() == null) {
                        v.setChildren(new ArrayList<>());
                    }
                    v.getChildren().add(v1);
                }
            });
        });
        return trees;
    }
}
