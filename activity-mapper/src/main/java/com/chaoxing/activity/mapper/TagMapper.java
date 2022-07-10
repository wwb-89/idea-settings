package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TagMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-11-23 16:38:48
 * @version: ver 1.0
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2021-11-24 15:31:21
     * @param tags
     * @return void
    */
    void batchAdd(@Param("tags") List<Tag> tags);

}