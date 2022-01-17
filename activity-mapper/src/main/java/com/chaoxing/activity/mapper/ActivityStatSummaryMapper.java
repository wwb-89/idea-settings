package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.admin.ActivityStatSummaryQueryDTO;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.model.ActivityStatSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TActivityStatSummaryMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-05-25 10:42:08
 * @version: ver 1.0
 */
@Mapper
public interface ActivityStatSummaryMapper extends BaseMapper<ActivityStatSummary> {

    /**
     * 活动统计汇总分页查询
     *
     * @param page
     * @param queryItem
     * @param orderType
     * @param signIds
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO>
     * @Description
     * @author huxiaolong
     * @Date 2021-05-27 18:23:18
     */
    Page<ActivityStatSummaryDTO> activityStatSummaryPage(@Param("page") Page<ActivityStatSummaryDTO> page,
                                                         @Param("queryItem") ActivityStatSummaryQueryDTO queryItem,
                                                         @Param("orderType") String orderType,
                                                         @Param("signIds") List<Integer> signIds);

    /**
     * 根据活动activityIds，查询对应的活动统计汇总列表
     *
     * @param activityIds
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO>
     * @Description
     * @author huxiaolong
     * @Date 2021-05-27 18:23:18
     */
    List<ActivityStatSummaryDTO> listActivityStatSummariesByIds(@Param("activityIds") List<Integer> activityIds);

    /**
     * 分页查询活动报名排行榜
     *
     * @param page
     * @param marketId
     * @param fid
     * @return
     * @Description
     * @author huxiaolong
     * @Date 2021-12-08 15:59:24
     */
    Page<ActivityStatSummaryDTO> activitySignedUpRankPage(@Param("page") Page<ActivityStatSummaryDTO> page,
                                                          @Param("marketId") Integer marketId,
                                                          @Param("fid") Integer fid);

    /**统计万能表单活动市场活动的报名人数
     * @Description 
     * @author wwb
     * @Date 2022-01-17 10:14:37
     * @param formId
     * @return java.lang.Integer
    */
    Integer countWfwFormMarketActivitySignedUpNum(@Param("formId") Integer formId);
}