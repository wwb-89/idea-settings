package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

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
@TableName(value = "t_activity_create_permission")
public class DataPushFormConfig {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 配置id; column: config_id*/
    private Integer configId;
    /** 活动市场id; column: market_id*/
    private Integer marketId;
    /** 表单字段名称; column: form_field_label*/
    private String formFieldLabel;
    /** 数据字段; column: data_field*/
    private String dataField;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;

}