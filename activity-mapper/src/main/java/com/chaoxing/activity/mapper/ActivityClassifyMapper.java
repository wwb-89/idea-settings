package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityClassify;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
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

	/**根据ids批量查询
	* @Description 
	* @author huxiaolong
	* @Date 2021-05-28 22:07:02
	* @param ids
	* @return java.util.List<com.chaoxing.activity.model.ActivityClassify>
	*/
    List<ActivityClassify> listByIds(@Param("ids") Collection<Integer> ids);
}