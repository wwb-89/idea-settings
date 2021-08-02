package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.blacklist.BlacklistDTO;
import com.chaoxing.activity.dto.blacklist.BlacklistRuleDTO;
import com.chaoxing.activity.dto.query.BlacklistQueryDTO;
import com.chaoxing.activity.service.blacklist.BlacklistHandleService;
import com.chaoxing.activity.service.blacklist.BlacklistQueryService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**黑名单服务
 * @author wwb
 * @version ver 1.0
 * @className BlacklistApiController
 * @description
 * @blame wwb
 * @date 2021-07-28 16:07:46
 */
@RestController
@RequestMapping("api/market/{marketId}/blacklist")
public class BlacklistApiController {

    @Resource
    private BlacklistHandleService blacklistHandleService;
    @Resource
    private BlacklistQueryService blacklistQueryService;

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

    /**分页查询黑名单
     * @Description 
     * @author wwb
     * @Date 2021-07-29 11:16:02
     * @param request
     * @param blacklistQueryDto
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("list")
    public RestRespDTO pagingBlacklist(HttpServletRequest request, BlacklistQueryDTO blacklistQueryDto) {
        Page page = HttpServletRequestUtils.buid(request);
        blacklistQueryService.pagingBlacklist(page, blacklistQueryDto);
        return RestRespDTO.success(page);
    }

    /**批量添加黑名单
     * @Description 
     * @author wwb
     * @Date 2021-07-29 17:54:08
     * @param request
     * @param marketId
     * @param blacklistDtosJson
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("add/batch")
    public RestRespDTO add(HttpServletRequest request, @PathVariable Integer marketId, @RequestParam String blacklistDtosJson) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        List<BlacklistDTO> blacklistDtos = JSON.parseArray(blacklistDtosJson, BlacklistDTO.class);
        blacklistHandleService.manualAddBlacklist(marketId, blacklistDtos, loginUser.buildOperateUserDTO());
        return RestRespDTO.success();
    }
    
    /**移除
     * @Description 
     * @author wwb
     * @Date 2021-07-29 17:50:27
     * @param request
     * @param marketId
     * @param uid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("remove")
    public RestRespDTO remove(HttpServletRequest request, @PathVariable Integer marketId, Integer uid) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        blacklistHandleService.manualRemoveBlacklist(marketId, uid, loginUser.buildOperateUserDTO());
        return RestRespDTO.success();
    }

    /**批量移除黑名单
     * @Description 
     * @author wwb
     * @Date 2021-07-29 17:54:25
     * @param request
     * @param marketId
     * @param uids
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("remove/batch")
    public RestRespDTO batchRemove(HttpServletRequest request, @PathVariable Integer marketId, @RequestParam(name = "uids[]") List<Integer> uids) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        blacklistHandleService.manualBatchRemoveBlacklist(marketId, uids, loginUser.buildOperateUserDTO());
        return RestRespDTO.success();
    }

}