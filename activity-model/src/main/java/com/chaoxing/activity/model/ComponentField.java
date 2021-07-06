package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 组件选项表（单选、多选）
 * @className: ComponentField, table_name: t_component_field
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:01
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_component_field")
public class ComponentField {

    /** 主键; column: id*/
    private Integer id;
    /** 组件id; column: component_id*/
    private Integer componentId;
    /** 字段名; column: field_name*/
    private String fieldName;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 是否被删除; column: is_deleted*/
    private Boolean isDeleted;
    /** 创建时间; column: create_time*/
    private Date createTime;
    /** 创建人uid; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private Date updateTime;
    /** 更新人uid; column: update_uid*/
    private Integer updateUid;
}