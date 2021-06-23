package com.chaoxing.activity.api.controller.action;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.queue.SignActionQueueService;
import com.chaoxing.activity.service.queue.user.UserActionRecordQueueService;
import com.chaoxing.activity.util.DateUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**报名签到行为
 * @author wwb
 * @version ver 1.0
 * @className SignActionApiController
 * @description
 * 1、新增、删除报名
 * 2、新增、删除签到
 * @blame wwb
 * @date 2021-05-25 15:55:54
 */
@RestController
@RequestMapping("sign/action")
public class SignActionApiController {

    @Resource
    private SignActionQueueService signActionQueueService;

    /**新增报名行为
     * @Description 
     * @author wwb
     * @Date 2021-06-23 17:41:28
     * @param signId
     * @param signUpId
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("sign-up/add")
    public RestRespDTO addSignUp(@RequestParam Integer signId, @RequestParam Integer signUpId, @RequestParam Long time) {
        signActionQueueService.push(new SignActionQueueService.QueueParamDTO(signId, SignActionQueueService.SignActionEnum.ADD_SIGN_UP, String.valueOf(signUpId), DateUtils.timestamp2Date(time)));
        return RestRespDTO.success();
    }

    /**删除报名行为
     * @Description 
     * @author wwb
     * @Date 2021-06-23 17:41:39
     * @param signId
     * @param signUpId
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("sign-up/delete")
    public RestRespDTO deleteSignUp(@RequestParam Integer signId, @RequestParam Integer signUpId, @RequestParam Long time) {
        signActionQueueService.push(new SignActionQueueService.QueueParamDTO(signId, SignActionQueueService.SignActionEnum.DELETE_SIGN_UP, String.valueOf(signUpId), DateUtils.timestamp2Date(time)));
        return RestRespDTO.success();
    }

    /**新增签到行为
     * @Description 
     * @author wwb
     * @Date 2021-06-23 17:41:48
     * @param signId
     * @param signInId
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("sign-in/add")
    public RestRespDTO addSignIn(@RequestParam Integer signId, @RequestParam Integer signInId, @RequestParam Long time) {
        signActionQueueService.push(new SignActionQueueService.QueueParamDTO(signId, SignActionQueueService.SignActionEnum.ADD_SIGN_IN, String.valueOf(signInId), DateUtils.timestamp2Date(time)));
        return RestRespDTO.success();
    }

    /**删除签到行为
     * @Description 
     * @author wwb
     * @Date 2021-06-23 17:41:59
     * @param signId
     * @param signInId
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("sign-up/delete")
    public RestRespDTO deleteSignIn(@RequestParam Integer signId, @RequestParam Integer signInId, @RequestParam Long time) {
        signActionQueueService.push(new SignActionQueueService.QueueParamDTO(signId, SignActionQueueService.SignActionEnum.DELETE_SIGN_IN, String.valueOf(signInId), DateUtils.timestamp2Date(time)));
        return RestRespDTO.success();
    }

}
