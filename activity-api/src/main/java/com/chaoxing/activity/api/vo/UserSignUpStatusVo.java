package com.chaoxing.activity.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/2 17:12
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpStatusVo {

    public Integer uid;

    public String uname;

    public String userName;

    List<UserSignUpVo> userSignUps;
}
