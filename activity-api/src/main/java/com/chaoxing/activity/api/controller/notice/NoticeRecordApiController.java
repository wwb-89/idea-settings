package com.chaoxing.activity.api.controller.notice;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.NoticeRecord;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.notice.record.NoticeRecordHandleService;
import com.chaoxing.activity.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**通知记录api
 * @author wwb
 * @version ver 1.0
 * @className NoticeRecordApiController
 * @description
 * @blame wwb
 * @date 2022-02-12 10:19:15
 */
@Slf4j
@RestController
@RequestMapping("notice/record")
public class NoticeRecordApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private NoticeRecordHandleService noticeRecordHandleService;

	/**记录报名通知
	 * @Description 
	 * @author wwb
	 * @Date 2022-02-12 10:21:14
	 * @param activityId
	 * @param content
	 * @param timestamp
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("sign-up/save")
	public RestRespDTO recordSignUpNotice(@RequestParam Integer activityId, @RequestParam String content, @RequestParam Long timestamp) {
		Activity activity = activityQueryService.getById(activityId);
		if (activity != null) {
			NoticeRecord noticeRecord = NoticeRecord.builder()
					.type(NoticeRecord.TypeEnum.SIGN_UP.getValue())
					.activityId(activityId)
					.activityCreateFid(activity.getCreateFid())
					.activityFlag(activity.getActivityFlag())
					.content(content)
					.time(DateUtils.startTimestamp2Time(timestamp))
					.build();
			noticeRecordHandleService.add(noticeRecord);
		}
		return RestRespDTO.success();
	}

}