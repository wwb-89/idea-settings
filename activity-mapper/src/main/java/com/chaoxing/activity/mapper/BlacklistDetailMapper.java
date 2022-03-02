package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.query.BlacklistQueryDTO;
import com.chaoxing.activity.model.BlacklistDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: BlacklistDetailMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-03-01 11:45:24
 * @version: ver 1.0
 */
@Mapper
public interface BlacklistDetailMapper extends BaseMapper<BlacklistDetail> {

    /**分页查询违约明细记录
     * @Description
     * @author huxiaolong
     * @Date 2022-03-01 15:54:06
     * @param page
     * @param params
     * @return
     */
    Page pageBlacklistDetail(@Param("page") Page page, @Param("params") BlacklistQueryDTO params);

    /**批量保存违约记录
     * @Description
     * @author huxiaolong
     * @Date 2022-03-01 16:07:40
     * @param data
     * @return
     */
    void batchAdd(@Param("data") List<BlacklistDetail> data);
}
