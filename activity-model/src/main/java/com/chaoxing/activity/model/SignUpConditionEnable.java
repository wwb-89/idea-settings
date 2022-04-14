package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报名条件启用表
 * @className: TSignUpConditionEnable, table_name: t_sign_up_condition_enable
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-21 18:38:35
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_sign_up_condition_enable")
public class SignUpConditionEnable {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 报名条件模版组件id; column: template_component_id*/
    private Integer templateComponentId;

}