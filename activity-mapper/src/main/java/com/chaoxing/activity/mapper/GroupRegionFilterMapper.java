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

	/**
	 * 根据组code查询地区列表
	 *
	 * @param areaCode
	 * @return java.util.List<com.chaoxing.activity.model.GroupRegionFilter>
	 * @Description
	 * @author wwb
	 * @Date 2020-11-22 14:27:04
	 */
	List<GroupRegionFilter> listByGroupCode(@Param("areaCode") String areaCode);

	/**根据code查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-25 18:34:00
	 * @param code
	 * @return com.chaoxing.activity.model.GroupRegionFilter
	*/
	GroupRegionFilter getByCode(@Param("code") String code);

}