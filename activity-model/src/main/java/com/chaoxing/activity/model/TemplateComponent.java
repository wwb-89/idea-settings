package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @TableField(exist = false)
    private List<TemplateComponent> children;
}