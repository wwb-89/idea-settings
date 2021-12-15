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

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 用户uid; column: uid*/
    private Integer uid;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 登录名; column: uname*/
    private String uname;
    /** 真实姓名; column: real_name*/
    private String realName;
    /** 手机号; column: mobile*/
    private String mobile;
    /** 学号; column: student_no*/
    private String studentNo;
    /** 组织架构; column: organization_structure*/
    private String organizationStructure;
    /** 报名数量; column: sign_up_num*/
    private Integer signUpNum;
    /** 报名成功数量; column: signed_up_num*/
    private Integer signedUpNum;
    /** 报名时间; column: sign_up_time*/
    private LocalDateTime signUpTime;
    /** 签到数量; column: sign_in_num*/
    private Integer signInNum;
    /** 签到成功数量; column: signed_in_num*/
    private Integer signedInNum;
    /** 签到请假次数; column: sign_in_leave_num*/
    private Integer signInLeaveNum;
    /** 未签到次数; column: not_sign_in_num*/
    private Integer notSignInNum;
    /** 签到率; column: signed_in_rate*/
    private BigDecimal signedInRate;
    /** 评价数量; column: rating_num*/
    private Integer ratingNum;
    /** 获得的积分; column: integral*/
    private BigDecimal integral;
    /** 活动积分; column: activity_integral*/
    private BigDecimal activityIntegral;
    /** 参与时长; column: participate_time_length*/
    private Integer participateTimeLength;
    /** 是否合格; column: is_qualified*/
    @TableField(value = "is_qualified")
    private Boolean qualified;
    /** 是否可用; column: is_valid*/
    @TableField(value = "is_valid")
    private Boolean valid;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    
    // 附加
    /** 合格数量（汇总多个活动时使用） */
    @TableField(exist = false)
    private Integer qualifiedNum;
    /** 参与活动数量 */
    @TableField(exist = false)
    private Integer participateActivityNum;
    /** 总得分 */
    @TableField(exist = false)
    private BigDecimal totalScore;
    
}