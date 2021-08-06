package com.chaoxing.activity.util.enums;

import lombok.Getter;

import java.util.Objects;

/**活动菜单
 * @author wwb
 * @version ver 1.0
 * @className ActivityMenuEnum
 * @description
 * @blame wwb
 * @date 2021-08-06 16:03:24
 */
@Getter
public enum ActivityMenuEnum {

    /** "报名名单" */
    SIGN_UP("报名名单", "sign_up"),
    SIGN_IN("签到管理", "sign_in"),
    RESULTS_MANAGE("考核管理", "results_manage");

    private final String name;
    private final String value;

    ActivityMenuEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static ActivityMenuEnum fromValue(String value) {
        ActivityMenuEnum[] values = ActivityMenuEnum.values();
        for (ActivityMenuEnum activityMenuEnum : values) {
            if (Objects.equals(activityMenuEnum.getValue(), value)) {
                return activityMenuEnum;
            }
        }
        return null;
    }

}