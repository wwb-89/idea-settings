package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.manager.PassportApiService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**机构api服务
 * @author wwb
 * @version ver 1.0
 * @className OrgApiController
 * @description
 * @blame wwb
 * @date 2021-09-02 10:39:54
 */
@RestController
@RequestMapping("org")
public class OrgApiController {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private MarketHandleService marketHandleService;
    @Resource
    private PassportApiService passportApiService;

    /**查询机构下创建的作品征集id列表
     * @Description 
     * @author wwb
     * @Date 2021-09-02 11:12:42
     * @param fid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("{fid}/workId")
    public RestRespDTO listOrgWorkId(@PathVariable Integer fid) {
        List<Integer> workIds = activityQueryService.listOrgCreatedWorkId(fid);
        return RestRespDTO.success(workIds);
    }

    /**鄂尔多斯定制的查询机构下创建的作品征集id列表
     * @Description 
     * @author wwb
     * @Date 2021-09-07 20:12:09
     * @param fid
     * @param workId
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("{fid}/erdos/workId")
    public RestRespDTO listOrgWorkId(@PathVariable Integer fid, @RequestParam Integer workId) {
        List<Integer> workIds = activityQueryService.listOrgJuniorCreatedWorkId(fid, workId);
        return RestRespDTO.success(workIds);
    }

    /**给机构创建活动市场
     * @Description 
     * @author wwb
     * @Date 2021-09-15 17:35:45
     * @param fid
     * @param flag
     * @param uid
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("{fid}/new-market")
    public RestRespDTO createMarket(@PathVariable Integer fid, @RequestParam String flag, @RequestParam Integer uid) {
        String userRealName = passportApiService.getUserRealName(uid);
        String orgName = passportApiService.getOrgName(fid);
        LoginUserDTO loginUserDto = LoginUserDTO.buildDefault(uid, userRealName, fid, orgName);
        marketHandleService.getOrCreateMarket(fid, Activity.ActivityFlagEnum.fromValue(flag), loginUserDto);
        return RestRespDTO.success();
    }

}