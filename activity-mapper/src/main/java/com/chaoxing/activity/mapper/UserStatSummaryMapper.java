package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.admin.UserStatSummaryQueryDTO;
import com.chaoxing.activity.dto.stat.UserSummaryStatDTO;
import com.chaoxing.activity.model.UserStatSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @className: TUserStatSummaryMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-05-25 10:42:08
 * @version: ver 1.0
 */
@Mapper
public interface UserStatSummaryMapper extends BaseMapper<UserStatSummary> {

    /**
     * 分页查询
     *
     * @param page
     * @param userStatSummaryQuery
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page
     * @Description
     * @author wwb
     * @Date 2021-05-28 16:01:45
     */
    Page<?> paging(@Param("page") Page<?> page, @Param("userStatSummaryQuery") UserStatSummaryQueryDTO userStatSummaryQuery);

    /**
     * 统计用户参加的活动数
     *
     * @param uid
     * @param fid
     * @param startTime
     * @param endTime
     * @return java.lang.Integer
     * @Description
     * @author wwb
     * @Date 2021-06-06 20:27:36
     */
    Integer countUserParticipateActivityNum(@Param("uid") Integer uid, @Param("fid") Integer fid, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询用户参加的活动
     *
     * @param page
     * @param uid
     * @param fid
     * @param startTime
     * @param endTime
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<?>
     * @Description
     * @author wwb
     * @Date 2021-06-06 20:37:48
     */
    Page<?> pagingUserParticipate(@Param("page") Page<?> page, @Param("uid") Integer uid, @Param("fid") Integer fid, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户的总参与时长
     *
     * @param uid
     * @return java.lang.Integer
     * @Description
     * @author wwb
     * @Date 2021-06-11 11:27:17
     */
    Integer countUserTotalTimeLength(@Param("uid") Integer uid);

    /**
     * 分页查询用户参加的活动
     *
     * @param page
     * @param fid
     * @param marketId
     * @param uidList
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<?>
     * @Description
     * @author huxiaolong
     * @Date 2021-08-02 15:16:37
     */
    Page<UserStatSummary> pageUserStatResult(Page<UserStatSummary> page, @Param("fid") Integer fid, @Param("marketId") Integer marketId, @Param("uidList") List<Integer> uidList);

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-10-22 14:31:45
    * @param page
    * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.UserSummaryStatDTO>
    */
    Page<UserSummaryStatDTO> pageUserSummaryStat(@Param("page") Page<UserSummaryStatDTO> page, @Param("marketId") Integer marketId, @Param("fid") Integer fid);
}