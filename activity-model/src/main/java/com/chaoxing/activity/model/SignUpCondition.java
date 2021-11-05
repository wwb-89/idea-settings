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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 模版组件报名条件表
 * @className: SignUpCondition, table_name: t_sign_up_condition
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:18
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_sign_up_condition")
public class SignUpCondition {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 机构id; column: fid*/
    private Integer fid;
    /** 模版组件关联id; column: template_component_id*/
    private Integer templateComponentId;
    /** 来源主键标识; column: origin_identify*/
    private String originIdentify;
    /** 字段名; column: field_name*/
    private String fieldName;
    /** 是否允许报名（记录存在的时候）; column: is_allow_signed_up*/
    @TableField(value = "is_allow_signed_up")
    private Boolean allowSignedUp;
    /** 是否在活动发布时配置; column: is_config_on_activity*/
    @TableField(value = "is_config_on_activity")
    private Boolean configOnActivity;

    /** 模板条件明细 */
    @TableField(exist = false)
    private List<TemplateSignUpCondition> templateConditionDetails;
    @TableField(exist = false)
    private List<ActivitySignUpCondition> activityConditionDetails;

    public SignUpCondition cloneToNewTemplateComponentId(Integer templateComponentId) {
        return SignUpCondition.builder()
                .templateComponentId(templateComponentId)
                .originIdentify(getOriginIdentify())
                .fieldName(getFieldName())
                .allowSignedUp(getAllowSignedUp())
                .build();
    }

    public static List<SignUpCondition> cloneToNewTemplateComponentId(List<SignUpCondition> signUpConditions, Map<Integer, Integer> oldNewTemplateComponentIdRelation) {
        return Optional.ofNullable(signUpConditions).orElse(Lists.newArrayList()).stream().map(v -> v.cloneToNewTemplateComponentId(oldNewTemplateComponentIdRelation.get(v.getId()))).collect(Collectors.toList());
    }

}