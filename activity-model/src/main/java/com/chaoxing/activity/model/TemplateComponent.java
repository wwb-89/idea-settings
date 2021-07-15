package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 模版组件关联表
 * @className: TemplateComponent, table_name: t_template_component
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:53:54
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_template_component")
public class TemplateComponent {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 父id; column: pid*/
    private Integer pid;
    /** 模版id; column: template_id*/
    private Integer templateId;
    /** 组件id; column: component_id*/
    private Integer componentId;
    /** 定制的名称; column: name*/
    private String name;
    /** 定制的简介; column: introduction*/
    private String introduction;
    /** 是否必填; column: is_required*/
    @TableField(value = "is_required")
    private Boolean required;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;

    @TableField(exist = false)
    private List<TemplateComponent> children;
    @TableField(exist = false)
    private SignUpCondition signUpCondition;
    @TableField(exist = false)
    private SignUpFillInfoType signUpFillInfoType;
    @TableField(exist = false)
    private Integer originId;

    /**将模版组件列表克隆到指定的模版
     * @Description 子组件将封装到父组件的children中
     * @author wwb
     * @Date 2021-07-14 18:06:08
     * @param templateComponents
     * @param templateId
     * @return java.util.List<com.chaoxing.activity.model.TemplateComponent>
    */
    public static List<TemplateComponent> cloneToNewTemplateId(List<TemplateComponent> templateComponents, Integer templateId) {
        // 复制一个新列表出来
        List<TemplateComponent> clonedTemplateComponents = Lists.newArrayList();
        Optional.ofNullable(templateComponents).orElse(Lists.newArrayList()).forEach(v -> {
            TemplateComponent clonedTemplateComponent = new TemplateComponent();
            BeanUtils.copyProperties(v, clonedTemplateComponent);
            clonedTemplateComponent.setTemplateId(templateId);
            clonedTemplateComponent.setOriginId(v.getId());
            clonedTemplateComponent.setId(null);
            clonedTemplateComponents.add(clonedTemplateComponent);
        });
        // 找到父组件列表
        List<TemplateComponent> parentTemplateComponents = Optional.ofNullable(clonedTemplateComponents).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(0, v.getPid())).collect(Collectors.toList());
        // 给父组件填充子组件列表
        Optional.ofNullable(parentTemplateComponents).orElse(Lists.newArrayList()).stream().forEach(templateComponent -> templateComponent.setChildren(Optional.ofNullable(clonedTemplateComponents).orElse(Lists.newArrayList()).stream().filter(v -> Objects.equals(templateComponent.getOriginId(), v.getPid())).collect(Collectors.toList())));
        return parentTemplateComponents;
    }

}