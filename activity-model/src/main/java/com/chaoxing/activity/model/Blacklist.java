package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 黑名单表
 * @className: Blacklist, table_name: t_blacklist
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-26 18:45:24
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_blacklist")
public class Blacklist {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 姓名; column: user_name*/
    private String userName;
    /** 账号; column: account*/
    private String account;
    /** 违约次数; column: default_num*/
    private Integer defaultNum;
    /** 加入方式; column: join_type*/
    private String joinType;
    /** 有效小时数; column: effective_hours*/
    private Integer effectiveHours;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    @Getter
    public enum JoinTypeEnum {

        /** 自动 */
        AUTO("自动", "auto"),
        MANUAL("手动", "manual");

        private final String name;
        private final String value;

        JoinTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static JoinTypeEnum fromValue(String value) {
            JoinTypeEnum[] values = JoinTypeEnum.values();
            for (JoinTypeEnum joinTypeEnum : values) {
                if (Objects.equals(joinTypeEnum.getValue(), value)) {
                    return joinTypeEnum;
                }
            }
            return null;
        }

    }

}