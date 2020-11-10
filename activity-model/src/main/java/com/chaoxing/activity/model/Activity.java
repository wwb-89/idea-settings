package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 活动表
 * @className: Activity, table_name: t_activity
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity")
public class Activity {

    /** 活动id; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动名称; column: name*/
    private String name;
    /** 开始日期; column: start_date*/
    private Date startDate;
    /** 结束日期; column: end_date*/
    private Date endDate;
    /** 封面云盘id; column: cover_cloud_id*/
    private String coverCloudId;
    /** 活动形式; column: activity_form*/
    private String activityForm;
    /** 活动地址; column: address*/
    private String address;
    /** 经度; column: longitude*/
    private BigDecimal longitude;
    /** 维度; column: dimension*/
    private String dimension;
    /** 活动分类id; column: activity_classify_id*/
    private Integer activityClassifyId;
    /** 是否启用签到报名; column: is_enable_sign*/
    @TableField(value = "is_enable_sign")
    private Boolean enableSign;
    /** 签到报名id; column: sign_id*/
    private Integer signId;
    /** 网页模板id; column: web_template_id*/
    private Integer webTemplateId;
    /** 是否已发布; column: is_released*/
    @TableField(value = "is_released")
    private Boolean released;
    /** 发布时间; column: release_time*/
    private Date releaseTime;
    /** 发布人id; column: release_uid*/
    private Integer releaseUid;
    /** 状态。0：已删除，1：待发布，2：进行中，3：已结束; column: status*/
    private Integer status;
    /** 是否开启审核; column: is_open_audit*/
    @TableField(value = "is_open_audit")
    private Boolean openAudit;
    /** 审核状态。0：审核不通过，1：审核通过，2：待审核; column: audit_status*/
    private Integer auditStatus;
    /** 创建人id; column: create_uid*/
    private Integer createUid;
    /** 创建单位id; column: create_fid*/
    private Integer createFid;
    /** 省; column: province_name*/
    private String provinceName;
    /** 市; column: city_name*/
    private String cityName;
    /** 区县; column: area_name*/
    private String areaName;
    /** 创建时间; column: create_time*/
    private Date createTime;
    /** 修改时间; column: update_time*/
    private Date updateTime;

}