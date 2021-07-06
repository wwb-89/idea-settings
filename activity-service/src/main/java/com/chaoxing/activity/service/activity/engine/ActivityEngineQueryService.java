package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ComponentFieldMapper;
import com.chaoxing.activity.mapper.ComponentMapper;
import com.chaoxing.activity.mapper.TemplateComponentMapper;
import com.chaoxing.activity.mapper.TemplateMapper;
import com.chaoxing.activity.model.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/7/6 2:29 下午
 * <p>
 */
@Slf4j
@Service
public class ActivityEngineQueryService {

    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private TemplateComponentMapper templateComponentMapper;
    @Autowired
    private ComponentMapper componentMapper;
    @Autowired
    private ComponentFieldMapper componentFieldMapper;


    /**根据机构fid，查询除系统模板外，其他模板
    * @Description 
    * @author huxiaolong
    * @Date 2021-07-06 14:35:58
    * @param fid
    * @return java.util.List<com.chaoxing.activity.model.Template>
    */
    public List<Template> listTemplateByFid(Integer fid) {
        return templateMapper.selectList(new QueryWrapper<Template>()
                .lambda()
                .eq(Template::getSystem, Boolean.TRUE)
                .or()
                .eq(Template::getFid, fid)
                .orderByAsc(Template::getSequence));
    }


}
