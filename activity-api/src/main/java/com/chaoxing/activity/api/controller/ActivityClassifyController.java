package com.chaoxing.activity.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyHandleService;
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
@RestController
@RequestMapping("activity/classify")
public class ActivityClassifyController {

    @Resource
    private ActivityClassifyHandleService activityClassifyHandleService;

    @RequestMapping("clone")
    public RestRespDTO cloneSystem(@RequestBody String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        List<Integer> fids = JSON.parseArray(jsonObject.getString("fids"), Integer.class);
        for (Integer fid : fids) {
            activityClassifyHandleService.cloneSystemClassifyNoCheck(fid);
        }
        return RestRespDTO.success();
    }

}
