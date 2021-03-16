package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动评价表
 * @className: ActivityRating, table_name: t_activity_rating
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-03-08 16:07:57
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_rating")
public class ActivityRating {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 得分; column: score*/
    private BigDecimal score;
    /** 评价次数; column: score_num*/
    private Integer scoreNum;
    /** 总分; column: total_score*/
    private BigDecimal totalScore;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    public static ActivityRating getDefault(Integer activityId) {
        return ActivityRating.builder()
                .activityId(activityId)
                .score(new BigDecimal(0d))
                .scoreNum(0)
                .totalScore(new BigDecimal(0d))
                .build();
    }

}