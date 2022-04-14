package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 活动奖项表
 * @className: TAward, table_name: t_award
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-29 21:12:39
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_award")
public class Award {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 奖项名称; column: name*/
    private String name;
    /** 奖项级别; column: level*/
    private String level;
    /** 说明; column: description*/
    private String description;
    /** 是否发布; column: is_released*/
    @TableField(value = "is_released")
    private Boolean released;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    @Getter
    public enum LevelEnum {

        /** 一等奖 */
        FIRST_PRIZE("一等奖", "first_prize"),
        SECOND_PRIZE("二等奖", "second_prize"),
        THIRD_PRIZE("三等奖", "third_prize"),
        OTHER_PRIZE("其他奖", "other_prize");

        private final String name;
        private final String value;

        LevelEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static LevelEnum fromValue(String value) {
            LevelEnum[] values = LevelEnum.values();
            for (LevelEnum levelEnum : values) {
                if (Objects.equals(levelEnum.getValue(), value)) {
                    return levelEnum;
                }
            }
            return null;
        }

    }

}