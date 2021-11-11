package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Objects;

/**
 * 系统通知模版
 * @className: SystemNoticeTemplate, table_name: t_system_notice_template
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-11-11 14:07:50
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_system_notice_template")
public class SystemNoticeTemplate {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
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
    /** 顺序; column: sequence*/
    private Integer sequence;

    @Getter
    public enum NoticeTypeEnum {

        ACTIVITY_INFO_CHANGE("活动信息变更", "activity_info_change"),
        ACTIVITY_ABOUT_START("活动即将开始", "activity_about_start"),
        ACTIVITY_ABOUT_END("活动即将结束", "activity_about_end"),
        SIGN_UP_ABOUT_START("报名即将开始", "sign_up_about_start");

        private final String name;
        private final String value;

        NoticeTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static NoticeTypeEnum fromValue(String value) {
            NoticeTypeEnum[] values = NoticeTypeEnum.values();
            for (NoticeTypeEnum noticeTypeEnum : values) {
                if (Objects.equals(noticeTypeEnum.getValue(), value)) {
                    return noticeTypeEnum;
                }
            }
            return null;
        }

    }

    @Getter
    public enum NoticeFieldEnum {

        PARTICIPANT("参与人", "user"),
        ACTIVITY_NAME("活动名称", "activity_name"),
        ACTIVITY_ADDRESS("活动地点", "activity_address"),
        ACTIVITY_TIME("活动时间", "activity_time"),
        SIGN_UP_TIME("报名时间", "sign_up_time");

        private final String name;
        private final String value;

        NoticeFieldEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

    }

}