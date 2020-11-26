package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.ActivityManageQueryDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.model.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: ActivityMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

	/**
	 * 查询参与的活动
	 *
	 * @param page
	 * @param activityQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2020-11-13 13:47:29
	 */
	Page<Activity> listParticipate(@Param("page") Page<?> page, @Param("params") ActivityQueryDTO activityQuery);

	/**
	 * 查询管理的活动
	 *
	 * @param page
	 * @param activityManageQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2020-11-18 14:25:42
	 */
	Page<Activity> listManaging(@Param("page") Page<?> page, @Param("params") ActivityManageQueryDTO activityManageQuery);

	/**查询创建的
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 21:49:48
	 * @param page
	 * @param fid
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	*/
	Page<Activity> listCreated(@Param("page") Page<?> page, @Param("fid") Integer fid);

}