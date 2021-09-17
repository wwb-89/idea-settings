package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.manager.CloudApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动封面地址同步服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityCoverUrlSyncService
 * @description 根据活动封面的云盘id获取封面的地址
 * @blame wwb
 * @date 2021-01-20 10:40:27
 */
@Slf4j
@Service
public class ActivityCoverUrlSyncService {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityHandleService activityHandleService;
	@Resource
	private CloudApiService cloudApiService;

	/**同步活动封面url
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 16:21:21
	 * @param activityId
	 * @return boolean
	*/
	public boolean syncActivityCoverUrl(Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		if (activity != null) {
			String cloudId = activity.getCoverCloudId();
			if (StringUtils.isNotBlank(cloudId)) {
				String imageUrl = cloudApiService.getImageUrl(cloudId);
				if (StringUtils.isBlank(imageUrl)) {
					return false;
				} else {
					activityHandleService.updateActivityCoverUrl(activityId, imageUrl);
				}
			}
		}
		return true;
	}

	/**获取封面url
	 * @Description
	 * @author wwb
	 * @Date 2021-01-20 11:24:34
	 * @param activity
	 * @return java.lang.String
	*/
	public String getCoverUrl(Activity activity) {
		String coverUrl = activity.getCoverUrl();
		if (StringUtils.isNotBlank(coverUrl)) {
			return coverUrl;
		}
		String coverCloudId = activity.getCoverCloudId();
		if (StringUtils.isNotBlank(coverCloudId)) {
			return cloudApiService.buildImageUrl(coverCloudId);
		}
		return "";
	}

}