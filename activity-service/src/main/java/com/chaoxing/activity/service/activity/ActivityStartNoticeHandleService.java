package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.dto.manager.sign.SignUp;
import com.chaoxing.activity.dto.module.SignAddEditDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.manager.NoticeApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**活动通开始知处理服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityStartNoticeHandleService
 * @description
 * 活动开始前一天需要发送通知
 * 通知对象：已报名活动的用户，已收藏活动的用户（且活动不需要报名）
 * @blame wwb
 * @date 2021-02-02 17:15:40
 */
@Slf4j
@Service
public class ActivityStartNoticeHandleService {

	/** 活动开始时通知的缓存key */
	private static final String ACTIVITY_START_NOTICE_CACHE_KEY = CacheConstant.CACHE_KEY_PREFIX + "activity_start_notice_queue";
	/** 活动时间格式化 */
	private static final DateTimeFormatter ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");
	/** 报名时间格式化 */
	private static final DateTimeFormatter SIGN_UP_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日HH:mm");

	@Resource
	private RedisTemplate redisTemplate;

	@Resource
	private SignApiService signApiService;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityCollectionQueryService activityCollectionQueryService;
	@Resource
	private NoticeApiService noticeApiService;

	/**订阅活动开始前通知发送
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-02 17:30:54
	 * @param activityId
	 * @param startTime
	 * @return void
	*/
	public void subscibeActivityNotice(Integer activityId, LocalDateTime startTime) {
		LocalDateTime now = LocalDateTime.now();
		long nowTimestamp = DateUtils.date2Timestamp(now);
		long startTimestamp = DateUtils.date2Timestamp(startTime);
		if (startTimestamp - nowTimestamp < CommonConstant.ACTIVITY_NOTICE_TIME_MILLISECOND) {
			// 小于通知阈值不处理
			return;
		}
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		// 开始前多少时间发送通知
		zSetOperations.add(ACTIVITY_START_NOTICE_CACHE_KEY, activityId, startTimestamp - CommonConstant.ACTIVITY_NOTICE_TIME_MILLISECOND);
	}

	/**取消订阅活动开始前通知发送
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-03 16:31:50
	 * @param activityId
	 * @return void
	*/
	public void cancelSubscibeActivityNotice(Integer activityId) {
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.remove(ACTIVITY_START_NOTICE_CACHE_KEY, activityId);
	}

	/**消费活动开始通知
	 * @Description 将通知存入通知队列中
	 * @author wwb
	 * @Date 2021-02-03 11:12:15
	 * @param 
	 * @return void
	*/
	public void consumeActivityNotice() {
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		Set<ZSetOperations.TypedTuple<Integer>> typedTuples = zSetOperations.rangeByScoreWithScores(ACTIVITY_START_NOTICE_CACHE_KEY, 0, Long.MAX_VALUE, 0, 10);
		Iterator<ZSetOperations.TypedTuple<Integer>> iterator = typedTuples.iterator();
		while (iterator.hasNext()) {
			// 只有一条数据
			ZSetOperations.TypedTuple<Integer> typedTuple = iterator.next();
			Integer activityId = typedTuple.getValue();
			Double score = typedTuple.getScore();
			long noticeTimestamp = score.longValue();
			LocalDateTime now = LocalDateTime.now();
			long nowTimestamp = DateUtils.date2Timestamp(now);
			if (noticeTimestamp <= nowTimestamp) {
				// 处理通知， 已报名的和已收藏（活动不需要报名的）
				sendActivityNotice(activityId);
			}
		}
	}

	private void sendActivityNotice(Integer activityId) {
		// 发送通知
		Activity activity = activityQueryService.getById(activityId);
		Integer signId = activity.getSignId();
		if (signId != null) {
			// 报名的uid列表
			List<Integer> signedUpUids = signApiService.listSignedUpUid(signId);
			generateSignedUpNotice(activity, signedUpUids);
		}
		// 收藏的uid列表
		List<Integer> collectedUids = activityCollectionQueryService.listCollectedUid(activityId);
		generateCollectNotice(activity, collectedUids);
		// 删除缓存
		ZSetOperations<String, Integer> zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.remove(ACTIVITY_START_NOTICE_CACHE_KEY, activityId);
	}

	/**是否直接发送活动开始的通知
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-05 15:59:25
	 * @param activity
	 * @return boolean
	*/
	private boolean isRedirectSendNotice(Activity activity) {
		LocalDateTime startTime = activity.getStartTime();
		LocalDateTime now = LocalDateTime.now();
		long startTimestamp = DateUtils.date2Timestamp(startTime);
		long nowTimestamp = DateUtils.date2Timestamp(now);
		// 如果时间阈值以外或活动一开始则不发送
		if (startTimestamp - nowTimestamp >= CommonConstant.ACTIVITY_NOTICE_TIME_MILLISECOND || now.isAfter(startTime)) {
			return false;
		}
		return true;
	}
	/**报名发送通知
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-05 15:55:38
	 * @param activity
	 * @param uids
	 * @return void
	*/
	public void sendSignedUpNotice(Activity activity, List<Integer> uids) {
		if (isRedirectSendNotice(activity)) {
			generateSignedUpNotice(activity, uids);
		}
	}
	/**生成报名通知
	 * @Description 活动通知阈值时间外不需要处理（批量处理）
	 * @author wwb
	 * @Date 2021-02-03 11:15:45
	 * @param activity
	 * @param uids
	 * @return void
	 */
	private void generateSignedUpNotice(Activity activity, List<Integer> uids) {
		if (isActivityStarted(activity)) {
			return;
		}
		if (CollectionUtils.isEmpty(uids)) {
			return;
		}
		String name = activity.getName();
		String previewUrl = activity.getPreviewUrl();
		String title = generateSignedUpNoticeTitle(activity);
		String content = generateSignedUpNoticeContent(activity);
		for (Integer signedUpUid : uids) {
			noticeApiService.produceNotice(title, content, NoticeDTO.generateAttachment(name, previewUrl), CommonConstant.NOTICE_SEND_UID, new ArrayList(){{add(signedUpUid);}});
		}
	}

	/**通知已收藏
	 * @Description
	 * @author wwb
	 * @Date 2021-02-05 16:23:47
	 * @param activity
	 * @param uids
	 * @return void
	*/
	public void noticeCollected(Activity activity, List<Integer> uids) {
		if (isRedirectSendNotice(activity)) {
			generateCollectNotice(activity, uids);
		}
		signApiService.noticeCollected(activity.getSignId(), uids);
	}
	/**生成收藏通知
	 * @Description 活动通知阈值时间外不需要处理（批量处理）
	 * @author wwb
	 * @Date 2021-02-03 11:15:58
	 * @param activity
	 * @param uids
	 * @return void
	 */
	private void generateCollectNotice(Activity activity, List<Integer> uids) {
		if (isActivityStarted(activity)) {
			return;
		}
		if (CollectionUtils.isEmpty(uids)) {
			return;
		}
		String name = activity.getName();
		String previewUrl = activity.getPreviewUrl();
		Integer signId = activity.getSignId();
		SignUp signUp = null;
		if (signId != null) {
			SignAddEditDTO signAddEditDTO = signApiService.getById(signId);
			signUp = signAddEditDTO.getSignUp();
		}
		String title = generateCollectedNoticeTitle(activity);
		String content = generateCollectedNoticeContent(activity, signUp);
		for (Integer collectedUid : uids) {
			noticeApiService.produceNotice(title, content, NoticeDTO.generateAttachment(name, previewUrl), CommonConstant.NOTICE_SEND_UID, new ArrayList(){{add(collectedUid);}});
		}
	}

	/**活动是否已开始
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-05 16:01:58
	 * @param activity
	 * @return boolean
	*/
	private boolean isActivityStarted(Activity activity) {
		LocalDateTime startTime = activity.getStartTime();
		LocalDateTime now = LocalDateTime.now();
		if (now.isAfter(startTime)) {
			// 活动已经开始，不需要发送通知
			return true;
		}
		return false;
	}

	private String generateSignedUpNoticeTitle(Activity activity) {
		return activity.getName() + "即将开始！";
	}

	private String generateSignedUpNoticeContent(Activity activity) {
		String content = "活动名称：" + activity.getName() + "\n" +
				"活动时间："+ activity.getStartTime().format(ACTIVITY_TIME_FORMATTER) +"- "+ activity.getEndTime().format(ACTIVITY_TIME_FORMATTER) + "\n";
		return content;
	}

	private String generateCollectedNoticeTitle(Activity activity) {
		return "您收藏的" + activity.getName() + "即将开始！";
	}

	private String generateCollectedNoticeContent(Activity activity, SignUp signUp) {
		String content = "活动名称：" + activity.getName() + "\n" +
				"活动时间："+ activity.getStartTime().format(ACTIVITY_TIME_FORMATTER) +"- "+ activity.getEndTime().format(ACTIVITY_TIME_FORMATTER) + "\n";
		if (signUp != null) {
			content += "报名时间："+ signUp.getStartTime().format(SIGN_UP_TIME_FORMATTER) +"- "+ signUp.getEndTime().format(SIGN_UP_TIME_FORMATTER) + "\n";
		}
		return content;
	}

}