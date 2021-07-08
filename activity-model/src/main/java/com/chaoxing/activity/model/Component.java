package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 组件表
 * @className: Component, table_name: t_component
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:08
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_component")
public class Component {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 父组件id; column: pid*/
    private Integer pid;
    /** 组件名称; column: name*/
    private String name;
    /** 组件编码。系统字段才有code; column: code*/
    private String code;
    /** 是否必填; column: is_required*/
    @TableField(value = "is_required")
    private Boolean required;
    /** 简介; column: introduction*/
    private String introduction;
    /** 是否是系统组件; column: is_system*/
    @TableField(value = "is_system")
    private Boolean system;
    /** 是否支持多个组件; column: is_multi*/
    @TableField(value = "is_multi")
    private Boolean multi;
    /** 组件类型。自定义组件才有类型：文本、单选、多选; column: type*/
    private String type;
    /** 数据来源; column: data_origin*/
    private String dataOrigin;
    /** 来源主键; column: origin_identify*/
    private String originIdentify;
    /** 字段标识; column: field_flag*/
    private String fieldFlag;
    /** 所属机构id; column: fid*/
    private Integer fid;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人uid; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人uid; column: update_uid*/
    private Integer updateUid;

    @TableField(exist = false)
    private List<ComponentField> fieldList;

    @Getter
    public enum TypeEnum {

        TEXT("文本", "text"),
        RADIO("单选", "radio"),
        CHECKBOX("多选", "checkbox");

        private String name;
        private String value;

        TypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static TypeEnum fromValue(String value) {
            TypeEnum[] values = TypeEnum.values();
            for (TypeEnum typeEnum : values) {
                if (Objects.equals(typeEnum.getValue(), value)) {
                    return typeEnum;
                }
            }
            return null;
        }
    }

    @Getter
    public enum DataOriginEnum {

        /** 自定义 */
        CUSTOM("自定义", "custom"),
        FORM("表单", "form");

        private String name;
        private String value;

        DataOriginEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static DataOriginEnum fromValue(String value) {
            DataOriginEnum[] values = DataOriginEnum.values();
            for (DataOriginEnum dataOriginEnum : values) {
                if (Objects.equals(dataOriginEnum.getValue(), value)) {
                    return dataOriginEnum;
                }
            }
            return null;
        }
    }

}