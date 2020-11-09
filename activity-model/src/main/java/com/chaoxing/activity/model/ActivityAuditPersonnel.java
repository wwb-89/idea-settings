package com.chaoxing.activity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动审核人员配置
 * @className: ActivityAuditPersonnel, table_name: t_activity_audit_personnel
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAuditPersonnel {

    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 审核人员id; column: personnel_uid*/
    private Integer personnelUid;
    /** 审核人员姓名; column: personnel_real_name*/
    private String personnelRealName;

}