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
 * 表单字段-选项表
 * @className: FormFieldOption, table_name: t_form_field_option
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-12-11 20:39:46
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_form_field_option")
public class FormFieldOption {

    /** id; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 表单字段id; column: field_id*/
    private Integer fieldId;
    /** 名称; column: name*/
    private String name;
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