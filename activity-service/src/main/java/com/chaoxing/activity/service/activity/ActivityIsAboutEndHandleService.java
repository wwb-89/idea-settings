package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**活动结束通知处理服务
 * @author huxiaolong
 * @version ver 1.0
 * @className ActivityIsAboutEndHandleService
 * @description
 * 已开启评价的活动结束时需要发送通知
 * 通知对象：已报名活动的、且未评价活动的用户
 * @blame wwb
 * @date 2021-05-14 10:14:33
 */
@Slf4j
@Service
public class ActivityIsAboutEndHandleService {

	/** 活动时间格式化 */
	private static final DateTimeFormatter ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");

	@Resource
	private ActivityQueryService activityQueryService;

	@Resource
	private XxtNoticeApiService noticeApiService;

	public boolean sendActivityIsAboutEndNotice(Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		// 再次判断活动是否已经结束，且活动是否开启评价
		if (isActivityEnded(activity) && activity.getOpenRating()) {
			// 查询已报名却未评价的用户ids
			List<Integer> signedUpUids = activityQueryService.listNoRateSignedUpUid(activity);

			generateRatingNotice(activity, signedUpUids);

			return true;
		}
		return false;
	}

	
	/**生成去评价通知
	* @Description 
	* @author huxiaolong
	* @Date 2021-05-14 10:07:39
	* @param activity
	* @param signedUpUids
	* @return void
	*/
	public void generateRatingNotice(Activity activity, List<Integer> signedUpUids) {
		if (CollectionUtils.isEmpty(signedUpUids)) {
			return;
		}
		String activityName = activity.getName();
		String previewUrl = activity.getPreviewUrl();
		String title = generateActivityEndNoticeTitle(activity);
		String content = generateActivityEndNoticeContent(activity);
		for (Integer signedUpUid : signedUpUids) {
			noticeApiService.sendNotice(title, content, NoticeDTO.generateAttachment(activityName, previewUrl), CommonConstant.NOTICE_SEND_UID, new ArrayList(){{add(signedUpUid);}});
		}
	}


	/**活动是否已结束
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-05 16:01:58
	 * @param activity
	 * @return boolean
	*/
	private boolean isActivityEnded(Activity activity) {
		LocalDateTime startTime = activity.getStartTime();
		LocalDateTime now = LocalDateTime.now();
		if (now.isAfter(startTime)) {
			// 活动已经开始，不需要发送通知
			return true;
		}
		return false;
	}

	private String generateActivityEndNoticeTitle(Activity activity) {
		return "活动评价：" + activity.getName();
	}

	private String generateActivityEndNoticeContent(Activity activity) {
		return "你好，感谢参与，请给本次活动评分" + "\n" +
				"活动名称：" + activity.getName() + "\n" +
				"活动时间：" + activity.getStartTime().format(ACTIVITY_TIME_FORMATTER) + "- " + activity.getEndTime().format(ACTIVITY_TIME_FORMATTER) + "\n" +
				"【评价入口卡片，标题为：去评价】" + "\n";
	}
}