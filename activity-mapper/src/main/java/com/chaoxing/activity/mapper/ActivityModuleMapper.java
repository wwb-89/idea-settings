package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityModule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ActivityModuleMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Mapper
public interface ActivityModuleMapper extends BaseMapper<ActivityModule> {

	/**
	 * 批量新增
	 *
	 * @param activityModules
	 * @return int
	 * @Description
	 * @author wwb
	 * @Date 2020-11-11 15:14:37
	 */
	int batchAdd(@Param("activityModules") List<ActivityModule> activityModules);

	/**
	 * 根据活动id查询最大顺序
	 *
	 * @param activityId
	 * @return int
	 * @Description
	 * @author wwb
	 * @Date 2020-11-11 15:38:43
	 */
	int getMaxSequenceByActivityId(@Param("activityId") Integer activityId);

}