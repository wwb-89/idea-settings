package com.chaoxing.activity.api.controller.action;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.queue.SignActionQueueService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**报名签到行为
 * @author wwb
 * @version ver 1.0
 * @className SignActionApiController
 * @description
 * @blame wwb
 * @date 2021-05-25 15:55:54
 */
@RestController
@RequestMapping("sign/action")
public class SignActionApiController {

    @Resource
    private SignActionQueueService signActionQueueService;

    @RequestMapping("sign-in-num-change")
    public RestRespDTO signInNumChanage(Integer signId) {
        signActionQueueService.addSignInNumChangeAction(signId);
        return RestRespDTO.success();
    }

}
