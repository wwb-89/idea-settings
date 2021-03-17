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
	Page<Activity> pageParticipate(@Param("page") Page<?> page, @Param("params") ActivityQueryDTO activityQuery);

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
	Page<Activity> pageManaging(@Param("page") Page<?> page, @Param("params") ActivityManageQueryDTO activityManageQuery);

	/**查询创建的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-17 16:11:49
	 * @param page
	 * @param activityManageQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	*/
	Page<Activity> pageCreated(@Param("page") Page<?> page, @Param("params") ActivityManageQueryDTO activityManageQuery);

	/**
	 * 查询机构创建的
	 *
	 * @param page
	 * @param fid
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2020-11-24 21:49:48
	 */
	Page<Activity> pageOrgCreated(@Param("page") Page<?> page, @Param("fid") Integer fid);

	/**
	 * 分页查询用户创建的
	 *
	 * @param page
	 * @param uid
	 * @param sw
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2021-01-27 21:05:26
	 */
	Page<Activity> pageUserCreated(@Param("page") Page<?> page, @Param("uid") Integer uid, @Param("sw") String sw);

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
	Page<Activity> pageActivityCalendarParticipate(@Param("page") Page<?> page, @Param("params") MhActivityCalendarQueryDTO activityCalendarQuery);

	/**
	 * 机构参与的活动的pageId列表
	 *
	 * @param fids
	 * @return java.util.List<java.lang.Integer>
	 * @Description
	 * @author wwb
	 * @Date 2021-01-13 19:22:05
	 */
	List<Integer> listOrgParticipatedActivityPageId(@Param("fids") List<Integer> fids);

	/**
	 * 根据报名签到id列表查询
	 *
	 * @param signIds
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2021-01-27 20:24:30
	 */
	List<Activity> listBySignIds(@Param("signIds") List<Integer> signIds);

	/**
	 * 分页查询报名的活动列表
	 *
	 * @param page
	 * @param uid
	 * @param sw
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2021-01-27 20:55:30
	 */
	Page<Activity> pageCollectedActivityId(@Param("page") Page<?> page, @Param("uid") Integer uid, @Param("sw") String sw);

	/**
	 * 根据报名签到id查询活动
	 *
	 * @param signId
	 * @return com.chaoxing.activity.model.Activity
	 * @Description
	 * @author wwb
	 * @Date 2021-03-10 19:20:28
	 */
	Activity getBySignId(@Param("signId") Integer signId);

	/**根据id查询活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-10 20:30:11
	 * @param id
	 * @return com.chaoxing.activity.model.Activity
	*/
	Activity getById(@Param("id") Integer id);

}