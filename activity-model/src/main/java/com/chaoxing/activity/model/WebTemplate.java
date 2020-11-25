package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 网页模板表
 * @className: TWebTemplate, table_name: t_web_template
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-23 19:27:42
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_web_template")
public class WebTemplate {

    /** id; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 模板id; column: template_id*/
    private Integer templateId;
    /** 模板名称; column: name*/
    private String name;
    /** 封面地址; column: cover_url*/
    private String coverUrl;
    /** 创建时间; column: create_time*/
    private Date createTime;

}