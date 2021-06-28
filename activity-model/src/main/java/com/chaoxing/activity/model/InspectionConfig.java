package com.chaoxing.activity.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 考核配置
 * @className: InspectionConfig, table_name: t_inspection_config
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-16 11:01:23
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_inspection_config")
public class InspectionConfig {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 合格判定方式; column: pass_decide_way*/
    private String passDecideWay;
    /** 判定值; column: decide_value*/
    private BigDecimal decideValue;
    /** 创建时间; column: create_time*/
    @JSONField(serialize = false, deserialize = false)
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    @JSONField(serialize = false, deserialize = false)
    private LocalDateTime updateTime;

    @Getter
    public enum PassDecideWayEnum {

        /** 手动 */
        MANUAL("手动评审", "manual"),
        PLACES("按名额", "places"),
        SCALE("按比例", "scale"),
        SCORE("达到积分", "score");

        private String name;
        private String value;

        PassDecideWayEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static PassDecideWayEnum fromValue(String value) {
            PassDecideWayEnum[] values = PassDecideWayEnum.values();
            for (PassDecideWayEnum passDecideWayEnum : values) {
                if (Objects.equals(passDecideWayEnum.getValue(), value)) {
                    return passDecideWayEnum;
                }
            }
            return null;
        }

    }

}