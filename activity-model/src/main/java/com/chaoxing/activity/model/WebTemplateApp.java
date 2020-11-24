package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网页模板app关联表
 * @className: TWebTemplateApp, table_name: t_web_template_app
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-23 19:27:42
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_web_template_app")
public class WebTemplateApp {

    /** column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 网页模板id; column: web_template_id*/
    private Integer webTemplateId;
    /** 应用id; column: app_id*/
    private Integer appId;
    /** 应用名称; column: app_name*/
    private Integer appName;
    /** 应用类型; column: app_type*/
    private String appType;
    /** 数据类型，封面、签到报名、地图、活动列表等; column: data_type*/
    private String dataType;
    /** 数据源类型; column: data_source_type*/
    private Integer dataSourceType;

    // 附加
    /** 模板应用数据列表 */
    private List<WebTemplateAppData> webTemplateAppDataList;

}