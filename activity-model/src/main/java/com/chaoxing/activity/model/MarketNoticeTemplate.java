package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 市场通知模版表
 * @className: MarketNoticeTemplate, table_name: t_market_notice_template
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-11-11 14:07:50
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_market_notice_template")
public class MarketNoticeTemplate {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动市场id; column: market_id*/
    private Integer marketId;
    /** 通知类型; column: notice_type*/
    private String noticeType;
    /** 收件人描述; column: receiver_description*/
    private String receiverDescription;
    /** 标题; column: title*/
    private String title;
    /** 标题（代码使用）; column: code_title*/
    private String codeTitle;
    /** 内容; column: content*/
    private String content;
    /** 内容（代码使用）; column: code_content*/
    private String codeContent;
    /** 发送时间描述; column: send_time_description*/
    private String sendTimeDescription;
    /** 是否支持时间配置; column: is_support_time_config*/
    @TableField(value = "is_support_time_config")
    private Boolean supportTimeConfig;
    /** 延迟小时数; column: delay_hour*/
    private Integer delayHour;
    /** 延迟分钟数; column: delay_minute*/
    private Integer delayMinute;
    /** 是否启用; column: is_enable*/
    @TableField(value = "is_enable")
    private Boolean enable;

}