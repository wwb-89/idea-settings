package com.chaoxing.activity.util.exception;

import lombok.Getter;

/**需要登录
 * @author wwb
 * @version ver 1.0
 * @className LoginRequiredException
 * @description
 * @blame wwb
 * @date 2021-05-20 10:39:55
 */
@Getter
public class LoginRequiredException extends BusinessException {

    public LoginRequiredException() {
        super("请登录");
    }

    public LoginRequiredException(String message) {
        super(message);
    }
}
