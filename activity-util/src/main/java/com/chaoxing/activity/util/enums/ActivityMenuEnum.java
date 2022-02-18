package com.chaoxing.activity.util.enums;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
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
    ;
    @Getter
    public enum BackendMenuEnum {
        /** "报名名单" */
        SIGN_UP("报名名单", "sign_up", "管理报名名单、报名审核等功能", 1),
        SIGN_IN("签到管理", "sign_in", "管理签到、发布签到", 10),
        FORM_COLLECTION("表单采集", "form_collection", "管理表单采集、发布表单采集", 20),
        RESULTS_MANAGE("考核管理", "results_manage", "审核用户的成绩是否为合格", 30),
        CERTIFICATE("证书发放", "certificate", "", 40),
        NOTICE("发通知", "notice", "", 50),
        STAT("统计", "stat", "", 60),

        /** 班级互动 */
        task("任务", "task", "", 70),
        DISCUSS("讨论", "discuss", "", 80),
        HOMEWORK("作业", "homework", "", 90),
        REVIEW_MANAGEMENT("评审", "review_management", "", 100),

        SETTING("设置", "setting", "", 10000);

        private final String name;
        private final String value;
        private final String desc;
        private final Integer sequence;

        BackendMenuEnum(String name, String value, String desc, Integer sequence) {
            this.name = name;
            this.value = value;
            this.desc = desc;
            this.sequence = sequence;
        }

        public static BackendMenuEnum fromValue(String value) {
            for (BackendMenuEnum backendMenuEnum : BackendMenuEnum.values()) {
                if (Objects.equals(backendMenuEnum.getValue(), value)) {
                    return backendMenuEnum;
                }
            }
            return null;
        }

        public static Boolean isClazzInteractionMenu(String menu) {
            if (StringUtils.isBlank(menu)) {
                return false;
            }
            List<BackendMenuEnum> clazzInteractionMenus = Lists.newArrayList(BackendMenuEnum.task, BackendMenuEnum.HOMEWORK, BackendMenuEnum.DISCUSS, BackendMenuEnum.REVIEW_MANAGEMENT);
            return clazzInteractionMenus.contains(BackendMenuEnum.fromValue(menu));
        }
    }

    @Getter
    private enum IconEnum {
        /** 万能icon */
        UNIVERSAL("universal", "55f4ed98f5c419f9c070e885547ec141"),
        /** 签到 */
        SIGN_IN("sign_in", "fa4e1e4e86526f5a9e4033bcc34de566"),
        /** 阅读测评icon */
        READING_TEST("reading_test", "a9503abf09bc06ed398b0232a584a43b"),
        /** 报名信息icon */
        SIGN_UP_INFO("sign_up_info", "f3c03ffc5605069be2a2b4eb88c0dc1c"),
        /** 评价icon */
        RATING("rating", "0c3dbf1ae772af825c12c45ab32f7791"),
        /** 管理 */
        MANAGEMENT("manage-transparent", "d83b11e4e2cebe4a25b0552bf8653a40");

        private final String name;
        private final String value;

        IconEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    @Getter
    public enum FrontendMenuEnum {
        /** 去报名 */
        TO_SIGN_UP("报名参与", "to_sign_up", IconEnum.UNIVERSAL.getValue(), false, 10, "before_sign_up"),
        /** 进入会场 */
        ENTER_VENUE("进入会场", "enter_venue", IconEnum.UNIVERSAL.getValue(), false, 20, "no_limit"),
        /** 去签到，点击跳转签到列表页 */
        TO_SIGN_IN("去签到", "to_sign_in", IconEnum.SIGN_IN.getValue(), false, 30, "after_sign_up"),
        /** 去填写，点击跳转表单采集填写列表页 */
        TO_FILL_FORM_COLLECTION("去填写", "to_fill_form_collection", IconEnum.UNIVERSAL.getValue(), false, 40, "after_sign_up"),
        /** 阅读测评 */
        TO_READING("阅读测评", "to_reading", IconEnum.READING_TEST.getValue(), false, 50, "after_sign_up"),
        /** 讨论小组 */
        TO_DISCUSSION_GROUP("讨论小组", "to_discussion_group", IconEnum.UNIVERSAL.getValue(), false, 60, "after_sign_up"),
        /** 评价 */
        TO_RATE("评价", "to_rate", IconEnum.RATING.getValue(), false, 70, "after_sign_up"),
        /** 活动管理 */
        TO_MANAGE("管理", "to_manage", IconEnum.MANAGEMENT.getValue(), false, 80, "no_limit"),
        /** 查看报名信息 */
        TO_SIGN_UP_INFO("报名信息", "to_sign_up_info", IconEnum.SIGN_UP_INFO.getValue(), false, 90, "after_sign_up"),
        /** 进入班级互动主页 */
        TO_CLASS_INTERACTION_HOMEPAGE("进入主页", "to_class_interaction_homepage", IconEnum.UNIVERSAL.getValue(), false, 100, "after_sign_up"),

        /** 状态按钮，不可点击 */
        /** 活动已结束 */
        ACTIVITY_ENDED("活动已结束", "activity_ended", IconEnum.UNIVERSAL.getValue(), true, 110, "no_limit");

        /** 按钮名称 */
        private final String name;
        /** 按钮code */
        private final String value;
        /** 按钮图标 */
        private final String icon;
        /** 是否是状态按钮 */
        private final Boolean statusBtn;
        /** 按钮排序 作品征集排序 50 -- 70间, 自定义按钮相关 150 开始默认排序 */
        private final Integer sequence;
        /** 默认显示规则 */
        private final String showRule;

        FrontendMenuEnum(String name, String value, String icon, Boolean statusBtn, Integer sequence, String showRule) {
            this.name = name;
            this.value = value;
            this.icon = icon;
            this.statusBtn = statusBtn;
            this.sequence = sequence;
            this.showRule = showRule;
        }
        public static FrontendMenuEnum fromValue(String value) {
            for (FrontendMenuEnum frontendMenuEnum : FrontendMenuEnum.values()) {
                if (Objects.equals(frontendMenuEnum.getValue(), value)) {
                    return frontendMenuEnum;
                }
            }
            return null;
        }

        public static Boolean isClazzInteractionMenu(String menu) {
            return Objects.equals(FrontendMenuEnum.fromValue(menu), FrontendMenuEnum.TO_CLASS_INTERACTION_HOMEPAGE);
        }
    }

    public static Boolean isClazzInteractionMenu(String menu) {
        return BackendMenuEnum.isClazzInteractionMenu(menu) || FrontendMenuEnum.isClazzInteractionMenu(menu);
    }



}