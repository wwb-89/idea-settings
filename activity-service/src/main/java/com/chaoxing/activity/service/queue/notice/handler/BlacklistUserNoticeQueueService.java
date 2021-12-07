package com.chaoxing.activity.service.queue.notice.handler;

import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.service.queue.notice.BlacklistUserNoticeQueue;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**给黑名单的用户发送通知服务
 * @description:
 * @author: huxiaolong
 * @date: 2021/12/6 5:13 下午
 * @version: 1.0
 */
@Slf4j
@Service
public class BlacklistUserNoticeQueueService {

    @Resource
    private XxtNoticeApiService xxtNoticeApiService;


    public void handle(BlacklistUserNoticeQueue.QueueParamDTO queueParam) {
        xxtNoticeApiService.sendNotice(queueParam.getTitle(), queueParam.getContent(), "", CommonConstant.NOTICE_SEND_UID, queueParam.getUids());
    }

}
