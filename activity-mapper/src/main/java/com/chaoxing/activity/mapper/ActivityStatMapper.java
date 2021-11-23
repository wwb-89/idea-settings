package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.dto.query.admin.ActivityStatQueryDTO;
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

    /**列出前十活动榜，默认浏览量排序
     * @Description
     * @author huxiaolong
     * @Date 2021-05-12 13:19:43
     * @param queryParams
     * @param orderField
     * @param orderType
     * @return java.util.List<com.chaoxing.activity.model.ActivityStat>
     */
    List<ActivityStat> listTopActivity(@Param("queryParams") ActivityStatQueryDTO queryParams,
                                       @Param("orderField") String orderField,
                                       @Param("orderType") String orderType);

    /**列出活动榜
     * @Description
     * @author huxiaolong
     * @Date 2021-05-12 13:19:43
     * @param queryParams
     * @param orderField
     * @param orderType
     * @return java.util.List<com.chaoxing.activity.model.ActivityStat>
     */
    List<ActivityStat> activityStatList(@Param("queryParams") ActivityStatQueryDTO queryParams,
                                        @Param("orderField") String orderField,
                                        @Param("orderType") String orderType);



    /**根据机构查询活动统计结果
    * @Description
    * @author huxiaolong
    * @Date 2021-06-11 15:45:52
    * @param fids
    * @param startDate
    * @param endDate
    * @return java.util.List<com.chaoxing.activity.model.ActivityStat>
    */
    List<ActivityStat> listActivityStatByFids(@Param("fids") List<Integer> fids, @Param("startDate") String startDate, @Param("endDate")String endDate);

    /**根据活动ids查询活动对应的浏览量
    * @Description
    * @author huxiaolong
    * @Date 2021-08-03 15:32:52
    * @param activityIds
    * @return java.util.List<com.chaoxing.activity.model.ActivityStat>
    */
    List<ActivityStat> listActivityPvByActivityIds(@Param("activityIds") List<Integer> activityIds);
}