package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.OrgMarketDataPush;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TOrgMarketDataPushMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-08-02 19:06:41
 * @version: ver 1.0
 */
@Mapper
public interface OrgMarketDataPushMapper extends BaseMapper<OrgMarketDataPush> {

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2021-08-03 11:43:41
     * @param fid
     * @param marketIds
     * @return int
    */
    int batchAdd(@Param("fid") Integer fid, @Param("marketIds") List<Integer> marketIds);

}