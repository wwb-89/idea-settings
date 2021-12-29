package com.chaoxing.activity.api.controller.form;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateFromFormParamDTO;
import com.chaoxing.activity.service.queue.activity.WfwFormSyncActivityQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**万能表单活动服务
 * @author wwb
 * @version ver 1.0
 * @className WfwFormActivityApiController
 * @description
 * @blame wwb
 * @date 2021-12-29 19:49:53
 */
@Slf4j
@RestController
@RequestMapping("activity")
public class WfwFormActivityApiController {

    @Resource
    private WfwFormSyncActivityQueue wfwFormSyncActivityQueue;

    /** 万能表单数据新增/修改/删除后同步修改活动
     * @Description
     * @author huxiaolong
     * @Date 2021-08-26 16:46:53
     * @param activityFormSyncParam
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @RequestMapping("sync/from/wfw-form")
    public RestRespDTO activitySyncOperate(@Valid ActivityCreateFromFormParamDTO activityFormSyncParam) {
        log.info("接收到万能表单的活动处理参数:{}", JSON.toJSONString(activityFormSyncParam));
        wfwFormSyncActivityQueue.push(activityFormSyncParam);
        return RestRespDTO.success();
    }

}