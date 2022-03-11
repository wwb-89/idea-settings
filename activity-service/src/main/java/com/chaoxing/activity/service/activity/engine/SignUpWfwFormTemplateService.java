package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.mapper.SignUpWfwFormTemplateMapper;
import com.chaoxing.activity.model.SignUpFillInfoType;
import com.chaoxing.activity.model.SignUpWfwFormTemplate;
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

    /**根据code获取系统模板
     * @Description
     * @author huxiaolong
     * @Date 2021-12-03 10:40:24
     * @param code
     * @return
     */
    public SignUpWfwFormTemplate getSystemTemplateByCode(String code, SignUpWfwFormTemplate.TypeEnum templateFormType) {
        if (templateFormType == null) {
            log.error("模板类型不存在!");
            return null;
        }
        SignUpWfwFormTemplate signUpWfwFormTemplate = signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                .eq(SignUpWfwFormTemplate::getCode, code)
                .eq(SignUpWfwFormTemplate::getType, templateFormType.getValue())
                .eq(SignUpWfwFormTemplate::getSystem, Boolean.TRUE)).stream().findFirst().orElse(null);
        if (signUpWfwFormTemplate == null) {
            log.error("code为{}的系统模板不存在", code);
        }
        return signUpWfwFormTemplate;
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
            signUpWfwFormTemplate = getSystemTemplateByCode(NORMAL_TEMPLATE_CODE, templateFormType);
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

    /**查询万能表单模版
     * @Description 
     * @author wwb
     * @Date 2021-12-22 16:02:54
     * @param 
     * @return java.util.List<com.chaoxing.activity.model.SignUpWfwFormTemplate>
    */
    public List<SignUpWfwFormTemplate> listNormal() {
        return signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                .eq(SignUpWfwFormTemplate::getSystem, true)
                .eq(SignUpWfwFormTemplate::getType, SignUpWfwFormTemplate.TypeEnum.WFW_FORM.getValue())
                .eq(SignUpWfwFormTemplate::getDeleted, false)
        );
    }

    /**查询审批模版
     * @Description 
     * @author wwb
     * @Date 2021-12-22 16:03:05
     * @param 
     * @return java.util.List<com.chaoxing.activity.model.SignUpWfwFormTemplate>
    */
    public List<SignUpWfwFormTemplate> listApproval() {
        return signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                .eq(SignUpWfwFormTemplate::getSystem, true)
                .eq(SignUpWfwFormTemplate::getType, SignUpWfwFormTemplate.TypeEnum.APPROVAL.getValue())
                .eq(SignUpWfwFormTemplate::getDeleted, false)
        );
    }

    /**根据名称查询
     * @Description 
     * @author wwb
     * @Date 2022-01-20 16:51:28
     * @param name
     * @return com.chaoxing.activity.model.SignUpWfwFormTemplate
    */
    public SignUpWfwFormTemplate getByName(String name) {
        if (StringUtils.isNotBlank(name)) {
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
        return getSystemTemplateByCode(NORMAL_TEMPLATE_CODE, formTypeEnum);

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