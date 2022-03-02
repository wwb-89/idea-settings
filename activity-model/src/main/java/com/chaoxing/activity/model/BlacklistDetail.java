package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.dto.stat.UserNotSignedInNumStatDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 黑名单违约记录明细
 * @className: BlacklistDetail, table_name: t_blacklist_detail
 * @Description: 
 * @author: mybatis generator
 * @date: 2022-03-01 11:45:24
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_blacklist_record")
public class BlacklistDetail {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 姓名; column: user_name*/
    private String userName;
    /** 账号; column: account*/
    private String account;
    /** 违约信息; column: breach_content */
    private String breachContent;
    /** 违约时间; column: create_time*/
    private LocalDateTime createTime;

    /** 活动名称 */
    @TableField(exist = false)
    private String activityName;

}