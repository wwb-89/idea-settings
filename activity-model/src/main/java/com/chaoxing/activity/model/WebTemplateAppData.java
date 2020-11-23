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
 * 网页模板应用数据
 * @className: TWebTemplateAppData, table_name: t_web_template_app_data
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-23 19:27:42
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_web_template_app_data")
public class WebTemplateAppData {

    /** column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 模板app id; column: web_template_app_id*/
    private Integer webTemplateAppId;
    /** 名称; column: name*/
    private String name;
    /** 数据类型（模块）; column: type*/
    private String type;
    /** 顺序; column: sequence*/
    private Integer sequence;

    // 附加
    /** 模板应用数据列表 */
    private List<WebTemplateAppData> webTemplateAppDataList;

}