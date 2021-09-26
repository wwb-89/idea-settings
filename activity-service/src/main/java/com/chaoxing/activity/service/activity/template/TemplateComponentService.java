package com.chaoxing.activity.service.activity.template;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.engine.TemplateComponentDTO;
import com.chaoxing.activity.mapper.ComponentFieldMapper;
import com.chaoxing.activity.mapper.SignUpConditionMapper;
import com.chaoxing.activity.mapper.SignUpFillInfoTypeMapper;
import com.chaoxing.activity.mapper.TemplateComponentMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.component.ComponentQueryService;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import com.chaoxing.activity.service.activity.engine.SignUpFillInfoTypeService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.util.ApplicationContextHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**模板组件服务
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/26 13:25
 * <p>
 */
@Service
public class TemplateComponentService {

    @Autowired
    private TemplateComponentMapper templateComponentMapper;
    @Autowired
    private ComponentFieldMapper componentFieldMapper;
    @Resource
    private SignUpConditionService signUpConditionService;
    @Resource
    private SignUpFillInfoTypeService signUpFillInfoTypeService;
    @Resource
    private ComponentQueryService componentQueryService;
    @Resource
    private WfwFormApiService wfwFormApiService;


    /**根据模版id查询模版组件关联
     * @Description
     * @author wwb
     * @Date 2021-07-14 17:19:04
     * @param templateId
     * @return java.util.List<com.chaoxing.activity.model.TemplateComponent>
     */
    public List<TemplateComponent> listTemplateComponentByTemplateId(Integer templateId) {
        return templateComponentMapper.selectList(new LambdaQueryWrapper<TemplateComponent>()
                .eq(TemplateComponent::getTemplateId, templateId)
                .eq(TemplateComponent::getDeleted, false)
        );
    }

    /**根据模版id和组件id查询模版组件列表
     * @Description
     * @author wwb
     * @Date 2021-07-15 16:08:00
     * @param templateId
     * @param componentId
     * @return java.util.List<com.chaoxing.activity.model.TemplateComponent>
     */
    public List<TemplateComponent> listByTemplateIdAndComponentId(Integer templateId, Integer componentId) {
        return templateComponentMapper.selectList(new LambdaQueryWrapper<TemplateComponent>()
                .eq(TemplateComponent::getTemplateId, templateId)
                .eq(TemplateComponent::getComponentId, componentId)
        );
    }

    /**根据templateComponentId查询子列表
     * @Description
     * @author wwb
     * @Date 2021-07-21 18:50:41
     * @param templateComponentId
     * @return java.util.List<com.chaoxing.activity.model.TemplateComponent>
     */
    public List<TemplateComponent> listSubTemplateComponent(Integer templateComponentId) {
        return templateComponentMapper.selectList(new LambdaQueryWrapper<TemplateComponent>()
                .eq(TemplateComponent::getPid, templateComponentId));
    }

    /**判断模板是否存在报名组件
     * @Description
     * @author huxiaolong
     * @Date 2021-09-02 19:41:40
     * @param templateId
     * @return boolean
     */
    public boolean exitSignUpComponent(Integer templateId) {
        int count = templateComponentMapper.countTemplateSignUp(templateId);
        return count > 0;
    }


    /**根据code获取系统组件在模板下的模板组件id
     * @Description
     * @author huxiaolong
     * @Date 2021-09-23 15:16:33
     * @param templateId
     * @param code
     * @return java.lang.Integer
     */
    public Integer getSysComponentTplComponentId(Integer templateId, String code) {
        Component component = componentQueryService.getSystemComponentByCode(code);
        if (component == null) {
            return null;
        }
        TemplateComponent templateComponent = templateComponentMapper.selectList(new LambdaQueryWrapper<TemplateComponent>()
                .eq(TemplateComponent::getComponentId, component.getId())
                .eq(TemplateComponent::getTemplateId, templateId)).stream().findFirst().orElse(null);

        return Optional.ofNullable(templateComponent).map(TemplateComponent::getId).orElse(null);
    }

    public Map<String, String> codeTOName(Integer templateId) {
        Map<String, String> defaultSystemCodeNameMap = componentQueryService.getSystemComponentCodeNameRelation();
        Map<String, String> customCustomNameToValue = Maps.newHashMap();
        List<TemplateComponentDTO> templateComponents = templateComponentMapper.listTemplateComponentInfo(templateId);
        templateComponents.forEach(v -> {
            if (StringUtils.isNotBlank(v.getCode())) {
                defaultSystemCodeNameMap.put(v.getCode(), v.getName());
            }
        });
        return defaultSystemCodeNameMap;
    }

    /**根据模板Id查询模板关联组件详细数据(含code和模板对应的name)
     * @Description
     * @author huxiaolong
     * @Date 2021-09-26 14:34:17
     * @param templateId
     * @return java.util.List<com.chaoxing.activity.dto.engine.TemplateComponentDTO>
     */
    public List<TemplateComponentDTO> listTemplateComponentInfo(Integer templateId) {
        List<TemplateComponentDTO> templateComponents = templateComponentMapper.listTemplateComponentInfo(templateId);
        packageTemplateComponents(templateComponents);
        return templateComponents;
    }

    private void packageTemplateComponents(List<TemplateComponentDTO> tplCompoenents) {
        // 报名条件templateComponentIds
        List<Integer> sucTplComponentIds = org.apache.commons.compress.utils.Lists.newArrayList();
        // 报名信息填写类型templateComponentIds
        List<Integer> sufiTplComponentIds = org.apache.commons.compress.utils.Lists.newArrayList();
        tplCompoenents.forEach(v -> {
            if (v.getPid() != 0 && Objects.equals(v.getCode(), Component.SystemComponentCodeEnum.SIGN_UP_CONDITION.getValue())) {
                sucTplComponentIds.add(v.getId());
            } else if (v.getPid() != 0 && Objects.equals(v.getCode(), Component.SystemComponentCodeEnum.SIGN_UP_FILL_INFO.getValue())) {
                sufiTplComponentIds.add(v.getId());
            }
        });
        Map<Integer, SignUpCondition> signUpConditionMap = signUpConditionService.listByTemplateComponentIds(sucTplComponentIds).stream()
                .collect(Collectors.toMap(SignUpCondition::getTemplateComponentId, v -> v, (v1, v2) -> v2));
        Map<Integer, SignUpFillInfoType> signUpFillInfoTypeMap = signUpFillInfoTypeService.listByTemplateComponentIds(sufiTplComponentIds).stream()
                .collect(Collectors.toMap(SignUpFillInfoType::getTemplateComponentId, v -> v, (v1, v2) -> v2));
        tplCompoenents.forEach(v -> {
            if (!signUpConditionMap.isEmpty()) {
                v.setSignUpCondition(Optional.ofNullable(signUpConditionMap.get(v.getId())).orElse(null));
            }
            if (!signUpFillInfoTypeMap.isEmpty()) {
                v.setSignUpFillInfoType(Optional.ofNullable(signUpFillInfoTypeMap.get(v.getId())).orElse(null));
            }
        });
    }

    /**查询模板组件关联数据，并安装父子结构进行树结构封装
     * @Description
     * @author huxiaolong
     * @Date 2021-08-03 16:20:36
     * @param templateId
     * @param fid
     * @return java.util.List<com.chaoxing.activity.dto.engine.TemplateComponentDTO>
     */
    public List<TemplateComponentDTO> listTemplateComponentTree(Integer templateId, Integer fid) {
        List<TemplateComponentDTO> templateComponents = templateComponentMapper.listTemplateComponentInfo(templateId);
        buildComponentFieldsAndFieldValues(fid, templateComponents);
        return TemplateComponentDTO.buildTrees(templateComponents);
    }

    /**查询基本信息组件关联数据(不含报名、签到)
     * @Description
     * @author huxiaolong
     * @Date 2021-08-03 16:21:49
     * @param templateId
     * @param fid
     * @return java.util.List<com.chaoxing.activity.dto.engine.TemplateComponentDTO>
     */
    public List<TemplateComponentDTO> listBasicInfoTemplateComponents(Integer templateId, Integer fid) {
        List<TemplateComponentDTO> templateComponents = templateComponentMapper.listTemplateComponentInfo(templateId)
                .stream()
                .filter(v -> v.getPid() == 0
                        && !Objects.equals(v.getCode(), Component.SystemComponentCodeEnum.SIGN_IN_OUT.getValue())
                        && !Objects.equals(v.getCode(), Component.SystemComponentCodeEnum.SIGN_UP.getValue())
                        && !Objects.equals(v.getCode(), Component.SystemComponentCodeEnum.COMPANY_SIGN_UP.getValue()))
                .collect(Collectors.toList());
        buildComponentFieldsAndFieldValues(fid, templateComponents);
        return templateComponents;
    }

    /**查询报名信息组件关联数据(不含基本信息、签到)
     * @Description
     * @author huxiaolong
     * @Date 2021-08-04 17:14:57
     * @param templateId
     * @return java.util.List<com.chaoxing.activity.dto.engine.TemplateComponentDTO>
     */
    public List<TemplateComponentDTO> listSignUpTemplateComponents(Integer templateId) {
        List<TemplateComponentDTO> templateComponents = templateComponentMapper.listTemplateComponentInfo(templateId)
                .stream()
                .filter(v -> v.getPid() != 0 || Objects.equals(v.getCode(), Component.SystemComponentCodeEnum.SIGN_UP.getValue()) || Objects.equals(v.getCode(), Component.SystemComponentCodeEnum.COMPANY_SIGN_UP.getValue()))
                .collect(Collectors.toList());
        templateComponents.forEach(v -> {
            if (StringUtils.isNotBlank(v.getCode()) && Objects.equals(v.getCode(), Component.SystemComponentCodeEnum.SIGN_UP_FILL_INFO.getValue())) {
                v.setSignUpFillInfoType(signUpFillInfoTypeService.getByTemplateComponentId(v.getId()));
            }
        });
        return TemplateComponentDTO.buildTrees(templateComponents);
    }

    /**查询封装自定义选择组件componentFields 自定义选项和 fieldValues表单选项
     * @Description
     * @author huxiaolong
     * @Date 2021-07-30 18:28:55
     * @param fid
     * @param templateComponents
     * @return void
     */
    private void buildComponentFieldsAndFieldValues(Integer fid, List<TemplateComponentDTO> templateComponents) {
        List<Integer> chooseComponentIds = templateComponents.stream()
                .filter(v -> StringUtils.isNotBlank(v.getType()) && Objects.equals(v.getDataOrigin(), Component.DataOriginEnum.CUSTOM.getValue()))
                .map(TemplateComponentDTO::getComponentId)
                .collect(Collectors.toList());
        Map<Integer, List<ComponentField>> componentFieldMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(chooseComponentIds)) {
            List<ComponentField> componentFields = componentFieldMapper.selectList(new QueryWrapper<ComponentField>().lambda().in(ComponentField::getComponentId, chooseComponentIds));
            componentFields.forEach(v -> {
                componentFieldMap.computeIfAbsent(v.getComponentId(), k -> org.apache.commons.compress.utils.Lists.newArrayList());
                componentFieldMap.get(v.getComponentId()).add(v);
            });
        }
        templateComponents.forEach(v -> {
            if (CollectionUtils.isNotEmpty(componentFieldMap.get(v.getComponentId()))) {
                v.setComponentFields(componentFieldMap.get(v.getComponentId()));
            }
            if (StringUtils.isNotBlank(v.getCode()) && Objects.equals(v.getCode(), Component.SystemComponentCodeEnum.SIGN_UP_FILL_INFO.getValue())) {
                v.setSignUpFillInfoType(signUpFillInfoTypeService.getByTemplateComponentId(v.getId()));
            }
            if (StringUtils.isNotBlank(v.getDataOrigin()) && Objects.equals(v.getDataOrigin(), Component.DataOriginEnum.FORM.getValue())) {
                v.setFieldValues(wfwFormApiService.listFormFieldValue(fid, Integer.parseInt(v.getOriginIdentify()), v.getFieldFlag()));
            }
        });
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
                v.getChildren().forEach(v1 -> ApplicationContextHolder.getBean(TemplateComponentService.class).handleSignUpConditionFillInfoType(v1));
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTemplateComponents(List<TemplateComponent> templateComponents) {
        templateComponents.forEach(v -> {
            TemplateComponent tplComponent = new TemplateComponent();
            BeanUtils.copyProperties(v, tplComponent);
            templateComponentMapper.updateById(tplComponent);
            SignUpCondition suc = v.getSignUpCondition();
            SignUpFillInfoType signUpFillInfoType = v.getSignUpFillInfoType();
            if (suc != null) {
                suc.setTemplateComponentId(tplComponent.getId());
                if (suc.getId() == null) {
                    signUpConditionService.add(suc);
                } else {
                    signUpConditionService.updateById(suc);
                }
            }
            if (signUpFillInfoType != null) {
                signUpFillInfoType.setTemplateComponentId(tplComponent.getId());
                if (signUpFillInfoType.getId() == null) {
                    signUpFillInfoTypeService.add(signUpFillInfoType);
                } else {
                    signUpFillInfoTypeService.updateById(signUpFillInfoType);
                }
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
            signUpConditionService.add(templateComponent.getSignUpCondition());
        }
        if (templateComponent.getSignUpFillInfoType() != null) {
            templateComponent.getSignUpFillInfoType().setTemplateComponentId(templateComponent.getId());
            signUpFillInfoTypeService.add(templateComponent.getSignUpFillInfoType());
        }
    }

    /**批量新增模版组件
     * @Description
     * @author wwb
     * @Date 2021-07-14 18:12:06
     * @param templateComponents
     * @return void
     */
    public void batchAddTemplateComponents(List<TemplateComponent> templateComponents) {
        if (CollectionUtils.isNotEmpty(templateComponents)) {
            templateComponentMapper.batchAdd(templateComponents);
            templateComponents.forEach(templateComponent -> {
                Optional.ofNullable(templateComponent.getChildren()).orElse(Lists.newArrayList()).forEach(v -> v.setPid(templateComponent.getId()));
                batchAddTemplateComponents(templateComponent.getChildren());
            });
        }
    }

    /**取消模板组件关联(即删除)
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-26 15:01:26
    * @param delTemplateComponentIds
    * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void cancelTemplateComponent(List<Integer> delTemplateComponentIds) {
        if (CollectionUtils.isEmpty(delTemplateComponentIds)) {
            return;
        }
        templateComponentMapper.update(null, new UpdateWrapper<TemplateComponent>()
                .lambda()
                .in(TemplateComponent::getId, delTemplateComponentIds)
                .set(TemplateComponent::getDeleted, Boolean.TRUE));
    }
}
