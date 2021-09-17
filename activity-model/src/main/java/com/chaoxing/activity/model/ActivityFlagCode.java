package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动标识区域code关联表
 * @className: ActivityFlagCode, table_name: t_activity_flag_code
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-09-17 10:50:23
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_flag_code")
public class ActivityFlagCode {

    /** 活动标识; column: activity_flag*/
    private String activityFlag;
    /** 区域code; column: area_code*/
    private String areaCode;
}