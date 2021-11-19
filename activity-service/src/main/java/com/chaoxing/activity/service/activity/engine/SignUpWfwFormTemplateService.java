package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.SignUpWfwFormTemplateMapper;
import com.chaoxing.activity.model.SignUpWfwFormTemplate;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**报名万能表单模版服务
 * @author wwb
 * @version ver 1.0
 * @className SignUpWfwFormTemplateService
 * @description
 * @blame wwb
 * @date 2021-11-18 16:44:35
 */
@Slf4j
@Service
public class SignUpWfwFormTemplateService {

    @Resource
    private SignUpWfwFormTemplateMapper signUpWfwFormTemplateMapper;

    /**获取系统报名万能表单模版
     * @Description 
     * @author wwb
     * @Date 2021-11-18 17:51:16
     * @param 
     * @return java.util.List<com.chaoxing.activity.model.SignUpWfwFormTemplate>
    */
    public List<SignUpWfwFormTemplate> listSystem() {
        return signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                .eq(SignUpWfwFormTemplate::getSystem, true)
                .eq(SignUpWfwFormTemplate::getDeleted, false)
        );
    }

    /**查询市场可用的报名万能表单模版
     * @Description 
     * @author wwb
     * @Date 2021-11-18 18:02:10
     * @param marketId
     * @return java.util.List<com.chaoxing.activity.model.SignUpWfwFormTemplate>
    */
    public List<SignUpWfwFormTemplate> listMarket(Integer marketId) {
        List<SignUpWfwFormTemplate> result = Lists.newArrayList();
        List<SignUpWfwFormTemplate> systemSignUpWfwFormTemplates = listSystem();
        result.addAll(systemSignUpWfwFormTemplates);
        List<SignUpWfwFormTemplate> marketSignUpWfwFormTemplates = signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                .eq(SignUpWfwFormTemplate::getMarketId, marketId)
                .eq(SignUpWfwFormTemplate::getDeleted, false)
        );
        result.addAll(marketSignUpWfwFormTemplates);
        return result;
    }

}