package com.chaoxing.activity.dto.engine;

import com.chaoxing.activity.model.ComponentField;
import com.chaoxing.activity.model.SignUpCondition;
import com.chaoxing.activity.model.SignUpFillInfoType;
import com.chaoxing.activity.model.TemplateCustomAppConfig;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/7/15 2:38 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateComponentDTO {

    private Integer id;

    private Integer pid;

    private Integer templateId;

    private Integer componentId;

    private String name;

    private String introduction;

    private Boolean required;

    private Integer sequence;

    private String code;

    private String type;

    private String dataOrigin;

    private String originIdentify;

    private String fieldFlag;

    private List<TemplateComponentDTO> children;

    /** 选择组件自定义选项值列表 */
    private List<ComponentField> componentFields;
    /** 选择组件表单选项值列表 */
    private List<String> fieldValues;
    /** 报名条件配置 */
    private SignUpCondition signUpCondition;
    /** 报名信息提报配置 */
    private SignUpFillInfoType signUpFillInfoType;
    /** 自定义应用配置列表 */
    private List<TemplateCustomAppConfig> customAppConfigs;
    /** 被删除的自定义应用配置ids */
    private List<Integer> removeCustomAppConfigIds;

    private Integer originId;


    /**将模版组件列表克隆到指定的模版
     * @Description 子组件将封装到父组件的children中
     * @author wwb
     * @Date 2021-07-14 18:06:08
     * @param templateComponents
     * @param templateId
     * @return java.util.List<com.chaoxing.activity.model.TemplateComponent>
     */
    public static List<TemplateComponentDTO> cloneToNewTemplateId(List<TemplateComponentDTO> templateComponents, Integer templateId) {
        // 复制一个新列表出来
        List<TemplateComponentDTO> clonedTemplateComponents = Lists.newArrayList();
        Optional.ofNullable(templateComponents).orElse(Lists.newArrayList()).forEach(v -> {
            TemplateComponentDTO clonedTemplateComponent = new TemplateComponentDTO();
            BeanUtils.copyProperties(v, clonedTemplateComponent);
            clonedTemplateComponent.setTemplateId(templateId);
            clonedTemplateComponent.setOriginId(v.getId());
            clonedTemplateComponent.setId(null);
            clonedTemplateComponents.add(clonedTemplateComponent);
        });
        // 找到父组件列表
        List<TemplateComponentDTO> parentTemplateComponents = Optional.ofNullable(clonedTemplateComponents).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(0, v.getPid())).collect(Collectors.toList());
        // 给父组件填充子组件列表
        Optional.ofNullable(parentTemplateComponents).orElse(Lists.newArrayList()).stream().forEach(templateComponent -> templateComponent.setChildren(Optional.ofNullable(clonedTemplateComponents).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(templateComponent.getOriginId(), v.getPid())).collect(Collectors.toList())));
        return parentTemplateComponents;
    }


    /**构建父子关系的树结构
    * @Description
    * @author huxiaolong
    * @Date 2021-08-04 17:13:12
    * @param templateComponents
    * @return java.util.List<com.chaoxing.activity.dto.engine.TemplateComponentDTO>
    */
    public static List<TemplateComponentDTO> buildTrees(List<TemplateComponentDTO> templateComponents) {
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
