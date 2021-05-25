package com.chaoxing.activity.dto.manager.sign;

import com.alibaba.fastjson.annotation.JSONField;
import com.chaoxing.activity.util.LocalDateTime2TimestampDeserializer;
import com.chaoxing.activity.util.LocalDateTimeSerializer;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 签到表
 * @className: SignIn
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-12-16 14:08:53
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignIn {

    /** 主键 */
    private Integer id;
    /** 报名签到id */
    private Integer signId;
    /** 签到名称 */
    private String name;
    /** 签到类型：签到、签退 */
    private String type;
    /** 签退关联的签到id， 当类型是签退时有效 */
    private String signInId;
    /** 签到码 */
    private String code;
    /** 开始时间 */
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTime2TimestampDeserializer.class)
    private LocalDateTime startTime;
    /** 结束时间 */
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTime2TimestampDeserializer.class)
    private LocalDateTime endTime;
    /** 签到方式。1：普通签到、2：位置签到、3：二维码签到 */
    private Integer way;
    /** 签到地址 */
    private String address;
    /** 详细地址 */
    private String detailAddress;
    /** 签到经度 */
    private BigDecimal longitude;
    /** 签到维度 */
    private BigDecimal dimension;
    /** 扫码方式。1：参与者扫码，2：参与者扫码 */
    private Integer scanCodeWay;
    /** 是否填写信息 */
    private Boolean fillInfo;
    /** 填写信息的表单id */
    private Integer fillInfoFormId;
    /** 是否公告签到名单 */
    private Boolean publicList;
    /** 按钮名称 */
    private String btnName;
    /** 是否被删除 */
    private Boolean deleted;
    /** 创建时间 */
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTime2TimestampDeserializer.class)
    private LocalDateTime createTime;
    /** 创建人id */
    private Integer createUid;
    /** 创建人姓名 */
    private String createUserName;
    /** 创建机构id */
    private Integer createFid;
    /** 创建机构名称 */
    private String createOrgName;
    /** 更新时间 */
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTime2TimestampDeserializer.class)
    private LocalDateTime updateTime;
    /** 更新人id */
    private Integer updateUid;

    // 附加
    /** 开始时间字符串表示 */
    private Long startTimestamp;
    /** 结束时间字符串表示 */
    private Long endTimestamp;

    /** 签到方式枚举
     * @className SignIn
     * @description 
     * @author wwb
     * @blame wwb
     * @date 2021-04-01 19:27:56
     * @version ver 1.0
     */
    @Getter
    public enum Way {

        /** 普通签到 */
        DIRECT("普通签到", 1),
        POSITION("位置签到", 2),
        QR_CODE("二维码签到", 3);

        private String name;
        private Integer value;

        Way(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static Way fromValue(Integer value) {
            Way[] values = Way.values();
            for (Way way : values) {
                if (Objects.equals(way.getValue(), value)) {
                    return way;
                }
            }
            return null;
        }

        public static void notNull(Way way) {
            Optional.ofNullable(way).orElseThrow(() -> new BusinessException("未知的签到方式"));
        }

    }

    public static SignIn buildDefaultSignIn() {
        SignIn signIn = buildDefault();
        signIn.setName("签到");
        signIn.setType("sign_in");
        signIn.setBtnName("签到");
        return signIn;
    }

    public static SignIn buildDefaultSignOut() {
        SignIn signIn = buildDefault();
        signIn.setName("签退");
        signIn.setType("sign_out");
        signIn.setBtnName("签退");
        return signIn;
    }

    private static SignIn buildDefault() {
        LocalDateTime now = LocalDateTime.now();
        return SignIn.builder()
                .name("签到")
                .startTime(now)
                .endTime(now.plusHours(2))
                .way(1)
                .address("")
                .detailAddress("")
                .scanCodeWay(1)
                .fillInfo(false)
                .publicList(false)
                .deleted(false)
                .build();
    }

}