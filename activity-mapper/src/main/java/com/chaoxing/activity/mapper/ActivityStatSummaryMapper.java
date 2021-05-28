package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    * @Description
    * @author huxiaolong
    * @Date 2021-05-27 18:23:18
    * @param page
    * @param fid
    * @param activityName
    * @param startTime
    * @param endTime
    * @param orderField
    * @param orderType
    * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO>
    */
    Page<ActivityStatSummaryDTO> activityStatSummaryPage(@Param("page") Page<ActivityStatSummaryDTO> page,
                                                         @Param("fid") Integer fid,
                                                         @Param("activityName") String activityName,
                                                         @Param("startTime") String startTime,
                                                         @Param("endTime") String endTime,
                                                         @Param("orderField") String orderField,
                                                         @Param("orderType") String orderType,
                                                         @Param("signIds") List<Integer> signIds
    );
}