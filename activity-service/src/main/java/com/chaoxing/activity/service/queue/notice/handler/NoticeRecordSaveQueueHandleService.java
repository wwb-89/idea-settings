package com.chaoxing.activity.service.queue.notice.handler;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.NoticeRecord;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.notice.record.NoticeRecordHandleService;
import com.chaoxing.activity.service.queue.notice.NoticeRecordSaveQueue;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**通知记录保存队列服务
 * @author wwb
 * @version ver 1.0
 * @className NoticeRecordSaveQueueHandleService
 * @description
 * @blame wwb
 * @date 2022-02-11 17:25:04
 */
@Slf4j
@Service
public class NoticeRecordSaveQueueHandleService {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private NoticeRecordHandleService noticeRecordHandleService;

	public void handle(NoticeRecordSaveQueue.QueueParamDTO queueParamDto) {
		Integer activityId = Optional.ofNullable(queueParamDto).map(NoticeRecordSaveQueue.QueueParamDTO::getActivityId).orElse(null);
		if (activityId == null) {
			return;
		}
		Activity activity = activityQueryService.getById(activityId);
		if (activity == null) {
			return;
		}
		NoticeRecord noticeRecord = NoticeRecord.builder()
				.type(queueParamDto.getType().getValue())
				.activityId(activityId)
				.activityCreateFid(activity.getCreateFid())
				.activityFlag(activity.getActivityFlag())
				.content(queueParamDto.getContent())
				.time(DateUtils.startTimestamp2Time(queueParamDto.getTimestamp()))
				.build();
		noticeRecordHandleService.add(noticeRecord);
	}

}