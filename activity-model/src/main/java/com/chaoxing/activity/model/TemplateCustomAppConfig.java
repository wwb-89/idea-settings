package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 模板自定义应用配置表
 * @className: TemplateCustomAppConfig, table_name: t_template_custom_app_config
 * @Description:
 * @author: mybatis generator
 * @date: 2021-12-23 11:33:22
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_template_custom_app_config")
public class TemplateCustomAppConfig {

    /** 主键id */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 自定义应用模板组件id; column: template_component_id */
    private Integer templateComponentId;
    /** 链接类型，frontend：前端，backend：后端; column: type */
    private String type;
    /** 链接标题; column: title */
    private String title;
    /** 图标id; column: icon_id */
    private String iconId;
    /** 链接; column: url */
    private String url;
    /** 是否在报名后显示，仅对前台链接生效; column: is_show_after_sign_up */
    @TableField(value = "is_show_after_sign_up")
    private Boolean showAfterSignUp;
    /** 是否以新页面方式打开，仅对后台链接生效; column: is_open_blank */
    @TableField(value = "is_open_blank")
    private Boolean openBlank;
    /** 是否删除; column: is_deleted */
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 顺序; column: sequence */
    private Integer sequence;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String defaultIconCloudId;
    @TableField(exist = false)
    private String activeIconCloudId;

    @Getter
    public enum UrlTypeEnum {

        /** 前台 */
        FRONTEND("前台", "frontend"),
        /** 后台 */
        BACKEND("后台", "backend");

        private String name;
        private String value;

        UrlTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static UrlTypeEnum fromValue(String value) {
            UrlTypeEnum[] values = UrlTypeEnum.values();
            for (UrlTypeEnum typeEnum : values) {
                if (Objects.equals(typeEnum.getValue(), value)) {
                    return typeEnum;
                }
            }
            return null;
        }
    }

}
