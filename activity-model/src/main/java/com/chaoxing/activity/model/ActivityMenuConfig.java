package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 活动菜单配置表
 * @className: ActivityMenuConfig, table_name: t_activity_menu_config
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-08-06 16:01:41
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_menu_config")
public class ActivityMenuConfig {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 菜单; column: menu*/
    private String menu;
    /**菜单来源：system-系统，template-模板，activity-活动; column: data_origin */
    private String dataOrigin;
    /**显示规则(no_limit, before_sign_up, after_sign_up); column: show_rule */
    private String showRule;
    /** 是否启用; column: is_enable */
    @TableField(value = "is_enable")
    private Boolean enable;
    /** 自定义应用模板组件id; column: template_component_id */
    private Integer templateComponentId;
    /** 排序 */
    private Integer sequence;

    @TableField(exist = false)
    private String type;

    @Getter
    public enum UrlTypeEnum {

        /** 前台 */
        FRONTEND("前台", "frontend"),
        /** 后台 */
        BACKEND("后台", "backend");

        private final String name;
        private final String value;

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

    @Getter
    public enum DataOriginEnum {
        /** "报名名单" */
        SYSTEM("系统", "system", 1),
        TEMPLATE("模板", "template", 5),
        ACTIVITY("活动", "activity", 10);

        private final String name;
        private final String value;
        private final Integer weight;

        DataOriginEnum(String name, String value, Integer weight) {
            this.name = name;
            this.value = value;
            this.weight = weight;
        }
        public static DataOriginEnum fromValue(String value) {
            DataOriginEnum[] values = DataOriginEnum.values();
            for (DataOriginEnum originEnum : values) {
                if (Objects.equals(originEnum.getValue(), value)) {
                    return originEnum;
                }
            }
            return null;
        }

        public static Boolean isSystem(String dataOrigin) {
            return Objects.equals(ActivityMenuConfig.DataOriginEnum.fromValue(dataOrigin), ActivityMenuConfig.DataOriginEnum.SYSTEM);
        }
    }

    @Getter
    public enum ShowRuleEnum {

        /** 不限 */
        NO_LIMIT("不限", "no_limit"),
        /** 报名前 */
        BEFORE_SIGN_UP("报名前", "before_sign_up"),
        /** 报名后 */
        AFTER_SIGN_UP("报名后", "after_sign_up");

        private final String name;
        private final String value;

        ShowRuleEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static ShowRuleEnum fromValue(String value) {
            ShowRuleEnum[] values = ShowRuleEnum.values();
            for (ShowRuleEnum ruleEnum : values) {
                if (Objects.equals(ruleEnum.getValue(), value)) {
                    return ruleEnum;
                }
            }
            return null;
        }
    }


    public static ActivityMenuConfig buildFromMenuDTO(ActivityMenuDTO menu) {
        String showRule = StringUtils.isBlank(menu.getShowRule()) ? ShowRuleEnum.NO_LIMIT.getValue() : menu.getShowRule();
        return ActivityMenuConfig.builder()
                .activityId(menu.getActivityId())
                .menu(menu.getCode())
                .dataOrigin(menu.getDataOrigin())
                .showRule(showRule)
                .enable(true)
                .sequence(menu.getSequence())
                .templateComponentId(menu.getTemplateComponentId())
                .build();
    }
}