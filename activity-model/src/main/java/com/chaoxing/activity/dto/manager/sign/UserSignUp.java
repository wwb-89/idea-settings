package com.chaoxing.activity.dto.manager.sign;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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
@TableName(value = "t_user_sign_up")
public class UserSignUp {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 用户名; column: uname*/
    private String uname;
    /** 用户姓名; column: user_name*/
    private String userName;
    /** 报名签到id; column: sign_id*/
    private Integer signId;
    /** 报名id; column: sign_up_id*/
    private Integer signUpId;
    /** 报名时间; column: sign_up_time*/
    private LocalDateTime signUpTime;
    /** 报名状态。0：取消，1：成功，2：待审核，3：审核失败; column: sign_up_status*/
    private Integer signUpStatus;
    /** 是否代理报名; column: is_proxy*/
    @TableField(value = "is_proxy")
    private Boolean proxy;
    /** 代理人id; column: proxy_uid*/
    private Integer proxyUid;
    /** 代理人姓名; column: proxy_user_name*/
    private String proxyUserName;
    /** 审核信息; column: audit_message*/
    private String auditMessage;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人id; column: create_uid*/
    private Integer createUid;
    /** 创建人姓名; column: create_user_name*/
    private String createUserName;
    /** 创建人fid; column: create_fid*/
    private Integer createFid;
    /** 创建人机构名; column: create_org_name*/
    private String createOrgName;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人id; column: update_uid*/
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

        public static SignUpStatusEnum fromValue(Integer value) {
            SignUpStatusEnum[] values = SignUpStatusEnum.values();
            for (SignUpStatusEnum signUpStatusEnum : values) {
                if (Objects.equals(signUpStatusEnum.getValue(), value)) {
                    return signUpStatusEnum;
                }
            }
            return null;
        }

        public static SignUpStatusEnum fromName(String name) {
            SignUpStatusEnum[] values = SignUpStatusEnum.values();
            for (SignUpStatusEnum signUpStatusEnum : values) {
                if (Objects.equals(signUpStatusEnum.getName(), name)) {
                    return signUpStatusEnum;
                }
            }
            return null;
        }

    }

}