package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityScope;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ActivityScopeMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Mapper
public interface ActivityScopeMapper extends BaseMapper<ActivityScope> {

	/**批量新增
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-12 16:44:55
	 * @param activityScopes
	 * @return int
	*/
	int batchAdd(@Param("activityScopes") List<ActivityScope> activityScopes);

}