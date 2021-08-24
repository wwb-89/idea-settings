package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityMarket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ActivityMarketMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-08-09 19:17:42
 * @version: ver 1.0
 */
@Mapper
public interface ActivityMarketMapper extends BaseMapper<ActivityMarket> {

    /**批量关联活动-市场
    * @Description
    * @author huxiaolong
    * @Date 2021-08-12 16:06:27
    * @param activityMarkets
    * @return void
    */
    void batchAdd(@Param("activityMarkets") List<ActivityMarket> activityMarkets);
}