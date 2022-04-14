package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.MarketClassify;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @className: MarketClassifyMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-07-19 15:03:35
 * @version: ver 1.0
 */
@Mapper
public interface MarketClassifyMapper extends BaseMapper<MarketClassify> {

	/**
	 * 查询活动市场的最大顺序
	 *
	 * @param marketId
	 * @return java.lang.Integer
	 * @Description
	 * @author wwb
	 * @Date 2021-07-19 17:45:33
	 */
	Integer getMaxSequenceByMarketId(@Param("marketId") Integer marketId);

	/**排序
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-06 19:42:25
	 * @param marketId
	 * @param classifyIdSequenceMap
	 * @return void
	*/
	void sort(@Param("marketId") Integer marketId, @Param("classifyIdSequenceMap") Map<Integer, Integer> classifyIdSequenceMap);

}