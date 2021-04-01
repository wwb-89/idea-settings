package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivitySignModule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TActivitySignModuleMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-03-30 17:09:58
 * @version: ver 1.0
 */
@Mapper
public interface ActivitySignModuleMapper extends BaseMapper<ActivitySignModule> {

	/**批量新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-30 17:15:03
	 * @param activitySignModules
	 * @return int
	*/
	int batchAdd(@Param("activitySignModules") List<ActivitySignModule> activitySignModules);

}