package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.admin.UserStatSummaryQueryDTO;
import com.chaoxing.activity.model.UserStatSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

}