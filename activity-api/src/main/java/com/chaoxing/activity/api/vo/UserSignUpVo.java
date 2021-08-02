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

    /** 报名id */
    private Integer signUpId;
    /** 报名名称 */
    private String signUpName;
    /** 报名时间 */
    private Long signUpTime;
    /** 报名状态 */
    private String signUpStatus;

    public static UserSignUpVo buildUserSignUp(UserSignUpDTO userSignUpDTO) {
        return UserSignUpVo.builder()
                .signUpId(userSignUpDTO.getSignUpId())
                .signUpName(userSignUpDTO.getSignUpName())
                .signUpTime(Optional.ofNullable(userSignUpDTO.getSignUpTime()).map(v -> v.toInstant(ZoneOffset.of("+8")).toEpochMilli()).orElse(null))
                .signUpStatus(userSignUpDTO.getSignUpStatus())
                .build();
    }
}
