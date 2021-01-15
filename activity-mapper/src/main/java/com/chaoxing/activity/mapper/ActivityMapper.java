package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.ActivityManageQueryDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.dto.query.MhActivityCalendarQueryDTO;
import com.chaoxing.activity.model.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

	/**
	 * 查询创建的
	 *
	 * @param page
	 * @param fid
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2020-11-24 21:49:48
	 */
	Page<Activity> listCreated(@Param("page") Page<?> page, @Param("fid") Integer fid);

	/**
	 * 活动日历查询
	 *
	 * @param page
	 * @param activityCalendarQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2020-12-03 15:59:54
	 */
	Page<Activity> listActivityCalendarParticipate(@Param("page") Page<?> page, @Param("params") MhActivityCalendarQueryDTO activityCalendarQuery);

	/**机构参与的活动的pageId列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-13 19:22:05
	 * @param fids
	 * @return java.util.List<java.lang.Integer>
	*/
	List<Integer> listOrgParticipatedActivityPageId(@Param("fids") List<Integer> fids);

}