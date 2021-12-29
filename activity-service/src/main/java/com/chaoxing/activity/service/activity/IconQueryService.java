package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.IconMapper;
import com.chaoxing.activity.model.Icon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: huxiaolong
 * @date: 2021/12/24 9:13 下午
 * @version: 1.0
 */
@Slf4j
@Service
public class IconQueryService {

    @Autowired
    private IconMapper iconMapper;

    /**查询所有图标
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-24 21:28:31
     * @param
     * @return 
     */
    public List<Icon> list() {
        return iconMapper.selectList(new LambdaQueryWrapper<>());
    }
}
