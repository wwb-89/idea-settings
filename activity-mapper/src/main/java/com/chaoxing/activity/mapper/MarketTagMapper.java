package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.MarketTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: MarketTagMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-11-23 16:38:48
 * @version: ver 1.0
 */
@Mapper
public interface MarketTagMapper extends BaseMapper<MarketTag> {

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2021-11-24 15:46:27
     * @param marketId
     * @param tagIds
     * @return void
    */
    void batchAdd(@Param("marketId") Integer marketId, @Param("tagIds") List<Integer> tagIds);

}