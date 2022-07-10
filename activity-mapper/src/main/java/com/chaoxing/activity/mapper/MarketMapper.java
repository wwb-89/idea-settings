package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.Market;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: MarketMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-04-11 11:06:42
 * @version: ver 1.0
 */
@Mapper
public interface MarketMapper extends BaseMapper<Market> {

    /**
     * 获取最大的顺序
     *
     * @param fid
     * @return java.lang.Integer
     * @Description
     * @author wwb
     * @Date 2021-07-21 14:11:56
     */
    Integer getMaxSequence(@Param("fid") Integer fid);

    /**
     * 查询机构下关联了指定活动的市场id列表
     *
     * @param fid
     * @param activityId
     * @return java.util.List<java.lang.Integer>
     * @Description
     * @author huxiaolong
     * @Date 2021-08-12 18:10:53
     */
    List<Integer> listOrgAssociatedActivityMarketId(@Param("fid") Integer fid, @Param("activityId") Integer activityId);

    /**查询机构指定flag的活动市场
     * @Description 
     * @author wwb
     * @Date 2022-04-01 11:09:42
     * @param fid
     * @param flag
     * @return java.util.List<com.chaoxing.activity.model.Market>
    */
    List<Market> listOrgSpecifiedFlag(@Param("fid") Integer fid, @Param("flag") String flag);

}