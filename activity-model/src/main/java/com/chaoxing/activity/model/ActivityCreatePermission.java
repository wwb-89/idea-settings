package com.chaoxing.activity.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 活动创建权限
 * @className: ActivityCreatePermission, table_name: t_activity_create_permission
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-01 18:33:22
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_create_permission")
public class ActivityCreatePermission {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 所属机构id; column: fid*/
    private Integer fid;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 角色id; column: role_id*/
    private Integer roleId;
    /** 是否全选; column: is_all_activity_classify*/
    @TableField(value = "is_all_activity_classify")
    private Boolean allActivityClassify;
    /** 活动类型范围，以","分割的活动类型id列表; column: activity_classify_scope*/
    private String activityClassifyScope;
    /** 报名范围类型; column: wfw_sign_up_scope_type*/
    private Integer wfwSignUpScopeType;
    /** 报名范围类型; column: contacts_sign_up_scope_type*/
    private Integer contactsSignUpScopeType;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    @JSONField(serialize = false)
    private LocalDateTime createTime;
    /** 创建人id; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    @JSONField(serialize = false)
    private LocalDateTime updateTime;
    /** 更新人id; column: update_uid*/
    private Integer updateUid;
    /** 报名范围。以","分割的id列表; column: wfw_sign_up_scope*/
    private String wfwSignUpScope;
    /** 报名范围。以","分割的id列表; column: contacts_sign_up_scope*/
    private String contactsSignUpScope;

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-06-02 10:15:04
    */
    @Getter
    public enum SignUpScopeType {

        /** 不限 */
        NO_LIMIT("不限", 1),
        COMPETENT_RANGE("作为主管的范围", 2),
        CUSTOM("自定义", 3);

        private String name;
        private Integer value;

        SignUpScopeType(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static SignUpScopeType fromValue(Integer value) {
            SignUpScopeType[] values = SignUpScopeType.values();
            for (SignUpScopeType type : values) {
                if (Objects.equals(type.getValue(), value)) {
                    return type;
                }
            }
            return null;
        }
    }

    public static ActivityCreatePermission buildActivityCreatePermission(ActivityCreatePermission permission) {
        return ActivityCreatePermission.builder()
                .fid(permission.getFid())
                .marketId(permission.getMarketId())
                .allActivityClassify(permission.getAllActivityClassify())
                .activityClassifyScope(permission.getActivityClassifyScope())
                .wfwSignUpScope(permission.getWfwSignUpScope())
                .wfwSignUpScopeType(permission.getWfwSignUpScopeType())
                .contactsSignUpScope(permission.getContactsSignUpScope())
                .contactsSignUpScopeType(permission.getContactsSignUpScopeType())
                .build();
    }
}