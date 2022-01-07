package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动组件值表
 * @className: ActivityComponentValue, table_name: t_activity_component_value
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:25
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_component_value")
public class ActivityComponentValue {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 模版组件id; column: template_component_id*/
    private Integer templateComponentId;
    /** 模版id; column: template_id*/
    private Integer templateId;
    /** 组件id; column: component_id*/
    private Integer componentId;
    /** 值; column: value*/
    private String value;
    /** 云盘id集合; column: cloud_ids */
    private String cloudIds;
}