package com.chaoxing.activity.api.controller.action;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.user.UserActionQueueService;
import com.chaoxing.activity.service.queue.user.UserSignQueueService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.enums.UserActionEnum;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
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
    private UserSignQueueService userSignQueueService;
    @Resource
    private UserActionQueueService userActionQueueService;
    @Resource
    private ActivityQueryService activityQueryService;

    @RequestMapping("signed-up")
    public RestRespDTO signedUp(Integer uid, Integer signId, Integer signUpId, Long time) {
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            userActionQueueService.push(UserActionQueueService.QueueParamDTO.builder()
                    .uid(uid)
                    .activityId(activity.getId())
                    .userActionType(UserActionTypeEnum.SIGN_UP)
                    .userAction(UserActionEnum.SIGNED_UP)
                    .identify(String.valueOf(signUpId))
                    .time(DateUtils.timestamp2Date(time))
                    .build());
        }
        return RestRespDTO.success();
    }

    @RequestMapping("cancel-sign-up")
    public RestRespDTO cancelSignUp(Integer uid, Integer signId, Integer signUpId, Long time) {
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            userActionQueueService.push(UserActionQueueService.QueueParamDTO.builder()
                    .uid(uid)
                    .activityId(activity.getId())
                    .userActionType(UserActionTypeEnum.SIGN_UP)
                    .userAction(UserActionEnum.CANCEL_SIGNED_UP)
                    .identify(String.valueOf(signUpId))
                    .time(DateUtils.timestamp2Date(time))
                    .build());
        }
        return RestRespDTO.success();
    }

    @RequestMapping("signed-in")
    public RestRespDTO signedIn(Integer uid, Integer signId, Integer signInId, Long time) {
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            userActionQueueService.push(UserActionQueueService.QueueParamDTO.builder()
                    .uid(uid)
                    .activityId(activity.getId())
                    .userActionType(UserActionTypeEnum.SIGN_IN)
                    .userAction(UserActionEnum.SIGNED_IN)
                    .identify(String.valueOf(signInId))
                    .time(DateUtils.timestamp2Date(time))
                    .build());
        }
        return RestRespDTO.success();
    }

    @RequestMapping("cancel-sign-in")
    public RestRespDTO cancelSignIn(Integer uid, Integer signId, Integer signInId, Long time) {
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            userActionQueueService.push(UserActionQueueService.QueueParamDTO.builder()
                    .uid(uid)
                    .activityId(activity.getId())
                    .userActionType(UserActionTypeEnum.SIGN_IN)
                    .userAction(UserActionEnum.CANCEL_SIGNED_IN)
                    .identify(String.valueOf(signInId))
                    .time(DateUtils.timestamp2Date(time))
                    .build());
        }
        return RestRespDTO.success();
    }

    @RequestMapping("work/add")
    public RestRespDTO submitWork(Integer workActivityId, Integer workId, Integer uid, Long time) {
        Activity activity = activityQueryService.getByWorkId(workActivityId);
        if (activity != null) {
            userActionQueueService.push(UserActionQueueService.QueueParamDTO.builder()
                    .uid(uid)
                    .activityId(activity.getId())
                    .userActionType(UserActionTypeEnum.WORK)
                    .userAction(UserActionEnum.SUBMIT_WORK)
                    .identify(String.valueOf(workId))
                    .time(DateUtils.timestamp2Date(time))
                    .build());
        }
        return RestRespDTO.success();
    }

    @RequestMapping("work/delete")
    public RestRespDTO deleteWork(Integer workActivityId, Integer workId, Integer uid, Long time) {
        Activity activity = activityQueryService.getByWorkId(workActivityId);
        if (activity != null) {
            userActionQueueService.push(UserActionQueueService.QueueParamDTO.builder()
                    .uid(uid)
                    .activityId(activity.getId())
                    .userActionType(UserActionTypeEnum.WORK)
                    .userAction(UserActionEnum.DELETE_WORK)
                    .identify(String.valueOf(workId))
                    .time(DateUtils.timestamp2Date(time))
                    .build());
        }
        return RestRespDTO.success();
    }

}