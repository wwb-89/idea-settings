package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/2 16:42
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpDTO {

    /** 报名签到id */
    private Integer signId;
    /** 报名id */
    private Integer signUpId;
    /** 报名名称 */
    private String signUpName;
    /** 用户id */
    private Integer uid;
    /** 用户名 */
    private String uname;
    /** 用户姓名 */
    private String userName;
    /** 报名日期 */
    private LocalDate signUpDate;
    /** 报名时间 */
    private LocalDateTime signUpTime;
    /** 报名状态 */
    private String signUpStatus;
    /** 报名创建时间 */
    private LocalDateTime createTime;
    /** 报名修改时间*/
    private LocalDateTime updateTime;

}
