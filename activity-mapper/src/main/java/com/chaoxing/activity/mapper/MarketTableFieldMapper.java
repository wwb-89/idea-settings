package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.MarketTableField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: MarketTableFieldMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-30 15:25:06
 * @version: ver 1.0
 */
@Mapper
public interface MarketTableFieldMapper extends BaseMapper<MarketTableField> {

    /**批量新增活动市场配置列表
    * @Description 
    * @author huxiaolong
    * @Date 2021-06-30 17:47:58
    * @param marketTableFields
    * @return void
    */
    void batchAdd(@Param("marketTableFields") List<MarketTableField> marketTableFields);
}