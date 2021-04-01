package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动报名签到模块列表
 * @className: ActivitySignModule, table_name: t_activity_sign_module
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-03-30 17:09:58
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_sign_module")
public class ActivitySignModule {

    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 模块类型。报名、签到、签退; column: module_type*/
    private String moduleType;
    /** 模块id; column: module_id*/
    private Integer moduleId;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;

}