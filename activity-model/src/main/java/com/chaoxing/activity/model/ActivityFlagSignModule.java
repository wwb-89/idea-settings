package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 活动标示报名签到模块
 * @className: ActivityFlagSignModule, table_name: t_activity_flag_sign_module
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-03-29 15:43:25
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_flag_sign_module")
public class ActivityFlagSignModule {

    /** 活动标示; column: activity_flag*/
    private String activityFlag;
    /** 模块类型; column: module_type*/
    private String moduleType;
    /** 模块名称; column: module_name*/
    private String moduleName;
    /** 是否启用限制参与范围; column: is_enable_limit_participate_scope*/
    @TableField(value = "is_enable_limit_participate_scope")
    private Boolean enableLimitParticipateScope;
    /** 限制参与范围类型; column: limit_participate_scope_type*/
    private String limitParticipateScopeType;
    /** 定制报名类型; column: custom_sign_up_type*/
    private String customSignUpType;
    /** 按钮名称; column: btn_name*/
    private String btnName;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;

    /** 模块类型枚举
     * @className ActivitySignModule
     * @description
     * @author wwb
     * @blame wwb
     * @date 2021-03-29 15:27:03
     * @version ver 1.0
     */
    @Getter
    public enum ModuleType {

        /** 报名 */
        SIGN_UP("报名", "sign_up"),
        SIGN_IN("签到", "sign_in"),
        SIGN_OUT("签退", "sign_out");

        private String name;
        private String value;

        ModuleType(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static ModuleType fromValue(String value) {
            ModuleType[] values = ModuleType.values();
            for (ModuleType moduleType : values) {
                if (Objects.equals(moduleType.getValue(), value)) {
                    return moduleType;
                }
            }
            return null;
        }
    }

    /** 限制参与范围类型枚举
     * @className SignUp
     * @description
     * @author wwb
     * @blame wwb
     * @date 2021-03-29 15:46:32
     * @version ver 1.0
     */
    @Getter
    public enum LimitParticipateScopeType {

        /** 微服务组织架构 */
        WFW_ORGANIZATIONAL_STRUCTURE("微服务组织架构", "wfw_organizational_structure");

        private String name;
        private String value;

        LimitParticipateScopeType(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static LimitParticipateScopeType fromValue(String value) {
            LimitParticipateScopeType[] values = LimitParticipateScopeType.values();
            for (LimitParticipateScopeType limitParticipateScopeType : values) {
                if (Objects.equals(limitParticipateScopeType.getValue(), value)) {
                    return limitParticipateScopeType;
                }
            }
            return null;
        }
    }

}