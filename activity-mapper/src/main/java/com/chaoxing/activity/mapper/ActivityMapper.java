package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @className: ActivityMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

	/**查询参与的活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-13 13:47:29
	 * @param page
	 * @param activityQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	*/
	Page<Activity> listParticipate(Page<?> page, ActivityQueryDTO activityQuery);

}