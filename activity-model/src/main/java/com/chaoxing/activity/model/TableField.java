package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 表格字段配置表
 * @className: TableField, table_name: t_table_field
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-24 16:02:38
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_table_field")
public class TableField {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 类型。报名管理、签到管理...; column: type*/
    private String type;
    /** 关联的类型。机构、活动; column: associated_type*/
    private String associatedType;
    /** 是否被删除; column: is_deleted*/
    @com.baomidou.mybatisplus.annotation.TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    @Getter
    public enum Type {

        /** 报名名单 */
        SIGN_UP_LIST("报名名单", "sign_up_list"),
        SIGN_IN_LIST("签到名单", "sign_in_list"),
        ACTIVITY_STAT("活动统计", "activity_stat"),
        USER_STAT("用户统计", "user_stat"),
        RESULT_MANAGE("活动考核管理", "activity_inspection_manage"),
        ACTIVITY_MANAGE_LIST("活动管理", "activity_manage_list");

        private final String name;
        private final String value;

        Type(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static Type fromValue(String value) {
            Type[] values = Type.values();
            for (Type type : values) {
                if (Objects.equals(type.getValue(), value)) {
                    return type;
                }
            }
            return null;
        }

    }

    @Getter
    public enum AssociatedType {

        /** 机构 */
        ORG("机构", "org"),
        ACTIVITY("活动", "activity"),
        ACTIVITY_MARKET("活动市场", "activity_market");

        private final String name;
        private final String value;

        AssociatedType(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static AssociatedType fromValue(String value) {
            AssociatedType[] values = AssociatedType.values();
            for (AssociatedType associatedType : values) {
                if (Objects.equals(associatedType.getValue(), value)) {
                    return associatedType;
                }
            }
            return null;
        }

    }

}