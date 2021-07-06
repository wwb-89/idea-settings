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
    /** 模版主键id; column: template_component_id*/
    private Integer templateComponentId;
    /** 来源主键标识; column: origin_identify*/
    private String originIdentify;
    /** 字段名; column: filed_name*/
    private String filedName;
    /** 是否允许报名; column: is_allow_signed_up*/
    @TableField(value = "is_allow_signed_up")
    private Boolean allowSignedUp;

}