package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @className: ActivityStat, table_name: t_activity_stat
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-10 16:08:51
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_stat")
public class ActivityStat {

    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 统计日期; column: stat_date*/
    private LocalDate statDate;
    /** 浏览量; column: pv*/
    private Integer pv;
    /** 报名人数; column: signed_up_num*/
    private Integer signedUpNum;
    /** 签到人数; column: signed_in_num*/
    private Integer signedInNum;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;

    /** 活动名称*/
    @TableField(exist = false)
    private String activityName;
    /** 序号*/
    @TableField(exist = false)
    private Integer rank;

}