package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 证书发放表
 * @className: CertificateIssue, table_name: t_certificate_issue
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-12-15 15:14:00
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_certificate_issue")
public class CertificateIssue {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 发放用户id; column: uid*/
    private Integer uid;
    /** 证书编号; column: no*/
    private String no;
    /** 序号; column: serial_no*/
    private Integer serialNo;
    /** 发放时间; column: issue_time*/
    private LocalDateTime issueTime;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}