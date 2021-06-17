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
import java.util.Date;

/**
 * 用户行为详情表
 * @className: UserActionDetail, table_name: t_user_action_detail
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-16 11:01:23
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_user_action_detail")
public class UserActionDetail {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 配置id; column: config_id*/
    private Integer configId;
    /** 行为类型; column: action_type*/
    private String actionType;
    /** 具体行为; column: action*/
    private String action;
    /** 行为标识。行为id等; column: action_identify*/
    private String actionIdentify;
    /** 行为描述; column: action_description*/
    private String actionDescription;
    /** 得分; column: score*/
    private BigDecimal score;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}