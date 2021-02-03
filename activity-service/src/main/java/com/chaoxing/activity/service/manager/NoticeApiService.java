package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className NoticeApiService
 * @description
 * @blame wwb
 * @date 2021-02-03 10:32:59
 */
@Slf4j
@Service
public class NoticeApiService {

	private static final String SEND_NOTICE_URL = "https://notice.chaoxing.com/apis/pp/notice_SendNotice";
	/** 每次最大发送数量（接收通知的人的数量） */
	private static final int EACH_MAX_SEND_NUM = 200;

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;
	@Resource
	private RedisTemplate redisTemplate;

	/**发送通知
	 * @Description 单次发送的接受者数量不要超过200
	 * 1、接受者少于200个则一次发送完成
	 * 2、接受者大于200则先生成消息再将任务推送到队列中执行
	 * @author wwb
	 * @Date 2020-12-14 20:01:30
	 * @param title
	 * @param content
	 * @param attachment
	 * @param senderUid
	 * @param receiverUids
	 * @return void
	 */
	public void sendNotice(String title, String content, String attachment, Integer senderUid, List<Integer> receiverUids) {
		Optional.ofNullable(content).filter(StringUtils::isNotBlank).orElseThrow(() -> new BusinessException("通知内容不能为空"));
		Optional.ofNullable(senderUid).orElseThrow(() -> new BusinessException("消息发送者不能为空"));
		Optional.ofNullable(receiverUids).filter(CollectionUtils::isNotEmpty).orElseThrow(() -> new BusinessException("接收消息的用户不能为空"));
		// 先生成消息
		int size = receiverUids.size();
		List<Integer> generateNoticeReceiverUids;
		List<Integer> remainIds;
		if (size > EACH_MAX_SEND_NUM) {
			// 取前200个用户来生成消息
			generateNoticeReceiverUids = receiverUids.subList(0, EACH_MAX_SEND_NUM);
			receiverUids.removeAll(generateNoticeReceiverUids);
			remainIds = receiverUids;
		} else {
			generateNoticeReceiverUids = receiverUids;
			remainIds = Lists.newArrayList();
		}
		try {
			long noticeId = generateNotice(title, content, attachment, senderUid, generateNoticeReceiverUids);
			if (CollectionUtils.isNotEmpty(remainIds)) {
				// 将信息存入redis定时发送信息
				List<List<Integer>> partition = Lists.partition(remainIds, EACH_MAX_SEND_NUM);
				for (List<Integer> integers : partition) {
					produceNotice(noticeId, title, content, attachment, senderUid, integers);
				}
			}
		} catch (BusinessException e) {
			log.error("发送通知 title::{},content:{},senderUid:{},receiverUids:{}, error:{}", title, content, senderUid, JSON.toJSONString(receiverUids), e.getMessage());
			throw e;
		}
	}

	/**消费通知
	 * @Description
	 * @author wwb
	 * @Date 2020-12-15 14:33:22
	 * @param
	 * @return void
	 */
	public void consumeNotice() {
		ListOperations<String, NoticeDTO> listOperations = redisTemplate.opsForList();
		String cacheKey = getNoticeQueueCacheKey();
		Long size = listOperations.size(cacheKey);
		while (size > 0) {
			NoticeDTO notice = listOperations.rightPop(cacheKey);
			size = listOperations.size(cacheKey);
			Long noticeId = notice.getId();
			try {
				if (noticeId != null) {
					sendNoticeByNoticeId(notice.getId(), notice.getTitle(), notice.getContent(), notice.getAttachment(), notice.getSenderUid(), notice.getReceiverUids());
				} else {
					sendNotice(notice.getTitle(), notice.getContent(), notice.getAttachment(), notice.getSenderUid(), notice.getReceiverUids());
				}
			} catch (Exception e) {
				e.printStackTrace();
				listOperations.leftPush(cacheKey, notice);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}
		}
	}

	/**生成通知
	 * @Description
	 * @author wwb
	 * @Date 2020-12-14 20:06:23
	 * @param title 通知标题
	 * @param content 通知内容
	 * @param attachment 附件
	 * @param senderUid 发送者id
	 * @param receiverUids 接收者id列表
	 * @return long 消息id
	 */
	private long generateNotice(String title, String content, String attachment, Integer senderUid, List<Integer> receiverUids) {
		Optional.ofNullable(content).filter(StringUtils::isNotBlank).orElseThrow(() -> new BusinessException("通知内容不能为空"));
		Optional.ofNullable(senderUid).orElseThrow(() -> new BusinessException("消息发送者不能为空"));
		Optional.ofNullable(receiverUids).filter(CollectionUtils::isNotEmpty).orElseThrow(() -> new BusinessException("接收消息的用户不能为空"));
		Optional.of(receiverUids.size()).filter(v -> v.compareTo(EACH_MAX_SEND_NUM) < 1).orElseThrow(() -> new BusinessException("每次发送的用户不能超过" + EACH_MAX_SEND_NUM));
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		Optional.ofNullable(title).filter(StringUtils::isNotBlank).ifPresent(v -> params.add("title", v));
		params.add("uid", senderUid);
		params.add("content", content);
		params.add("touids", String.join(",", receiverUids.stream().map(String::valueOf).collect(Collectors.toList())));
		params.add("source_type", 1000);
		params.add("attachment", attachment);
		HttpHeaders httpHeaders = new HttpHeaders();
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, httpHeaders);
		String result = restTemplate.postForObject(SEND_NOTICE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer code = jsonObject.getInteger("result");
		if (Objects.equals(code, 1)) {
			return jsonObject.getJSONObject("data").getLongValue("id");
		} else {
			String msg = jsonObject.getString("msg");
			throw new BusinessException(msg);
		}
	}

	/**根据通知id发送通知
	 * @Description
	 * @author wwb
	 * @Date 2020-12-15 14:27:39
	 * @param noticeId
	 * @param title
	 * @param content
	 * @param attachment
	 * @param senderUid
	 * @param receiverUids
	 * @return void
	 */
	private void sendNoticeByNoticeId(long noticeId, String title, String content, String attachment, Integer senderUid, List<Integer> receiverUids) {
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("noticeId", noticeId);
		Optional.ofNullable(title).filter(StringUtils::isNotBlank).ifPresent(v -> params.add("title", v));
		params.add("uid", senderUid);
		params.add("content", content);
		params.add("touids", String.join(",", receiverUids.stream().map(String::valueOf).collect(Collectors.toList())));
		params.add("source_type", 1000);
		params.add("attachment", attachment);
		HttpHeaders httpHeaders = new HttpHeaders();
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, httpHeaders);
		String result = restTemplate.postForObject(SEND_NOTICE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer code = jsonObject.getInteger("result");
		if (Objects.equals(code, 1)) {

		} else {
			String msg = jsonObject.getString("msg");
			log.error("发送通知 title::{},content:{},senderUid:{},receiverUids:{}, error:{}", title, content, senderUid, JSON.toJSONString(receiverUids), msg);
			produceNotice(noticeId, title, content, attachment, senderUid, receiverUids);
		}
	}

	/**生产通知
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-03 11:05:58
	 * @param title
	 * @param content
	 * @param attachment
	 * @param senderUid
	 * @param receiverUids
	 * @return void
	*/
	public void produceNotice(String title, String content, String attachment, Integer senderUid, List<Integer> receiverUids) {
		NoticeDTO notice = NoticeDTO.builder()
				.title(title)
				.content(content)
				.attachment(attachment)
				.senderUid(senderUid)
				.receiverUids(receiverUids)
				.build();
		ListOperations<String, NoticeDTO> listOperations = redisTemplate.opsForList();
		String cacheKey = getNoticeQueueCacheKey();
		listOperations.leftPush(cacheKey, notice);
	}
	/**生产通知
	 * @Description
	 * @author wwb
	 * @Date 2020-12-15 14:32:21
	 * @param noticeId
	 * @param title
	 * @param content
	 * @param attachment
	 * @param senderUid
	 * @param receiverUids
	 * @return void
	 */
	private void produceNotice(long noticeId, String title, String content, String attachment, Integer senderUid, List<Integer> receiverUids) {
		NoticeDTO notice = NoticeDTO.builder()
				.id(noticeId)
				.title(title)
				.content(content)
				.attachment(attachment)
				.senderUid(senderUid)
				.receiverUids(receiverUids)
				.build();
		ListOperations<String, NoticeDTO> listOperations = redisTemplate.opsForList();
		String cacheKey = getNoticeQueueCacheKey();
		listOperations.leftPush(cacheKey, notice);
	}

	private String getNoticeQueueCacheKey() {
		return CacheConstant.QUEUE_CACHE_KEY_PREFIX + "notice";
	}

}
