package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 活动市场数据推送配置表
 * @className: DataPushConfig, table_name: t_data_push_config
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-10-29 14:25:53
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_data_push_config")
public class DataPushConfig {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动市场id; column: market_id*/
    private Integer marketId;
    /** 推送名称; column: name*/
    private String name;
    /** 推送数据类型; column: data_type*/
    private String dataType;
    /** 推送方式; column: way*/
    private String way;
    /** 推送方式值; column: way_value*/
    private String wayValue;
    /** 机构id; column: fid*/
    private Integer fid;
    /** 是否启用; column: is_enable*/
    @TableField(value = "is_enable")
    private Boolean enable;
    /** 是否删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人id; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人id; column: update_uid*/
    private Integer updateUid;

    @Getter
    public enum DataTypeEnum {

        /** 活动数据 */
        ACTIVITY_DATA("活动数据", "activity_data"),
        USER_DATA("用户数据", "user_data");

        private final String name;
        private final String value;

        DataTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

    }

    @Getter
    public enum WayEnum {

        /** 活动数据 */
        WFW_FORM("万能表单", "wfw_form"),
        URL("接口", "url");

        private final String name;
        private final String value;

        WayEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static WayEnum fromValue(String value) {
            WayEnum[] values = WayEnum.values();
            for (WayEnum wayEnum : values) {
                if (Objects.equals(wayEnum.getValue(), value)) {
                    return wayEnum;
                }
            }
            return null;
        }

    }

}