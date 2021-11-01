package com.chaoxing.activity.api.controller.action;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.event.user.*;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.event.user.*;
import com.chaoxing.activity.service.queue.user.UserActionQueue;
import com.chaoxing.activity.util.DateUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**报名签到用户行为服务
 * @author wwb
 * @version ver 1.0
 * @className UserActionChangeApiController
 * @description
 * @blame wwb
 * @date 2021-05-25 11:08:55
 */
@RestController
@RequestMapping("user/action")
public class UserActionApiController {

    @Resource
    private UserActionQueue userActionQueue;
    @Resource
    private ActivityQueryService activityQueryService;

    @Resource
    private UserSignedUpEventQueue userSignedUpEventQueue;
    @Resource
    private UserCancelSignUpEventQueue userCancelSignUpEventQueue;
    @Resource
    private UserSignedInEventQueue userSignedInEventQueue;
    @Resource
    private UserCancelSignInEventQueue userCancelSignInEventQueue;
    @Resource
    private UserLeaveSignInEventQueue userLeaveSignInEventQueue;

    /**用户报名
     * @Description 
     * @author wwb
     * @Date 2021-06-23 16:07:43
     * @param uid
     * @param signId
     * @param signUpId
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("signed-up")
    public RestRespDTO signedUp(Integer uid, Integer signId, Integer signUpId, Long time) {
        UserSignedUpEventOrigin userSignedUpEventOrigin = UserSignedUpEventOrigin.builder()
                .uid(uid)
                .signId(signId)
                .signUpId(signUpId)
                .signedUpTime(DateUtils.timestamp2Date(time))
                .timestamp(time)
                .build();
        userSignedUpEventQueue.push(userSignedUpEventOrigin);
        return RestRespDTO.success();
    }

    /**取消报名
     * @Description 
     * @author wwb
     * @Date 2021-06-23 16:08:17
     * @param uid
     * @param signId
     * @param signUpId
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("cancel-sign-up")
    public RestRespDTO cancelSignUp(Integer uid, Integer signId, Integer signUpId, Long time) {
        UserCancelSignUpEventOrigin userCancelSignedUpEventOrigin = UserCancelSignUpEventOrigin.builder()
                .uid(uid)
                .signId(signId)
                .signUpId(signUpId)
                .timestamp(time)
                .build();
        userCancelSignUpEventQueue.push(userCancelSignedUpEventOrigin);
        return RestRespDTO.success();
    }

    /**用户签到
     * @Description 
     * @author wwb
     * @Date 2021-06-23 16:08:30
     * @param uid
     * @param signId
     * @param signInId
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("signed-in")
    public RestRespDTO signedIn(Integer uid, Integer signId, Integer signInId, Long time) {
        UserSignedInEventOrigin userSignedInEventOrigin = UserSignedInEventOrigin.builder()
                .uid(uid)
                .signId(signId)
                .signInId(signInId)
                .signedInTime(DateUtils.timestamp2Date(time))
                .timestamp(time)
                .build();
        userSignedInEventQueue.push(userSignedInEventOrigin);
        return RestRespDTO.success();
    }

    /**用户取消签到
     * @Description 
     * @author wwb
     * @Date 2021-06-23 16:08:41
     * @param uid
     * @param signId
     * @param signInId
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("cancel-sign-in")
    public RestRespDTO cancelSignIn(Integer uid, Integer signId, Integer signInId, Long time) {
        UserCancelSignInEventOrigin userCancelSignInEventOrigin = UserCancelSignInEventOrigin.builder()
                .uid(uid)
                .signId(signId)
                .signInId(signInId)
                .timestamp(time)
                .build();
        userCancelSignInEventQueue.push(userCancelSignInEventOrigin);
        return RestRespDTO.success();
    }

    /**用户请假
     * @Description 
     * @author wwb
     * @Date 2021-06-23 16:08:52
     * @param uid
     * @param signId
     * @param signInId
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("leave-sign-in")
    public RestRespDTO leaveSignIn(Integer uid, Integer signId, Integer signInId, Long time) {
        UserLeaveSignInEventOrigin userLeaveSignInEventOrigin = UserLeaveSignInEventOrigin.builder()
                .uid(uid)
                .signId(signId)
                .signInId(signInId)
                .leaveTime(DateUtils.timestamp2Date(time))
                .timestamp(time)
                .build();
        userLeaveSignInEventQueue.push(userLeaveSignInEventOrigin);
        return RestRespDTO.success();
    }

}