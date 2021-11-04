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
import java.util.stream.Collectors;

/**
 * 活动报名条件明细表
 * @className: ActivitySignUpCondition, table_name: t_activity_sign_up_condition
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-11-02 16:57:22
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_sign_up_condition")
public class ActivitySignUpCondition {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 模版组件id; column: template_component_id*/
    private Integer templateComponentId;
    /** 字段名称; column: field_name*/
    private String fieldName;
    /** 条件; column: condition*/
    @TableField(value = "`condition`")
    private String condition;
    /** 值; column: value*/
    @TableField(value = "`value`")
    private String value;

    public static ActivitySignUpCondition buildFromTemplateSignUpCondition(TemplateSignUpCondition signUpCondition) {
        return ActivitySignUpCondition.builder()
                .condition("")
                .value("")
                .templateComponentId(signUpCondition.getTemplateComponentId())
                .fieldName(signUpCondition.getFieldName()).build();
    }

    public static List<ActivitySignUpCondition> buildFromTemplateSignUpConditions(List<TemplateSignUpCondition> signUpConditions) {
        return signUpConditions.stream().map(ActivitySignUpCondition::buildFromTemplateSignUpCondition).collect(Collectors.toList());
    }

}