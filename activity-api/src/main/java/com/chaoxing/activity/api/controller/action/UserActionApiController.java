package com.chaoxing.activity.api.controller.action;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.queue.user.UserActionQueueService;
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
    private UserActionQueueService userActionQueueService;
    @Resource
    private ActivityQueryService activityQueryService;

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
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            userActionQueueService.push(new UserActionQueueService.QueueParamDTO(uid, activity.getId(), UserActionTypeEnum.SIGN_UP, UserActionEnum.SIGNED_UP, String.valueOf(signUpId), DateUtils.timestamp2Date(time)));
        }
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
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            new UserActionQueueService.QueueParamDTO(uid, activity.getId(), UserActionTypeEnum.SIGN_UP, UserActionEnum.CANCEL_SIGNED_UP, String.valueOf(signUpId), DateUtils.timestamp2Date(time));
        }
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
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            new UserActionQueueService.QueueParamDTO(uid, activity.getId(), UserActionTypeEnum.SIGN_IN, UserActionEnum.SIGNED_IN, String.valueOf(signInId), DateUtils.timestamp2Date(time));
        }
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
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            new UserActionQueueService.QueueParamDTO(uid, activity.getId(), UserActionTypeEnum.SIGN_IN, UserActionEnum.CANCEL_SIGNED_IN, String.valueOf(signInId), DateUtils.timestamp2Date(time));
        }
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
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            new UserActionQueueService.QueueParamDTO(uid, activity.getId(), UserActionTypeEnum.SIGN_IN, UserActionEnum.LEAVE_SIGNED_IN, String.valueOf(signInId), DateUtils.timestamp2Date(time));
        }
        return RestRespDTO.success();
    }

    /**用户提交作品
     * @Description 
     * @author wwb
     * @Date 2021-06-23 16:09:05
     * @param workActivityId
     * @param workId
     * @param uid
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("work/add")
    public RestRespDTO submitWork(Integer workActivityId, Integer workId, Integer uid, Long time) {
        Activity activity = activityQueryService.getByWorkId(workActivityId);
        if (activity != null) {
            userActionQueueService.push(new UserActionQueueService.QueueParamDTO(uid, activity.getId(), UserActionTypeEnum.WORK, UserActionEnum.SUBMIT_WORK, String.valueOf(workId), DateUtils.timestamp2Date(time)));
        }
        return RestRespDTO.success();
    }

    /**用户删除作品
     * @Description 
     * @author wwb
     * @Date 2021-06-23 16:09:16
     * @param workActivityId
     * @param workId
     * @param uid
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("work/delete")
    public RestRespDTO deleteWork(Integer workActivityId, Integer workId, Integer uid, Long time) {
        Activity activity = activityQueryService.getByWorkId(workActivityId);
        if (activity != null) {
            userActionQueueService.push(new UserActionQueueService.QueueParamDTO(uid, activity.getId(), UserActionTypeEnum.WORK, UserActionEnum.DELETE_WORK, String.valueOf(workId), DateUtils.timestamp2Date(time)));
        }
        return RestRespDTO.success();
    }

}