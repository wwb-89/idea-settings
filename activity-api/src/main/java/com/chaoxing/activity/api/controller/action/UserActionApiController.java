package com.chaoxing.activity.api.controller.action;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.queue.UserActionDetailQueueService;
import com.chaoxing.activity.service.queue.UserActionQueueService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.enums.UserActionEnum;
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
    private UserActionQueueService userSignActionQueueService;
    @Resource
    private UserActionDetailQueueService userActionDetailQueueService;

    @RequestMapping("signed-up")
    public RestRespDTO signedUp(Integer uid, Integer signId, Integer signUpId, Long time) {
        userSignActionQueueService.addUserSignUpAction(UserActionQueueService.QueueParamDTO.builder().uid(uid).signId(signId).signUpId(signUpId).userAction(UserActionEnum.SIGNED_UP).time(DateUtils.timestamp2Date(time)).build());
        return RestRespDTO.success();
    }

    @RequestMapping("cancel-sign-up")
    public RestRespDTO cancelSignUp(Integer uid, Integer signId, Integer signUpId, Long time) {
        userSignActionQueueService.addUserSignUpAction(UserActionQueueService.QueueParamDTO.builder().uid(uid).signId(signId).signUpId(signUpId).userAction(UserActionEnum.CANCEL_SIGNED_UP).time(DateUtils.timestamp2Date(time)).build());
        return RestRespDTO.success();
    }

    @RequestMapping("signed-in")
    public RestRespDTO signedIn(Integer uid, Integer signId, Integer signInId, Long time) {
        userSignActionQueueService.addUserSignInAction(UserActionQueueService.QueueParamDTO.builder().uid(uid).signId(signId).signInId(signInId).userAction(UserActionEnum.SIGNED_IN).time(DateUtils.timestamp2Date(time)).build());
        return RestRespDTO.success();
    }

    @RequestMapping("cancel-sign-in")
    public RestRespDTO cancelSignIn(Integer uid, Integer signId, Integer signInId, Long time) {
        userSignActionQueueService.addUserSignInAction(UserActionQueueService.QueueParamDTO.builder().uid(uid).signId(signId).signInId(signInId).userAction(UserActionEnum.CANCEL_SIGNED_UP).time(DateUtils.timestamp2Date(time)).build());
        return RestRespDTO.success();
    }

    @RequestMapping("result-change")
    public RestRespDTO result(Integer uid, Integer signId) {
        userSignActionQueueService.addUserResultAction(UserActionQueueService.QueueParamDTO.builder().uid(uid).signId(signId).build());
        return RestRespDTO.success();
    }

    @RequestMapping("work/add")
    public RestRespDTO submitWork(Integer workActivityId, Integer workId, Integer uid, Long createTime) {
        userActionDetailQueueService.push(UserActionDetailQueueService.QueueParamDTO.builder().build());
        return RestRespDTO.success();
    }

    @RequestMapping("work/delete")
    public RestRespDTO deleteWork(Integer workActivityId, Integer workId, Integer uid) {
        userActionDetailQueueService.push(UserActionDetailQueueService.QueueParamDTO.builder().build());
        return RestRespDTO.success();
    }

}