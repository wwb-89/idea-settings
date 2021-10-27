package com.chaoxing.activity.api.controller.action;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.event.SignInAddEventOrigin;
import com.chaoxing.activity.dto.event.SignInDeletedEventOrigin;
import com.chaoxing.activity.dto.event.SignUpAddEventOrigin;
import com.chaoxing.activity.dto.event.SignUpDeletedEventOrigin;
import com.chaoxing.activity.service.queue.event.SignInAddEventQueue;
import com.chaoxing.activity.service.queue.event.SignInDeletedEventQueue;
import com.chaoxing.activity.service.queue.event.SignUpAddEventQueue;
import com.chaoxing.activity.service.queue.event.SignUpDeletedEventQueue;
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
    private SignUpAddEventQueue signUpAddEventQueue;
    @Resource
    private SignUpDeletedEventQueue signUpDeletedEventQueue;
    @Resource
    private SignInAddEventQueue signInAddEventQueue;
    @Resource
    private SignInDeletedEventQueue signInDeletedEventQueue;

    /**添加报名
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
        SignUpAddEventOrigin eventOrigin = SignUpAddEventOrigin.builder()
                .signId(signId)
                .signUpId(signUpId)
                .timestamp(time)
                .build();
        signUpAddEventQueue.push(eventOrigin);
        return RestRespDTO.success();
    }

    /**删除报名
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
        SignUpDeletedEventOrigin eventOrigin = SignUpDeletedEventOrigin.builder()
                .signId(signId)
                .signUpId(signUpId)
                .timestamp(time)
                .build();
        signUpDeletedEventQueue.push(eventOrigin);
        return RestRespDTO.success();
    }

    /**添加签到
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
        SignInAddEventOrigin eventOrigin = SignInAddEventOrigin.builder()
                .signId(signId)
                .signInId(signInId)
                .timestamp(time)
                .build();
        signInAddEventQueue.push(eventOrigin);
        return RestRespDTO.success();
    }

    /**删除签到
     * @Description 
     * @author wwb
     * @Date 2021-06-23 17:41:59
     * @param signId
     * @param signInId
     * @param time
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("sign-in/delete")
    public RestRespDTO deleteSignIn(@RequestParam Integer signId, @RequestParam Integer signInId, @RequestParam Long time) {
        SignInDeletedEventOrigin eventOrigin = SignInDeletedEventOrigin.builder()
                .signId(signId)
                .signInId(signInId)
                .timestamp(time)
                .build();
        signInDeletedEventQueue.push(eventOrigin);
        return RestRespDTO.success();
    }

}
