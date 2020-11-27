
package com.chaoxing.activity.dmin.util;

import com.chaoxing.activity.dto.LoginUserDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**登录工具
 * @className LoginUtils
 * @description
 * @author wwb
 * @date 2018-08-06 09:10:44
 * @version ver 1.0
 */
public class LoginUtils {

    private LoginUtils() {

    }

    /** session中保存的用户信息key */
    public static final String LOGIN_USER_SESSION_KEY = "_login_user_";

    public static LoginUserDTO getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            Object attribute = session.getAttribute(LOGIN_USER_SESSION_KEY);
            if (attribute != null) {
                LoginUserDTO loginUserDTO = (LoginUserDTO)attribute;
                return loginUserDTO;
            }
        }
        return null;
    }

    public static void login(HttpServletRequest request, LoginUserDTO loginUser) {
        if (loginUser != null) {
            HttpSession session = request.getSession();
            session = Optional.ofNullable(session).orElse(request.getSession(true));
            session.setAttribute(LOGIN_USER_SESSION_KEY, loginUser);
        }
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.removeAttribute(LOGIN_USER_SESSION_KEY);
        }
    }

}
