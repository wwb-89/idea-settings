package com.chaoxing.activity.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.service.activity.classify.ClassifyHandleService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.util.exception.BusinessException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyController
 * @description
 * @blame wwb
 * @date 2021-04-23 18:45:53
 */
@Deprecated
@RestController
@RequestMapping("activity/classify")
public class ActivityClassifyApiController {

    @Resource
    private ClassifyQueryService classifyQueryService;
    @Resource
    private ClassifyHandleService classifyHandleService;
    @Resource
    private MarketQueryService marketQueryService;

    /**给指定的机构克隆系统活动类型
     * @Description 
     * @author wwb
     * @Date 2021-05-19 11:48:28
     * @param data
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("clone")
    public RestRespDTO cloneSystem(@RequestBody String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        List<Integer> fids = JSON.parseArray(jsonObject.getString("fids"), Integer.class);
        for (Integer fid : fids) {
            classifyHandleService.cloneSystemClassifyToOrg(fid);
        }
        return RestRespDTO.success();
    }

    /**查询机构可选的活动分类列表
     * @Description 表单活动申报创建活动使用
     * 1、但marketId为空且flag不为空的时候，根据fid和flag查询活动市场，
     * 如果查询的活动市场为空这查询机构下的互动类型列表
     * @author wwb
     * @Date 2021-05-19 11:54:17
     * @param fid 与活动市场id二选一
     * @param marketId 与fid二选一
     * @param flag 活动标识
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("list")
    public RestRespDTO listOrgClassify(Integer fid, Integer marketId, String flag) {
        List<Classify> classifies;
        if (marketId == null) {
            marketId = marketQueryService.getMarketIdByFlag(fid, flag);
        }
        if (marketId != null) {
            classifies = classifyQueryService.listMarketClassifies(marketId);
        } else if (fid != null) {
            classifyHandleService.cloneSystemClassifyToOrg(fid);
            classifies = classifyQueryService.listOrgClassifies(fid);
        } else {
            throw new BusinessException("机构id或市场id不能同时为空");
        }
        return RestRespDTO.success(classifies);
    }

}
