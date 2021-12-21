package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 报名万能表单模版表
 * @className: SignUpWfwFormTemplate, table_name: t_sign_up_wfw_form_template
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-11-18 16:09:24
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_sign_up_wfw_form_template")
public class SignUpWfwFormTemplate {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 名称; column: name*/
    private String name;
    /** 编码; column: code*/
    private String code;
    /** sign; column: sign*/
    private String sign;
    /** key; column: key*/
    @TableField(value = "`key`")
    private String key;
    /** 表单id; column: form_id*/
    private Integer formId;
    /** 表单所属机构id; column: fid*/
    private Integer fid;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 是否系统表单; column: is_system*/
    @TableField(value = "is_system")
    private Boolean system;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    @Getter
    public enum TypeEnum {

        /** 标准的 */
        NORMAL("标准的", "normal"),
        APPROVAL("审批", "approval");

        private final String name;
        private final String value;

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

}