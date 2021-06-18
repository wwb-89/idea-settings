package com.chaoxing.activity.api.controller.action;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.queue.UserActionDetailQueueService;
import com.chaoxing.activity.service.queue.UserActionQueueService;
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

    @RequestMapping("sign-up-change")
    public RestRespDTO signUpChange(Integer uid, Integer signId) {
        userSignActionQueueService.addUserSignUpAction(UserActionQueueService.QueueParamDTO.builder().uid(uid).signId(signId).build());
        return RestRespDTO.success();
    }

    @RequestMapping("sign-in-change")
    public RestRespDTO signedIn(Integer uid, Integer signId) {
        userSignActionQueueService.addUserSignInAction(UserActionQueueService.QueueParamDTO.builder().uid(uid).signId(signId).build());
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