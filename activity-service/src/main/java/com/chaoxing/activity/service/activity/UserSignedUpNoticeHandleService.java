package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

/**用户报名成功通知处理程序
 * @author wwb
 * @version ver 1.0
 * @className UserSignedUpNoticeHandleService
 * @description
 * 活动开始前一天需要发送通知
 * 通知对象：已报名活动的用户，已收藏活动的用户（且活动不需要报名）
 * @blame wwb
 * @date 2021-02-02 17:15:40
 */
@Slf4j
@Service
public class UserSignedUpNoticeHandleService {

	/** 活动时间格式化 */
	private static final DateTimeFormatter ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");

	@Resource
	private XxtNoticeApiService noticeApiService;


	/**用户报名成功发送通知
	 * @Description 
	 * @author wwb
	 * @Date 2021-09-01 20:27:14
	 * @param activity
	 * @param uid
	 * @return void
	*/
	public void userSignedUpNotice(Activity activity, Integer uid) {
		String name = activity.getName();
		String previewUrl = activity.getPreviewUrl();
		String title = generateUserSignedUpNoticeTitle(activity);
		String content = generateUserSignedUpNoticeContent(activity);
		noticeApiService.sendNotice(title, content, NoticeDTO.generateAttachment(name, previewUrl), CommonConstant.NOTICE_SEND_UID, new ArrayList(){{add(uid);}});
	}

	private String generateUserSignedUpNoticeTitle(Activity activity) {
		return "成功报名活动 " + activity.getName();
	}

	private String generateUserSignedUpNoticeContent(Activity activity) {
		String content = "您好，您已成功报名活动！\n";
		content += "活动名称：" + activity.getName() + "\n";
		String address = Optional.ofNullable(activity.getAddress()).filter(StringUtils::isNotBlank).orElse("") + Optional.ofNullable(activity.getDetailAddress()).filter(StringUtils::isNotBlank).orElse("");
		if (StringUtils.isNotBlank(address)) {
			content += "活动地点：" + address + "\n";
		}
		content += "会议时间：" + activity.getStartTime().format(ACTIVITY_TIME_FORMATTER) + "- " + activity.getEndTime().format(ACTIVITY_TIME_FORMATTER) + "\n";
		return content;
	}


}