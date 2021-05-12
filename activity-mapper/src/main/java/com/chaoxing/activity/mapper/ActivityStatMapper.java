package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivityStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TActivityStatMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-10 16:08:51
 * @version: ver 1.0
 */
@Mapper
public interface ActivityStatMapper extends BaseMapper<ActivityStat> {
    /**根据统计起始时间，统计结束时间，查询每一个activity最新stat统计结果列表
    * @Description 
    * @author huxiaolong
    * @Date 2021-05-11 15:42:38
    * @param startDate
    * @param endDate
    * @param activityIds
    * @return java.util.List<com.chaoxing.activity.model.ActivityStat>
    */
    List<ActivityStat> listActivityStat(@Param("startDate") String startDate,
                                        @Param("endDate") String endDate,
                                        @Param("activityIds") List<Integer> activityIds);

    /**根据统计起始时间，统计结束时间，查询每一个activity最新stat统计结果列表
     * @Description
     * @author huxiaolong
     * @Date 2021-05-11 15:42:38
     * @param startDate
     * @param endDate
     * @param activityIds
     * @return java.util.List<com.chaoxing.activity.model.ActivityStat>
     */
    List<ActivityStat> listLatestStatResults(@Param("startDate") String startDate,
                                             @Param("endDate") String endDate,
                                             @Param("activityIds") List<Integer> activityIds);
}