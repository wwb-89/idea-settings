package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.dto.notice.NoticeTemplateFieldDTO;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    /** 是否删除; column: is_deleted */
    private Boolean deleted;

    @Getter
    public enum NoticeTypeEnum {

        /** 活动信息变更 */
        ACTIVITY_INFO_CHANGE("活动信息变更", "activity_info_change"),
        ACTIVITY_ABOUT_START("活动即将开始", "activity_about_start"),
        ACTIVITY_ABOUT_END("活动即将结束", "activity_about_end"),
        SIGN_UP_SUCCESS("成功报名活动", "sign_up_success"),
        SIGN_UP_ABOUT_START("报名即将开始", "sign_up_about_start"),
        SIGN_UP_ABOUT_END("报名即将结束", "sign_up_about_end"),
        AUTO_ADD_TO_BLACKLIST("自动进入黑名单", "auto_add_to_blacklist"),
        MANUAL_ADD_TO_BLACKLIST("手动进入黑名单", "manual_add_to_blacklist"),
        REMOVE_FROM_BLACKLIST("移出黑名单", "remove_from_blacklist");

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

        /** 参与热 */
//        PARTICIPANT("参与人", "user"),
        ACTIVITY_NAME("活动名称", "activity_name"),
        ACTIVITY_ADDRESS("活动地点", "activity_address"),
        ACTIVITY_TIME("活动时间", "activity_time"),
        SIGN_UP_TIME("报名时间", "sign_up_time"),
        ACTIVITY_ORGANISERS("活动主办方", "activity_organisers"),
        BLACKLIST_AUTO_REMOVE_HOURS("黑名单自动移除", "blacklist_auto_remove_hours"),
        BLACKLIST_ADD_TIME("黑名单进入时间", "blacklist_add_time"),
        BLACKLIST_REMOVE_TIME("黑名单移出时间", "blacklist_remove_time");

        private final String name;
        private final String value;

        NoticeFieldEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        /**
         * 转换通知字段
         * @param waitConvertStr
         * @param noticeTemplateField
         * @return
         */
        public static String convertNoticeField(String waitConvertStr, NoticeTemplateFieldDTO noticeTemplateField) {
            waitConvertStr = Optional.ofNullable(waitConvertStr).orElse("");
            String activityName = Optional.ofNullable(noticeTemplateField.getActivityName()).orElse("");
            String address = Optional.ofNullable(noticeTemplateField.getAddress()).orElse("");
            String activityTime = Optional.ofNullable(noticeTemplateField.getActivityTime()).orElse("");
            String activityOrganisers = Optional.ofNullable(noticeTemplateField.getActivityOrganisers()).orElse("");
            String blacklistAddTime = Optional.ofNullable(noticeTemplateField.getBlacklistAddTime()).orElse("");
            String blacklistRemoveTime = Optional.ofNullable(noticeTemplateField.getBlacklistRemoveTime()).orElse("");
            String autoRemoveHours = Optional.ofNullable(noticeTemplateField.getAutoRemoveHours()).map(String::valueOf).orElse("");
            StringBuilder signUpTime = new StringBuilder();
            List<NoticeTemplateFieldDTO.SignUpNoticeTemplateFieldDTO> signUps = noticeTemplateField.getSignUps();
            if (CollectionUtils.isNotEmpty(signUps)) {
                int signUpSize = signUps.size();
                if (signUpSize > 1) {
                    for (int i = 0; i < signUpSize; i++) {
                        NoticeTemplateFieldDTO.SignUpNoticeTemplateFieldDTO signUp = signUps.get(i);
                        boolean last = (i == signUpSize - 1);
                        signUpTime.append(signUp.getName());
                        signUpTime.append("：");
                        signUpTime.append(signUp.getTime());
                        signUpTime.append(last ? "" : CommonConstant.NEW_LINE_CHAR);
                    }
                } else {
                    signUpTime.append(signUps.get(0).getTime());
                }
            }
            for (NoticeFieldEnum fieldEnum : NoticeFieldEnum.values()) {
                String noticeField = "{" + fieldEnum.getValue() + "}";
                String regNoticeField = "\\" + noticeField;
                String value = "";
                switch (fieldEnum) {
                    case ACTIVITY_NAME:
                        value = activityName;
                        break;
                    case ACTIVITY_ADDRESS:
                        value = address;
                        break;
                    case ACTIVITY_TIME:
                        value = activityTime;
                        break;
                    case SIGN_UP_TIME:
                        value = signUpTime.toString();
                        break;
                    case ACTIVITY_ORGANISERS:
                        value = activityOrganisers;
                        break;
                    case BLACKLIST_ADD_TIME:
                        value = blacklistAddTime;
                        break;
                    case BLACKLIST_REMOVE_TIME:
                        value = blacklistRemoveTime;
                        break;
                    case BLACKLIST_AUTO_REMOVE_HOURS:
                        value = autoRemoveHours;
                    default:
                        break;
                }
                waitConvertStr = replacePlaceholder(waitConvertStr, noticeField, regNoticeField, value);
            }
            return waitConvertStr;
        }

        private static String replacePlaceholder(String waitConvertStr, String key, String regKey, String value) {
            String[] lines = waitConvertStr.split(CommonConstant.NEW_LINE_CHAR);
            StringBuilder result = new StringBuilder();
            int length = lines.length;
            for (int i = 0; i < length; i++) {
                String line = lines[i];
                boolean lastLine = (i == length - 1);
                if (line.contains(key)) {
                    if (StringUtils.isNotBlank(value)) {
                        boolean containNewLine = value.contains(CommonConstant.NEW_LINE_CHAR);
                        if (containNewLine) {
                            result.append(line.replaceAll(regKey, ""));
                            result.append(CommonConstant.NEW_LINE_CHAR);
                            result.append(value);
                        } else {
                            result.append(line.replaceAll(regKey, value));
                        }
                    } else {
                        // 没有值那么忽略该行
                        continue;
                    }
                } else {
                    result.append(line);
                }
                result.append(lastLine ? "" : CommonConstant.NEW_LINE_CHAR);
            }
            return result.toString();
        }

    }

}