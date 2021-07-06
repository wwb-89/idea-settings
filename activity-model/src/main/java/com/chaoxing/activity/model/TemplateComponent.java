package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer id;
    /** 模版id; column: template_id*/
    private Integer templateId;
    /** 组件id; column: component_id*/
    private Integer componentId;
    /** 定制的名称; column: name*/
    private String name;
    /** 定制的简介; column: introduction*/
    private String introduction;
    /** 是否必填; column: is_required*/
    private Boolean isRequired;
    /** 顺序; column: sequence*/
    private Integer sequence;

}