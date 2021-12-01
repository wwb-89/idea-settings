package com.chaoxing.activity.task.stat;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.user.UserSignStatSummaryQueue;
import com.chaoxing.activity.service.stat.UserStatSummaryHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserSignStatSummaryTask
 * @description
 * @blame wwb
 * @date 2021-11-10 21:21:39
 */
@Slf4j
@Component
public class UserSignStatSummaryTask {

	@Resource
	private UserSignStatSummaryQueue userSignStatSummaryQueue;
	@Resource
	private UserStatSummaryHandleService userStatSummaryService;

	@Scheduled(fixedDelay = 10L)
	public void handle() throws InterruptedException {
		log.info("根据参数执行用户活动数据报名签到汇总 start");
		UserSignStatSummaryQueue.QueueParamDTO queueParam = userSignStatSummaryQueue.pop();
		if (queueParam == null) {
			return;
		}
		try {
			userStatSummaryService.updateUserSignData(queueParam.getUid(), queueParam.getActivityId());
		} catch (Exception e) {
			log.error("根据参数:{} 执行用户活动数据签到报名汇总error:{}", JSON.toJSONString(queueParam), e.getMessage());
			e.printStackTrace();
			userSignStatSummaryQueue.push(queueParam);
		}
	}

}
