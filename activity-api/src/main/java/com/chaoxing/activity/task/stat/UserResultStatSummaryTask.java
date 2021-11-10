package com.chaoxing.activity.task.stat;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.user.UserResultStatSummaryQueue;
import com.chaoxing.activity.service.stat.UserStatSummaryHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserResultStatSummaryTask
 * @description
 * @blame wwb
 * @date 2021-11-10 21:21:57
 */
@Slf4j
@Component
public class UserResultStatSummaryTask {

	@Resource
	private UserResultStatSummaryQueue userResultStatSummaryQueue;
	@Resource
	private UserStatSummaryHandleService userStatSummaryService;


	@Scheduled(fixedDelay = 1L)
	public void handle() throws InterruptedException {
		log.info("根据参数执行用户活动数据成绩汇总 start");
		UserResultStatSummaryQueue.QueueParamDTO queueParam = userResultStatSummaryQueue.pop();
		if (queueParam == null) {
			return;
		}
		try {
			userStatSummaryService.updateUserResult(queueParam.getUid(), queueParam.getActivityId());
		} catch (Exception e) {
			log.error("根据参数:{} 执行用户活动数据成绩汇总error:{}", JSON.toJSONString(queueParam), e.getMessage());
			e.printStackTrace();
			userResultStatSummaryQueue.push(queueParam);
		}
	}

}
