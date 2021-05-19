package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityClassify;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ActivityClassifyMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Mapper
public interface ActivityClassifyMapper extends BaseMapper<ActivityClassify> {

	/**
	 * 查询最大顺序
	 *
	 * @param fid
	 * @return java.lang.Integer
	 * @Description
	 * @author wwb
	 * @Date 2020-11-10 17:55:17
	 */
	Integer getMaxSequence(@Param("fid") Integer fid);

	/**批量新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-23 18:29:37
	 * @param activityClassifies
	 * @return int
	*/
	int batchAdd(@Param("activityClassifies") List<ActivityClassify> activityClassifies);

}