package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * 用户成绩表
 * @className: UserResult, table_name: t_user_result
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-16 11:01:23
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_user_result")
public class UserResult {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 用户uid; column: uid*/
    private Integer uid;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 合格状态; column: qualified_status*/
    private Integer qualifiedStatus;
    /** 手动评审的合格状态。。0：不合格，1：合格，2：待处理; column: manual_qualified_status*/
    private Integer manualQualifiedStatus;
    /** 自动评审的合格状态。0：不合格，1：合格，2：待处理; column: auto_qualified_status*/
    private Integer autoQualifiedStatus;
    /** 总得分; column: total_score*/
    private BigDecimal totalScore;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    @Getter
    public enum QualifiedStatusEnum {

        /** 不合格 */
        NOT_QUALIFIED("不合格", 0),
        QUALIFIED("合格", 1),
        WAIT("待处理", 2);

        private final String name;
        private final Integer value;

        QualifiedStatusEnum(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static QualifiedStatusEnum fromValue(Integer value) {
            QualifiedStatusEnum[] values = QualifiedStatusEnum.values();
            for (QualifiedStatusEnum qualifiedStatusEnum : values) {
                if (Objects.equals(qualifiedStatusEnum.getValue(), value)) {
                    return qualifiedStatusEnum;
                }
            }
            return null;
        }

    }

}