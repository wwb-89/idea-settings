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
 * 活动市场数据推送表单配置
 * @className: DataPushFormConfig, table_name: t_activity_create_permission
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-10-29 14:25:53
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_data_push_form_config")
public class DataPushFormConfig {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 配置id; column: config_id*/
    private Integer configId;
    /** 活动市场id; column: market_id*/
    private Integer marketId;
    /** 是否是自定义字段; column: is_custom_field*/
    @TableField(value = "is_custom_field")
    private Boolean customField;
    /** 表单字段名称; column: form_field_label*/
    private String formFieldLabel;
    /** 表单字段别名; column: form_field_alias*/
    private String formFieldAlias;
    /** 数据字段; column: data_field*/
    private String dataField;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;

}