package com.chaoxing.activity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动模块表
 * @className: ActivityModule, table_name: t_activity_module
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityModule {

    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 模块类型; column: type*/
    private String type;
    /** 模块名称; column: name*/
    private String name;
    /** icon云盘id; column: icon_cloud_id*/
    private String iconCloudId;
    /** 外部id; column: external_id*/
    private String externalId;
    /** pc端地址; column: pc_url*/
    private String pcUrl;
    /** 移动端地址; column: mobile_url*/
    private String mobileUrl;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 外部配置信息; column: external_config*/
    private String externalConfig;

}