package com.chaoxing.activity.task;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.service.queue.notice.XxtNoticeQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**学习通通知任务
 * @author wwb
 * @version ver 1.0
 * @className NoticeTask
 * @description
 * @blame wwb
 * @date 2021-02-03 11:31:30
 */
@Slf4j
@Component
public class XxtNoticeTask {

	@Resource
	private XxtNoticeQueue xxtNoticeQueueService;
	@Resource
	private XxtNoticeApiService xxtNoticeApiService;

	/**发送通知
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 18:26:10
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 10L)
	public void sendNotice() throws InterruptedException {
		NoticeDTO notice = xxtNoticeQueueService.get();
		if (notice == null) {
			return;
		}
		try {
			xxtNoticeApiService.sendNotice(notice);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("发送学习通通知:{} error", JSON.toJSONString(notice), e.getMessage());
			xxtNoticeQueueService.add(notice);
		}
	}

}
