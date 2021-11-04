package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模版的报名条件明细表
 * @className: TemplateSignUpCondition, table_name: t_template_sign_up_condition
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-11-02 16:56:56
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_template_sign_up_condition")
public class TemplateSignUpCondition {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 模板组件id; column: template_component_id*/
    private Integer templateComponentId;
    /** 字段名称; column: field_name*/
    private String fieldName;
    /** 条件; column: condition*/
    @TableField(value = "`condition`")
    private String condition;
    /** 值; column: value*/
    @TableField(value = "`value`")
    private String value;
}