package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.model.ActivityClassifyNew;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: TActivityClassifyNewMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-04-11 22:00:29
 * @version: ver 1.0
 */
@Mapper
public interface ActivityClassifyNewMapper extends BaseMapper<ActivityClassifyNew> {

	/**
	 * 分页查询
	 *
	 * @param page
	 * @param activityMarketId
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.ActivityClassifyNew>
	 * @Description
	 * @author wwb
	 * @Date 2021-04-12 11:07:36
	 */
	Page<ActivityClassifyNew> paging(@Param("page") Page<ActivityClassifyNew> page, @Param("activityMarketId") Integer activityMarketId);

	/**查询最大的顺序
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 13:36:46
	 * @param activityMarketId
	 * @return int
	*/
	int getMaxSequence(@Param("activityMarketId") Integer activityMarketId);

}