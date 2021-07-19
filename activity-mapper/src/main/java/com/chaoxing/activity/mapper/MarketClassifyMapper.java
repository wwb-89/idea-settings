package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.MarketClassify;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: MarketClassifyMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-07-19 15:03:35
 * @version: ver 1.0
 */
@Mapper
public interface MarketClassifyMapper extends BaseMapper<MarketClassify> {

	/**查询活动市场的最大顺序
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 17:45:33
	 * @param marketId
	 * @return java.lang.Integer
	*/
	Integer getMaxSequenceByMarketId(@Param("marketId") Integer marketId);

}