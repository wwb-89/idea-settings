package com.chaoxing.activity.util.enums;

import lombok.Getter;

import java.util.Objects;

/**活动自动字段枚举
 * @author wwb
 * @version ver 1.0
 * @className ActivitySystemFieldEnum
 * @description
 * @blame wwb
 * @date 2021-12-10 11:33:08
 */
@Getter
public enum ActivitySystemFieldEnum {

    /** 活动id */
    ACTIVITY_ID("活动id", "activity_id"),
    ACTIVITY_NAME("活动名称", "activity_name"),
    ACTIVITY_START_TIME("活动开始时间", "activity_start_time"),
    ACTIVITY_END_TIME("活动结束时间", "activity_end_time"),
    ACTIVITY_COVER("活动封面", "cover_cloud_id"),
    ACTIVITY_TYPE("活动形式", "activity_type"),
    ACTIVITY_ADDRESS("活动地点", "activity_address"),
    LOCATION("活动位置", "location"),
    SIGN_UP_PARTICIPATE_SCOPE("报名参与范围", "sign_up_participate_scope"),
    CREATE_ORG("创建单位", "create_org"),
    ORGANISERS("活动主办方", "organisers"),
    ACTIVITY_CLASSIFY("活动分类", "activity_classify"),
    ACTIVITY_INTEGRAL("活动积分", "activity_integral"),
    UNIT("积分单位", "unit"),
    PREVIEW_URL("活动预览", "preview_url"),
    CREATE_USER("发起人", "create_user"),
    ACTIVITY_STATUS("活动状态", "activity_status"),
    TAGS("标签", "tags"),
    INTRODUCTION("简介", "introduction"),
    CREATOR_DEPARTMENT("创建者部门", "creator_department");

    private final String name;
    private final String value;

    ActivitySystemFieldEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static ActivitySystemFieldEnum fromValue(String value) {
        ActivitySystemFieldEnum[] activitySystemFieldEnums = ActivitySystemFieldEnum.values();
        for (ActivitySystemFieldEnum activitySystemFieldEnum : activitySystemFieldEnums) {
            if (Objects.equals(activitySystemFieldEnum.getValue(), value)) {
                return activitySystemFieldEnum;
            }
        }
        return null;
    }

}