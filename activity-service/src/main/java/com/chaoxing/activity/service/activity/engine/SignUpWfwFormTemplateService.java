package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.SignUpWfwFormTemplateMapper;
import com.chaoxing.activity.model.SignUpWfwFormTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private static final String NORMAL_TEMPLATE_CODE = "normal";

    /**根据code获取系统模板
     * @Description
     * @author huxiaolong
     * @Date 2021-12-03 10:40:24
     * @param code
     * @return
     */
    public SignUpWfwFormTemplate getSystemTemplateByCode(String code) {
        if (StringUtils.isBlank(code)) {
            log.error("code为空，系统模板不存在");
            return null;
        }
        SignUpWfwFormTemplate signUpWfwFormTemplate = signUpWfwFormTemplateMapper.selectList(new LambdaQueryWrapper<SignUpWfwFormTemplate>()
                .eq(SignUpWfwFormTemplate::getCode, code)
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
    public SignUpWfwFormTemplate getByIdOrDefaultNormal(Integer wfwFormTemplateId) {
        SignUpWfwFormTemplate signUpWfwFormTemplate = getById(wfwFormTemplateId);
        if (signUpWfwFormTemplate == null) {
            signUpWfwFormTemplate = getSystemTemplateByCode(NORMAL_TEMPLATE_CODE);
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
                .eq(SignUpWfwFormTemplate::getType, SignUpWfwFormTemplate.TypeEnum.NORMAL.getValue())
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

}