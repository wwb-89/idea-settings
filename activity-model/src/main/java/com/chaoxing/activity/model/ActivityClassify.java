package com.chaoxing.activity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 活动类型表
 * @className: ActivityClassify, table_name: t_activity_classify
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityClassify {

    /** column: id*/
    private Integer id;
    /** 名称; column: name*/
    private String name;
    /** 所属机构id; column: affiliation_fid*/
    private Integer affiliationFid;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 状态。0：无效，1：有效; column: status*/
    private Integer status;
    /** 创建时间; column: create_time*/
    private Date createTime;
    /** 更新时间; column: update_time*/
    private Date updateTime;

}