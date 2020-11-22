package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.GroupRegionFilter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: GroupRegionFilterMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2020-11-20 18:07:24
 * @version: ver 1.0
 */
@Mapper
public interface GroupRegionFilterMapper extends BaseMapper<GroupRegionFilter> {

	/**根据组code查询地区列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-22 14:27:04
	 * @param groupCode
	 * @return java.util.List<com.chaoxing.activity.model.GroupRegionFilter>
	*/
	List<GroupRegionFilter> listByGroupCode(@Param("groupCode") String groupCode);

}