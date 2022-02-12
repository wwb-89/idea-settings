package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 通知记录表
 * @className: NoticeRecord, table_name: t_notice_record
 * @Description: 
 * @author: mybatis generator
 * @date: 2022-02-11 17:01:23
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_notice_record")
public class NoticeRecord {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 通知类型; column: type*/
    private String type;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 活动创建机构id; column: activity_create_fid*/
    private Integer activityCreateFid;
    /** 活动标识; column: activity_flag*/
    private String activityFlag;
    /** 通知时间; column: time*/
    private LocalDateTime time;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 通知内容; column: content*/
    private String content;

    /** 通知记录枚举
     * @className NoticeRecord
     * @description 
     * @author wwb
     * @blame wwb
     * @date 2022-02-11 17:17:28
     * @version ver 1.0
     */
    @Getter
    public enum TypeEnum {

        /** 活动发布 */
        ACTIVITY_RELEASE("活动发布", "activity_release"),
        SIGN_UP("报名", "sign_up");

        private final String name;
        private final String value;

        TypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

    }

}