package com.chaoxing.activity.dto.manager.sign;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户报名表
 * @className: UserSignUp, table_name: t_user_sign_up
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-12-16 14:08:53
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUp {

    /** 主键 */
    private Integer id;
    /** 用户id */
    private Integer uid;
    /** 用户名 */
    private String uname;
    /** 用户姓名 */
    private String userName;
    /** 报名签到id */
    private Integer signId;
    /** 报名id */
    private Integer signUpId;
    /** 报名日期 */
    private LocalDate signUpDate;
    /** 报名时间 */
    private LocalDateTime signUpTime;
    /** 报名状态。0：取消，1：成功，2：待审核，3：审核失败 */
    private Integer signUpStatus;
    /** 是否代理报名 */
    private Boolean proxy;
    /** 代理人id */
    private Integer proxyUid;
    /** 代理人姓名 */
    private String proxyUserName;
    /** 审核时间 */
    private String auditTime;
    /** 审核信息 */
    private String auditMessage;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 创建人id */
    private Integer createUid;
    /** 创建人姓名 */
    private String createUserName;
    /** 创建人fid */
    private Integer createFid;
    /** 创建人机构名 */
    private String createOrgName;
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 更新人id */
    private Integer updateUid;

    /**报名状态枚举
     * @Description
     * @author wwb
     * @Date 2020-12-17 11:03:03
     * @return
     */
    @Getter
    public enum SignUpStatusEnum{

        /** 取消 */
        CANCEL("取消", 0),
        SUCCESS("成功", 1),
        WAIT_AUDIT("待审核", 2),
        AUDIT_FAIL("审核失败", 3);

        private String name;
        private Integer value;

        SignUpStatusEnum(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static UserSignUp.SignUpStatusEnum fromValue(Integer value) {
            UserSignUp.SignUpStatusEnum[] values = UserSignUp.SignUpStatusEnum.values();
            for (UserSignUp.SignUpStatusEnum signUpStatusEnum : values) {
                if (Objects.equals(signUpStatusEnum.getValue(), value)) {
                    return signUpStatusEnum;
                }
            }
            return null;
        }

        public static UserSignUp.SignUpStatusEnum fromName(String name) {
            UserSignUp.SignUpStatusEnum[] values = UserSignUp.SignUpStatusEnum.values();
            for (UserSignUp.SignUpStatusEnum signUpStatusEnum : values) {
                if (Objects.equals(signUpStatusEnum.getName(), name)) {
                    return signUpStatusEnum;
                }
            }
            return null;
        }

    }

}