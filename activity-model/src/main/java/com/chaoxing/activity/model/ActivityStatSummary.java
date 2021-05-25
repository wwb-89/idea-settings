package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动统计汇总表
 * @className: TActivityStatSummary, table_name: t_activity_stat_summary
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-25 10:42:08
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_stat_summary")
public class ActivityStatSummary {

    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 签到数量; column: signed_in_num*/
    private Integer signedInNum;
    /** 签到率; column: sign_in_rate*/
    private BigDecimal signInRate;
    /** 合格数量; column: qualified_num*/
    private Integer qualifiedNum;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}