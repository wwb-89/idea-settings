package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 模版表
 * @className: Template, table_name: t_template
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:53:01
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_template")
public class Template {

    /** 主键; column: id*/
    private Integer id;
    /** 模版名称; column: name*/
    private String name;
    /** 是否是系统模版; column: is_system*/
    private Boolean isSystem;
    /** 所属机构id; column: fid*/
    private Integer fid;
    /** 封面url; column: cover_url*/
    private String coverUrl;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 创建时间; column: create_time*/
    private Date createTime;
    /** 创建人uid; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private Date updateTime;
    /** 更新人uid; column: update_uid*/
    private Integer updateUid;

}