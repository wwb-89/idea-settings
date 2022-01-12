package com.chaoxing.activity.service.activity.collection;

import com.chaoxing.activity.model.ActivityCollection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityCollectionValidateService
 * @description
 * @blame wwb
 * @date 2021-01-28 20:53:31
 */
@Slf4j
@Service
public class ActivityCollectionValidateService {

	@Resource
	private ActivityCollectionQueryService activityCollectionQueryService;

	/**是否已收藏
	 * @Description
	 * @author wwb
	 * @Date 2021-01-28 20:49:22
	 * @param activityId
	 * @param uid
	 * @return boolean
	 */
	public boolean isCollected(Integer activityId, Integer uid) {
		ActivityCollection activityCollection = activityCollectionQueryService.get(activityId, uid);
		return activityCollection != null;
	}

}
