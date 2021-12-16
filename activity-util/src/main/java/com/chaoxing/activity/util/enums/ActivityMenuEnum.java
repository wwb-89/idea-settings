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
    SIGN_UP("报名名单", "sign_up", "管理报名名单、报名审核等功能"),
    SIGN_IN("签到管理", "sign_in", "管理签到、发布签到"),
    RESULTS_MANAGE("考核管理", "results_manage", "审核用户参与活动的成绩是否为合格"),
    CERTIFICATE("证书", "certificate", ""),
    NOTICE("发通知", "notice", ""),
    STAT("统计", "stat", ""),
    SETTING("设置", "setting", "");

    private final String name;
    private final String value;
    private final String desc;

    ActivityMenuEnum(String name, String value, String desc) {
        this.name = name;
        this.value = value;
        this.desc = desc;
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