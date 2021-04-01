package com.chaoxing.activity.service.event;

import com.chaoxing.activity.service.queue.UserRatingQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户评价变更事件服务
 * @author wwb
 * @version ver 1.0
 * @className UserRatingChangeEventService
 * @description
 * @blame wwb
 * @date 2021-03-26 18:13:44
 */
@Slf4j
@Service
public class UserRatingChangeEventService {

	@Resource
	private UserRatingQueueService userRatingQueueService;

	/**用户评价变更
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 18:15:22
	 * @param uid
	 * @param signId
	 * @return void
	*/
	public void change(Integer uid, Integer signId) {
		userRatingQueueService.add(uid, signId);
	}

}