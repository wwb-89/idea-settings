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
 * @description:
 * @author: huxiaolong
 * @date: 2022/2/28 11:57 上午
 * @version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_template_push_reminder_config")
public class TemplatePushReminderConfig {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 模板组件id; column: template_component_id*/
    private Integer templateComponentId;
    /** 是否手动选择收件人(0: 否，1: 是); column: is_remind_within_role_scope */
    @TableField(value = "is_remind_within_role_scope")
    private Boolean remindWithinRoleScope;


}
