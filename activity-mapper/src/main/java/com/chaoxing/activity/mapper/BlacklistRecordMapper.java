package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.BlacklistRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: BlacklistRecordMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-07-26 18:45:24
 * @version: ver 1.0
 */
@Mapper
public interface BlacklistRecordMapper extends BaseMapper<BlacklistRecord> {

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2021-07-27 20:11:19
     * @param blacklistRecords
     * @return int
    */
    int batchAdd(@Param("blacklistRecords") List<BlacklistRecord> blacklistRecords);

}