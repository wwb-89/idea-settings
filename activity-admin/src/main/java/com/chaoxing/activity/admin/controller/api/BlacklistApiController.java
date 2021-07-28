package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.blacklist.BlacklistRuleDTO;
import com.chaoxing.activity.service.blacklist.BlacklistHandleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**黑名单服务
 * @author wwb
 * @version ver 1.0
 * @className BlacklistApiController
 * @description
 * @blame wwb
 * @date 2021-07-28 16:07:46
 */
@RestController
@RequestMapping("market/{marketId}/blacklist")
public class BlacklistApiController {

    @Resource
    private BlacklistHandleService blacklistHandleService;

    /**更新黑名单规则
     * @Description 
     * @author wwb
     * @Date 2021-07-28 16:10:16
     * @param request
     * @param blacklistRuleDto
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("rule/update")
    public RestRespDTO update(HttpServletRequest request, BlacklistRuleDTO blacklistRuleDto) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        blacklistHandleService.addOrUpdateBlacklistRule(blacklistRuleDto, loginUser.buildOperateUserDTO());
        return RestRespDTO.success();
    }

}