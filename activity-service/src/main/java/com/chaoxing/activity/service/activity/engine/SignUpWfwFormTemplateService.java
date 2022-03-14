package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.mapper.SignUpWfwFormTemplateMapper;
import com.chaoxing.activity.model.SignUpFillInfoType;
import com.chaoxing.activity.model.SignUpWfwFormTemplate;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
    @Resource
    private SignUpFillInfoTypeService signUpFillInfoTypeService;

    private static final String NORMAL_TEMPLATE_CODE = "normal";

    /**获取系统默认报名模板
     * @Description
     * @author wwb
     * @Date 2022-03-14 16:00:19
     * @param templateFormType
     * @return com.chaoxing.activity.model.SignUpWfwFormTemplate
     */
    private SignUpWfwFormTemplate getSystemNormalTemplate(SignUpWfwFormTemplate.TypeEnum templateFormType) {
        return signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                .eq(SignUpWfwFormTemplate::getCode, NORMAL_TEMPLATE_CODE)
                .eq(SignUpWfwFormTemplate::getType, templateFormType.getValue())
                .eq(SignUpWfwFormTemplate::getSystem, Boolean.TRUE)).stream().findFirst().orElse(null);
    }

    /**根据模板id获取模板,若模板不存在，则返回系统默认的通用模板
     * @Description
     * @author huxiaolong
     * @Date 2021-12-03 10:46:55
     * @param wfwFormTemplateId
     * @return
     */
    public SignUpWfwFormTemplate getByIdOrDefaultNormal(Integer wfwFormTemplateId, SignUpWfwFormTemplate.TypeEnum templateFormType) {
        SignUpWfwFormTemplate signUpWfwFormTemplate = getById(wfwFormTemplateId);
        if (signUpWfwFormTemplate == null) {
            signUpWfwFormTemplate = getSystemNormalTemplate(templateFormType);
        }
        return signUpWfwFormTemplate;
    }

    /**根据模板id获取模板
     * @Description
     * @author huxiaolong
     * @Date 2021-12-03 10:43:45
     * @param wfwFormTemplateId
     * @return
     */
    public SignUpWfwFormTemplate getById(Integer wfwFormTemplateId) {
        if (wfwFormTemplateId == null) {
            return null;
        }
        return signUpWfwFormTemplateMapper.selectById(wfwFormTemplateId);
    }

    /**查询市场可用的万能表单报名模板
     * @Description 
     * @author wwb
     * @Date 2022-03-14 16:13:24
     * @param marketId
     * @return java.util.List<com.chaoxing.activity.model.SignUpWfwFormTemplate>
    */
    public List<SignUpWfwFormTemplate> listMarketWfwFormSignUpTemplate(Integer marketId) {
        return listMarketSignUpTemplate(marketId, SignUpWfwFormTemplate.TypeEnum.WFW_FORM);
    }

    /**查询市场可用的审批报名模板
     * @Description
     * @author wwb
     * @Date 2022-03-14 16:13:51
     * @param marketId
     * @return java.util.List<com.chaoxing.activity.model.SignUpWfwFormTemplate>
    */
    public List<SignUpWfwFormTemplate> listMarketApprovalSignUpTemplate(Integer marketId) {
        return listMarketSignUpTemplate(marketId, SignUpWfwFormTemplate.TypeEnum.APPROVAL);
    }

    private List<SignUpWfwFormTemplate> listMarketSignUpTemplate(Integer marketId, SignUpWfwFormTemplate.TypeEnum type) {
        List<SignUpWfwFormTemplate> signUpWfwFormTemplates = Lists.newArrayList();
        if (marketId != null) {
            List<SignUpWfwFormTemplate> marketSignUpTemplates = signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                    .eq(SignUpWfwFormTemplate::getSystem, false)
                    .eq(SignUpWfwFormTemplate::getMarketId, marketId)
                    .eq(SignUpWfwFormTemplate::getType, type.getValue())
                    .eq(SignUpWfwFormTemplate::getDeleted, false)
            );
            signUpWfwFormTemplates.addAll(marketSignUpTemplates);
        }
        List<SignUpWfwFormTemplate> systemSignUpTemplates = signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                .eq(SignUpWfwFormTemplate::getSystem, true)
                .eq(SignUpWfwFormTemplate::getType, type.getValue())
                .eq(SignUpWfwFormTemplate::getDeleted, false)
        );
        signUpWfwFormTemplates.addAll(systemSignUpTemplates);
        return signUpWfwFormTemplates;
    }

    /**根据名称查询
     * @Description
     * @author wwb
     * @Date 2022-01-20 16:51:28
     * @param name
     * @return com.chaoxing.activity.model.SignUpWfwFormTemplate
     */
    public SignUpWfwFormTemplate getByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        List<SignUpWfwFormTemplate> signUpWfwFormTemplates = signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                .eq(SignUpWfwFormTemplate::getName, name.trim())
                .eq(SignUpWfwFormTemplate::getDeleted, false)
        );
        return signUpWfwFormTemplates.stream().findFirst().orElse(null);
    }

    /**根据名称或报名信息获取表单模板，此方法不会返回空！
     * @Description
     * @author huxiaolong
     * @Date 2022-03-11 16:12:46
     * @param name
     * @param signUp
     * @return
     */
    public SignUpWfwFormTemplate getByNameOrDefaultSignUp(String name, SignUpCreateParamDTO signUp) {
        SignUpWfwFormTemplate signUpWfwFormTemplate = getByName(name);
        // 名字存在，返回模板
        if (signUpWfwFormTemplate != null) {
            return signUpWfwFormTemplate;
        }
        // 根据报名是否开启审核判断报名表单类型是wfw_form亦或是approval
        SignUpWfwFormTemplate.TypeEnum formTypeEnum = Optional.ofNullable(signUp.getOpenAudit()).orElse(false) ?  SignUpWfwFormTemplate.TypeEnum.APPROVAL : SignUpWfwFormTemplate.TypeEnum.WFW_FORM;
        SignUpFillInfoType signUpFillInfoType = signUpFillInfoTypeService.getByTemplateComponentId(signUp.getOriginId());
        // 如果模板的报名信息填报配置存在，且配置模板ids不为空，根据类型查询第一个满足的表单模板
        String wfwFormTemplateIds = Optional.ofNullable(signUpFillInfoType).map(SignUpFillInfoType::getWfwFormTemplateIds).orElse(null);
        if (StringUtils.isNotBlank(wfwFormTemplateIds)) {
            // 表单模板ids转换为list
            signUpFillInfoType.wfwFormTemplateIds2FormTemplateIds();
            signUpWfwFormTemplate = signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                    .eq(SignUpWfwFormTemplate::getType, formTypeEnum.getValue())
                    .in(SignUpWfwFormTemplate::getId, signUpFillInfoType.getFormTemplateIds())).stream().findFirst().orElse(null);
        }
        if (signUpWfwFormTemplate != null) {
            return signUpWfwFormTemplate;
        }
        return getSystemNormalTemplate(formTypeEnum);

    }

    /**列出所有的系统模板
     * @Description
     * @author huxiaolong
     * @Date 2022-02-23 14:28:00
     * @return
     */
    public List<SignUpWfwFormTemplate> listAllSystem() {
        return signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                .eq(SignUpWfwFormTemplate::getSystem, true)
                .eq(SignUpWfwFormTemplate::getDeleted, false));
    }
}