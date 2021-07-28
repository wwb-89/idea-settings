package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2021-07-27 16:30:06
     * @param blacklists
     * @return int
    */
    int batchAdd(@Param("blacklists") List<Blacklist> blacklists);

}