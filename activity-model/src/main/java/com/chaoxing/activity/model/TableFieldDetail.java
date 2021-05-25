package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 表格字段详情配置表
 * @className: TableFieldDetail, table_name: t_table_field_detail
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-24 16:02:38
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_table_field_detail")
public class TableFieldDetail {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 表格字段配置id; column: table_field_id*/
    private Integer tableFieldId;
    /** 名称; column: name*/
    private String name;
    /** 编码; column: code*/
    private String code;
    /** 是否默认选中; column: is_default_checked*/
    @TableField(value = "is_default_checked")
    private Boolean defaultChecked;
    /** 是否允许取消选中; column: is_allow_uncheck*/
    @TableField(value = "is_allow_uncheck")
    private Boolean allowUncheck;
    /** 是否默认置顶; column: is_default_top*/
    @TableField(value = "is_default_top")
    private Boolean defaultTop;
    /** 是否允许置顶; column: is_allow_top*/
    @TableField(value = "is_allow_top")
    private Boolean allowTop;
    /** 是否可排序; column: is_sortable*/
    @TableField(value = "is_sortable")
    private Boolean sortable;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}