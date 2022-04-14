package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    /** 网站id; column: website_id*/
    private Integer websiteId;
    /** 模板名称; column: name*/
    private String name;
    /** 活动形式; column: activity_type*/
    private String activityType;
    /** 封面地址; column: cover_url*/
    private String coverUrl;
    /** 预览地址; column: preview_url*/
    private String previewUrl;
    /** 引用头部的pageId; column: origin_page_id*/
    private Integer originPageId;
    /** 是否系统模板; column: is_system*/
    @TableField(value = "is_system")
    private Boolean system;
    /** 所属区域编码; column: affiliation_area_code*/
    private String affiliationAreaCode;
    /** 所属机构; column: affiliation_fid*/
    private String affiliationFid;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private Date createTime;

}