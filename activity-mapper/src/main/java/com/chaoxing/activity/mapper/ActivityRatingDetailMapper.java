package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.ActivityRatingQueryDTO;
import com.chaoxing.activity.model.ActivityRatingDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TActivityRatingDetailMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-03-08 16:07:57
 * @version: ver 1.0
 */
@Mapper
public interface ActivityRatingDetailMapper extends BaseMapper<ActivityRatingDetail> {

    /**
     * 分页查询
     *
     * @param page
     * @param activityRatingQueryDTO
     * @return
     */
    Page<ActivityRatingDetail> listByQuery(@Param("page") Page<?> page, @Param("params") ActivityRatingQueryDTO activityRatingQueryDTO);

    /**查询用户创建的评价
     * @Description 
     * @author wwb
     * @Date 2021-03-17 21:33:00
     * @param activityId
     * @param uid
     * @return java.util.List<com.chaoxing.activity.model.ActivityRatingDetail>
    */
    List<ActivityRatingDetail> listUserCreated(@Param("activityId") Integer activityId, @Param("uid") Integer uid);

    /**
     * 批量更新状态
     *
     * @param activityId
     * @param ratingDetailIds
     * @return
     */
    Integer batchUpAuditStatus(@Param("activityId") Integer activityId, @Param("ratingDetailIds") List<Integer> ratingDetailIds, @Param("auditStatus") Integer auditStatus);
}