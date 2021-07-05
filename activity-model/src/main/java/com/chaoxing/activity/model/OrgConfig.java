package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 机构配置表
 * @className: OrgConfig, table_name: t_org_config
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-17 10:47:08
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_org_config")
public class OrgConfig {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** fid; column: fid*/
    private Integer fid;
    /** 报名范围类型。微服务/通讯录; column: sign_up_scope_type*/
    private String signUpScopeType;
    /** 时长申述url; column: time_length_appeal_url*/
    private String timeLengthAppealUrl;
    /** 时长申述url; column: credit_appeal_url*/
    private String creditAppealUrl;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    @Getter
    public enum SignUpScopeType {

        /** 微服务 */
        WFW("微服务", "wfw"),
        CONTACTS("通讯录", "contacts");

        private String name;
        private String value;

        SignUpScopeType(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static SignUpScopeType fromValue(String value) {
            SignUpScopeType[] values = SignUpScopeType.values();
            for (SignUpScopeType signUpScopeType : values) {
                if (Objects.equals(signUpScopeType.getValue(), value)) {
                    return signUpScopeType;
                }
            }
            return null;
        }

    }

}