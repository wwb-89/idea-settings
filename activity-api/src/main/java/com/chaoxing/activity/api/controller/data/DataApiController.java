package com.chaoxing.activity.api.controller.data;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.DataPushValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className DataApiController
 * @description
 * @blame wwb
 * @date 2021-08-03 14:32:21
 */
@Slf4j
@RestController
@RequestMapping("data")
public class DataApiController {

    @Resource
    private DataPushValidationService dataPushValidationService;
    @Resource
    private ActivityQueryService activityQueryService;

    /**报名签到的数据是否推送
     * @Description
     * @author wwb
     * @Date 2021-08-03 14:05:49
     * @param signId
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @RequestMapping("sign/{signId}/push-able")
    public RestRespDTO pushAble(@PathVariable Integer signId) {
        boolean pushAble = true;
        Activity activity = activityQueryService.getBySignId(signId);
        if (activity != null) {
            pushAble = dataPushValidationService.pushAble(activity.getCreateFid(), activity.getMarketId());
        }
        return RestRespDTO.success(pushAble);
    }

}
