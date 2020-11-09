package com.chaoxing.activity.service;

import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityService
 * @description
 * @blame wwb
 * @date 2020-11-09 19:58:28
 */
@Service
public class ActivityService {

	@Resource
	private ActivityMapper activityMapper;

	public List<Activity> list() {
		return activityMapper.selectList(null);
	}

	public Integer count() {
		return activityMapper.count();
	}

}