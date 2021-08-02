package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.admin.UserStatSummaryQueryDTO;
import com.chaoxing.activity.model.UserStatSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
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
     * @return java.lang.Integer
     * @Description
     * @author wwb
     * @Date 2021-06-06 20:27:36
     */
    Integer countUserParticipateActivityNum(@Param("uid") Integer uid, @Param("fid") Integer fid);

    /**
     * 分页查询用户参加的活动
     *
     * @param page
     * @param uid
     * @param fid
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<?>
     * @Description
     * @author wwb
     * @Date 2021-06-06 20:37:48
     */
    Page<?> pagingUserParticipate(@Param("page") Page<?> page, @Param("uid") Integer uid, @Param("fid") Integer fid);

    /**统计用户的总参与时长
     * @Description 
     * @author wwb
     * @Date 2021-06-11 11:27:17
     * @param uid
     * @return java.lang.Integer
    */
    Integer countUserTotalTimeLength(@Param("uid") Integer uid);

    /**分页查询用户参加的活动
    * @Description
    * @author huxiaolong
    * @Date 2021-08-02 15:16:37
    * @param page
    * @param fid
    * @param marketId
    * @param uidList
    * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<?>
    */
    Page<UserStatSummary> pageUserStatResult(Page<UserStatSummary> page, @Param("fid") Integer fid, @Param("marketId") Integer marketId, @Param("uidList") List<Integer> uidList);
}