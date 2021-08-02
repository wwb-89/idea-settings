package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.Market;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: MarketMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-04-11 11:06:42
 * @version: ver 1.0
 */
@Mapper
public interface MarketMapper extends BaseMapper<Market> {

    /**获取最大的顺序
     * @Description 
     * @author wwb
     * @Date 2021-07-21 14:11:56
     * @param fid
     * @return java.lang.Integer
    */
    Integer getMaxSequence(@Param("fid") Integer fid);

}