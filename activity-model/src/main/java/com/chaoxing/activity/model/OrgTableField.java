package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 机构表格字段关联表
 * @className: OrgTableField, table_name: t_org_table_field
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-24 16:02:38
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_org_table_field")
public class OrgTableField {

    /** 机构id; column: fid*/
    private Integer fid;
    /** 表格字段配置id; column: table_field_id*/
    private Integer tableFieldId;
    /** 表格字段详细配置id; column: table_field_detail_id*/
    private Integer tableFieldDetailId;
    /** 是否置顶; column: is_top*/
    @TableField(value = "is_top")
    private Boolean top;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人uid; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人uid; column: update_uid*/
    private Integer updateUid;

}