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
        COLLECTED("collected", "fd55f9794e52c4125b9c885e4b8297c8"),
        /** 浏览 */
        BROWSE("browse", "465489a8bbcb36d493c98b5ff7066def"),
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
        SIGNED_UP_NUM("signed-up-num", "cce3d846e1cfc2e59f8352bc99495668"),
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

}

