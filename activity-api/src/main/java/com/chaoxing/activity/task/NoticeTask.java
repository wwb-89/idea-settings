package com.chaoxing.activity.task;

import com.chaoxing.activity.service.activity.ActivityStartNoticeHandleService;
import com.chaoxing.activity.service.manager.NoticeApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className NoticeTask
 * @description
 * @blame wwb
 * @date 2021-02-03 11:31:30
 */
@Slf4j
@Component
public class NoticeTask {

	@Resource
	private ActivityStartNoticeHandleService activityStartNoticeHandleService;
	@Resource
	private NoticeApiService noticeApiService;

	@Scheduled(fixedDelay = 1 * 1000)
	public void generateActivityStartNotice() {
		activityStartNoticeHandleService.consumeActivityNotice();
	}

	@Scheduled(fixedDelay = 1 * 1000)
	public void consumeNotice() {
		noticeApiService.consumeNotice();
	}

}
