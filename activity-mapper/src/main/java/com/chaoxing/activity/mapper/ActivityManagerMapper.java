package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.model.ActivityManager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ActivityManagerMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-03-18 10:30:33
 * @version: ver 1.0
 */
@Mapper
public interface ActivityManagerMapper extends BaseMapper<ActivityManager> {

	/**
	 * 分页查询管理员
	 *
	 * @param page
	 * @param activityId
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.ActivityManager>
	 * @Description
	 * @author wwb
	 * @Date 2021-03-27 12:36:00
	 */
	Page<ActivityManager> paging(@Param("page") Page<ActivityManager> page, @Param("activityId") Integer activityId);

	/**批量新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-28 21:38:20
	 * @param activityManagers
	 * @return int
	*/
	int batchAdd(@Param("activityManagers") List<ActivityManager> activityManagers);

}