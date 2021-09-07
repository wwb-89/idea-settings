package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.ActivityManageQueryDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.dto.query.MhActivityCalendarQueryDTO;
import com.chaoxing.activity.dto.stat.ActivityClassifyDTO;
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

	Page<Activity> pageErdosParticipate(@Param("page") Page<?> page, @Param("params") ActivityQueryDTO activityQuery);

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

	/**
	 * 查询创建的活动
	 *
	 * @param page
	 * @param activityManageQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2021-03-17 16:11:49
	 */
	Page<Activity> pageCreated(@Param("page") Page<?> page, @Param("params") ActivityManageQueryDTO activityManageQuery);

	/**
	 * 查询机构创建的或能参与的
	 *
	 * @param page
	 * @param fid
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2020-11-24 21:49:48
	 */
	Page<Activity> listOrgParticipatedOrCreated(@Param("page") Page<?> page, @Param("fid") Integer fid);

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
	 * 分页查询用户管理的
	 *
	 * @param page
	 * @param uid
	 * @param sw
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2021-04-08 18:01:23
	 */
	Page<Activity> pageUserManaged(@Param("page") Page<?> page, @Param("uid") Integer uid, @Param("sw") String sw);

	Page<Activity> pageUserMarketManaged(@Param("page") Page<?> page, @Param("uid") Integer uid, @Param("sw") String sw, @Param("marketId") Integer marketId);

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
	 * 活动日历查询（创建的活动）
	 *
	 * @param page
	 * @param activityCalendarQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2021-04-21 17:10:04
	 */
	Page<Activity> pageActivityCalendarCreated(@Param("page") Page<?> page, @Param("params") MhActivityCalendarQueryDTO activityCalendarQuery);

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
	 * 根据报名签到id列表查询
	 *
	 * @param signIds
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2021-01-27 20:24:30
	 */
	List<Activity> listByMarketSignIds(@Param("signIds") List<Integer> signIds, @Param("marketId") Integer marketId);

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

	Page<Activity> pageMarketCollectedActivityId(@Param("page") Page<?> page, @Param("uid") Integer uid, @Param("sw") String sw, @Param("marketId") Integer marketId);

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

	/**
	 * 根据id查询活动
	 *
	 * @param id
	 * @return com.chaoxing.activity.model.Activity
	 * @Description
	 * @author wwb
	 * @Date 2021-03-10 20:30:11
	 */
	Activity getById(@Param("id") Integer id);

	/**
	 * 查询机构创建的
	 *
	 * @param fid
	 * @param activityFlag
	 * @return java.util.List<com.chaoxing.activity.model.Activity>
	 * @Description
	 * @author wwb
	 * @Date 2021-04-19 10:41:04
	 */
	List<Activity> listOrgCreated(@Param("fid") Integer fid, @Param("activityFlag") String activityFlag);

	/**
	 * 查询活动进行日期范围中含activityDate的活动id
	 *
	 * @param activityDate
	 * @return java.util.List<java.lang.Integer>
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-10 17:24:10
	 */
	List<Integer> listByActivityDate(@Param("activityDate") String activityDate);

	/**
	 * 查询机构fid下的活动，并按照类型进行分组
	 *
	 * @param activityIds
	 * @return java.util.List<java.lang.Integer>
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-11 15:27:37
	 */
	List<ActivityClassifyDTO> listActivityGroupByClassifyId(@Param("activityIds") List<Integer> activityIds);

	/**
	 * 根据机构id, 给定的活动时间范围，查询在此范围内进行中的活动id列表
	 *
	 * @param fid
	 * @param startDate
	 * @param endDate
	 * @return java.util.List<java.lang.Integer>
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-10 10:06:36
	 */
	List<Integer> listOrgReleasedActivityIds(@Param("fid") Integer fid, @Param("startDate") String startDate, @Param("endDate") String endDate);

	/**
	 * 根据机构ids, 给定的活动时间范围，查询在此范围内进行中的活动id列表
	 *
	 * @param fids
	 * @param startDate
	 * @param endDate
	 * @return java.util.List<java.lang.Integer>
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-11 14:02:14
	 */
    List<Integer> listOrgsReleasedActivityId(@Param("fids") List<Integer> fids, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
	 * 鄂尔多斯定制的机构创建的作品征集id列表
	 *
	 * @param createdFid
	 * @param participateFid
	 * @param activityFlag
	 * @return java.util.List<java.lang.Integer>
	 * @Description
	 * @author wwb
	 * @Date 2021-09-07 20:49:01
	 */
	List<Integer> listErdosCustomOrgCreatedWorkId(@Param("createdFid") Integer createdFid, @Param("participateFid") Integer participateFid, @Param("activityFlag") String activityFlag);

}