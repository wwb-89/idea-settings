package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 表格字段配置表
 * @className: TableField, table_name: t_table_field
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-24 16:02:38
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_table_field")
public class TableField {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 类型。报名管理、签到管理...; column: type*/
    private String type;
    /** 关联的类型。机构、活动; column: associated_type*/
    private String associatedType;
    /** 是否被删除; column: is_deleted*/
    @com.baomidou.mybatisplus.annotation.TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}