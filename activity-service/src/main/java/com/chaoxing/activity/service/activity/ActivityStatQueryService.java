package com.chaoxing.activity.service.activity;

import com.chaoxing.activity.mapper.ActivityMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatQueryService
 * @description
 * @blame wwb
 * @date 2021-01-13 19:29:58
 */
@Slf4j
@Service
public class ActivityStatQueryService {

	@Resource
	private ActivityMapper activityMapper;

	/**机构参与的活动的pageId列表
	 * @Description
	 * @author wwb
	 * @Date 2021-01-13 19:20:40
	 * @param fids
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listOrgParticipatedActivityPageId(List<Integer> fids) {
		List<Integer> result = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(fids)) {
			List<Integer> pageIds = activityMapper.listOrgParticipatedActivityPageId(fids);
			result = pageIds.stream().filter(v -> v != null).collect(Collectors.toList());
		}
		return result;
	}

}
