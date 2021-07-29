package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.BlacklistQueryDTO;
import com.chaoxing.activity.model.Blacklist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: BlacklistMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-07-26 18:45:24
 * @version: ver 1.0
 */
@Mapper
public interface BlacklistMapper extends BaseMapper<Blacklist> {

    /**
     * 批量新增
     *
     * @param blacklists
     * @return int
     * @Description
     * @author wwb
     * @Date 2021-07-27 16:30:06
     */
    int batchAdd(@Param("blacklists") List<Blacklist> blacklists);

    /**
     * 分页查询黑名单
     *
     * @param page
     * @param blacklistQueryDto
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page
     * @Description
     * @author wwb
     * @Date 2021-07-29 11:19:45
     */
    Page<?> pageBlacklist(@Param("page") Page<?> page, @Param("blacklistQueryDto") BlacklistQueryDTO blacklistQueryDto);

}