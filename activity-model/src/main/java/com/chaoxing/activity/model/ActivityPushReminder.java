package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.dto.manager.sign.SignUpParticipateScopeDTO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动推送提醒
 * @className: ActivityPushReminder, table_name: t_activity_push_reminder
 * @Description:
 * @author: mybatis generator
 * @date: 2021-12-21 16:33:22
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_push_reminder")
public class ActivityPushReminder {

    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id */
    private Integer activityId;
    /** 推送提醒接收范围 */
    private String receiveScope;
    /** 通知提醒内容 */
    private String content;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<SignUpParticipateScopeDTO> receiveScopes;

    public static ActivityPushReminder buildDefault(Integer activityId) {
        return ActivityPushReminder.builder()
                .activityId(activityId)
                .content("")
                .receiveScope("")
                .receiveScopes(Lists.newArrayList())
                .build();
    }
}
