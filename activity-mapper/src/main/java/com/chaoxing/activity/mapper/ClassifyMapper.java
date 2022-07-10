package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.Classify;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ClassifyMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-07-19 15:03:35
 * @version: ver 1.0
 */
@Mapper
public interface ClassifyMapper extends BaseMapper<Classify> {

	/**
	 * 查询机构的活动分类列表
	 *
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.Classify>
	 * @Description
	 * @author wwb
	 * @Date 2021-07-19 15:48:46
	 */
	List<Classify> listByFid(@Param("fid") Integer fid);

	/**
	 * 查询活动市场的活动分类列表
	 *
	 * @param marketId
	 * @return java.util.List<com.chaoxing.activity.model.Classify>
	 * @Description
	 * @author wwb
	 * @Date 2021-07-19 16:05:58
	 */
	List<Classify> listByMarketId(@Param("marketId") Integer marketId);

	/**根据机构id列表查询活动类型列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 20:03:22
	 * @param fids
	 * @return java.util.List<com.chaoxing.activity.model.Classify>
	*/
	List<Classify> listByFids(@Param("fids") List<Integer> fids);

}