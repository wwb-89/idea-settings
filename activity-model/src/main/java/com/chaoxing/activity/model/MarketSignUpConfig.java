package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动市场报名配置表
 * @className: MarketSignUpConfig, table_name: t_market_sign_up_config
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-11-17 14:43:48
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_market_sign_up_config")
public class MarketSignUpConfig {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动市场id; column: market_id*/
    private Integer marketId;
    /** 同时报名的活动上限; column: sign_up_activity_limit*/
    private Integer signUpActivityLimit;
    /** 报名按钮名称; column: sign_up_btn_name*/
    private String signUpBtnName;
    /** 报名关键字; column: sign_up_keyword*/
    private String signUpKeyword;

}