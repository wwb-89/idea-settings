package com.chaoxing.activity.api.vo;

import com.chaoxing.activity.dto.manager.sign.UserSignUpDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;
import java.util.Optional;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/2 15:58
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpVo {

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
    private Long signUpDate;
    /** 报名时间 */
    private Long signUpTime;
    /** 报名状态 */
    private Integer signUpStatus;
    /** 报名创建时间 */
    private Long createTime;
    /** 报名修改时间*/
    private Long updateTime;

    public static UserSignUpVo buildUserSignUp(UserSignUpDTO userSignUpDTO) {
        return UserSignUpVo.builder()
                .signId(userSignUpDTO.getSignId())
                .signUpId(userSignUpDTO.getSignUpId())
                .signUpName(userSignUpDTO.getSignUpName())
                .uid(userSignUpDTO.getUid())
                .uname(userSignUpDTO.getUname())
                .userName(userSignUpDTO.getUserName())
                .signUpDate(Optional.ofNullable(userSignUpDTO.getSignUpDate()).map(v -> v.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli()).orElse(null))
                .signUpTime(Optional.ofNullable(userSignUpDTO.getSignUpTime()).map(v -> v.toInstant(ZoneOffset.of("+8")).toEpochMilli()).orElse(null))
                .signUpStatus(userSignUpDTO.getSignUpStatus())
                .createTime(Optional.ofNullable(userSignUpDTO.getCreateTime()).map(v -> v.toInstant(ZoneOffset.of("+8")).toEpochMilli()).orElse(null))
                .updateTime(Optional.ofNullable(userSignUpDTO.getUpdateTime()).map(v -> v.toInstant(ZoneOffset.of("+8")).toEpochMilli()).orElse(null))
                .build();
    }
}
