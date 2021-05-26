package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户统计汇总表
 * @className: TUserStatSummary, table_name: t_user_stat_summary
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-25 10:42:08
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_user_stat_summary")
public class UserStatSummary {

    /** 用户uid; column: uid*/
    private Integer uid;
    /** 登录名; column: uname*/
    private String uname;
    /** 手机号; column: mobile*/
    private String mobile;
    /** 参与的活动数; column: participate_activity_num*/
    private Integer participateActivityNum;
    /** 签到数量; column: signed_in_num*/
    private Integer signedInNum;
    /** 签到率; column: sign_in_rate*/
    private BigDecimal signInRate;
    /** 评价数量; column: rating_num*/
    private Integer ratingNum;
    /** 合格的数量; column: qualified_num*/
    private Integer qualifiedNum;
    /** 总参与时长; column: total_participate_in_length*/
    private Integer totalParticipateInLength;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}