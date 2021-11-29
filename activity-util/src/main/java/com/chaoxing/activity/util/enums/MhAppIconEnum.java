package com.chaoxing.activity.util.enums;

import lombok.Getter;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/13 15:33
 * <p>
 */
@Getter
public enum MhAppIconEnum {
    ;

    @Getter
    public enum ONE {

        /** 收藏 */
        COLLECTED("collected", "265c36f8e17688c48bcea1290ae5b272"),
        /** 浏览 */
        BROWSE("browse", "7337c2c2951fbcd5ab3ec84b0ce87609"),
        /** 签到数 */
        SIGNED_IN_NUM("signed-in-num", "70a0bc103aedda1f1cd5fcc1b0524595"),
        /** 时间 */
        TIME("time", "9a08c693ef2457b6a26169603cea851d"),
        /** 统计-彩色 */
        STATISTICS_COLOR("statistics-color", "26352e7dcdbbd10b695dbcc015c19ba2"),
        /** 海报 */
        POSTER("poster", "b22c13be2b47131378779c8a636fe173"),
        /** 已报名用户 */
        SIGNED_UP_USER("signed-up-user", "8b2229560d8df09a5c216b2ad38de9f3"),
        /** 积分 */
        INTEGRAL("integral", "f71b7862035605bbd91a3ab8bf3dbcc4"),
        /** 时间-透明 */
        TIME_TRANSPARENT("time-transparent", "339f5085fb968439dd0baaaa3a026aa9"),
        /** 活动地点 */
        LOCATION("location", "b750b5f467e39fcb23bc8a6160305f21"),
        /** 主办方 */
        ORGANISER("organiser", "b8aa41af62d0dc9e2e873b13ebcf4949"),
        /** 报名数 */
        SIGNED_UP_NUM("signed-up-num", "4ccd8f1195ca7712ac806ac441d14991"),
        /** 评价 */
        RATING("rating", "c840a53bfc108db42b80710af8567465"),
        /** 管理 */
        MANAGE_TRANSPARENT("manage-transparent", "d83b11e4e2cebe4a25b0552bf8653a40");

        private final String name;
        private final String value;

        ONE(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    @Getter
    public enum TWO {

        /** 警告 */
        WARNING("warning", "3efb02948d2348640a5dfbcb458d3659"),
        /** 审核 */
        REVIEW("review", "dfccc3c6827b78e18e6c4423b08a0eca"),
        /** 成功 */
        SUCCESS("success", "dbaa4787179f720774c845bfcee2cb5d");

        private final String name;
        private final String value;

        TWO(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    /** 按钮的图标枚举 */
    @Getter
    public enum THREE {

        /** 万能icon */
        UNIVERSAL("universal", "55f4ed98f5c419f9c070e885547ec141"),
        /** 作品审核icon */
        WORK_REVIEW("work_review", "96c16a372012948dc6fd0ed0bd096c60"),
        /** 作品优选icon */
        WORK_PREFERRED_SELECTION("work_preferred_selection", "f9bd7f66745711533ea305d940226884"),
        /** 我的作品icon */
        MY_WORK("my_work", "54eb45f8017e868f59447dee034994a6"),
        /** 全部作品icon */
        ALL_WORK("all_work", "57687912bf5be5c78fc744ece4347ae3"),
        /** 报名信息icon */
        SIGN_UP_INFO("sign_up_info", "f3c03ffc5605069be2a2b4eb88c0dc1c"),
        /** 报名详情icon */
        SIGN_UP_DETAIL("sign_up_detail", "8b615070b69388b05392972ecdb10e38"),
        /** 提交作品icon */
        SUBMIT_WORK("submit_work", "846cdf79fc0cf54da259db4ca7fe2ab6"),
        /** 更多icon */
        MORE("MORE", "6af1a60c5935677f11679afd3d444d19"),
        /** 评价icon */
        RATING("rating", "7fdfbea39df1f9779b04c956b584012e"),
        /** 评分icon */
        RATING_SCORE("rating_score", "6a8c415821313ceed15381f10042f6d5");

        private final String name;
        private final String value;

        THREE(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}

