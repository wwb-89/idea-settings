package com.chaoxing.activity.api.controller.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormImageDTO;
import com.chaoxing.activity.dto.manager.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**湖北群艺馆万能表单直播api服务
 * @author wwb
 * @version ver 1.0
 * @className HbqygWfwFormLiveApiController
 * @description
 * @blame wwb
 * @date 2021-12-20 17:34:52
 */
@Slf4j
@RestController
@RequestMapping("mh/hbqyg/live")
public class HbqygWfwFormLiveApiController {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private WfwFormApiService wfwFormApiService;
    @Resource
    private CloudApiService cloudApiService;

    /**直播
     * @Description
     * 1、根据websiteId查询活动id
     * 2、根据活动的 createFid和formId查询表单的指定记录
     * 3、获取指定的字段
     * Associated_live_broadcast 是否关联直播，是/否
     * Live_recommendati 直播信息
     * identifier_live 直播编号
     * Live_title 直播标题
     * Live_start_time 直播开始时间
     * Live_cover 直播封面
     * Portal_address 直播地址
     * @author wwb
     * @Date 2021-12-20 17:41:14
     * @param data
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping
    public RestRespDTO liveList(@RequestBody String data) {
        JSONObject result = new JSONObject();
        result.put("curPage", 1);
        Integer totalPages = 0;
        Integer totalRecords = 0;

        JSONObject jsonObject = JSON.parseObject(data);
        Integer websiteId = jsonObject.getInteger("websiteId");
        Activity activity = activityQueryService.getByWebsiteId(websiteId);
        Integer createFid = Optional.ofNullable(activity).map(Activity::getCreateFid).orElse(null);
        String origin = Optional.ofNullable(activity).map(Activity::getOrigin).orElse(null);
        Integer originFormUserId = Optional.ofNullable(activity).map(Activity::getOriginFormUserId).orElse(null);
        if (StringUtils.isNotBlank(origin) && originFormUserId != null) {
            FormDataDTO formRecord = wfwFormApiService.getFormRecord(originFormUserId, Integer.parseInt(origin), createFid);
            if (formRecord != null) {
                List<MhGeneralAppResultDataDTO> mhGeneralAppResultDatas = Lists.newArrayList();
                List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = Lists.newArrayList();
                // 直播封面
                FormImageDTO liveCover = formRecord.getImage("Live_cover");
                String imageUrl = Optional.ofNullable(liveCover).map(FormImageDTO::getObjectId).map(v -> cloudApiService.buildImageUrl(v)).orElse("");
                mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                        .value(imageUrl)
                        .flag("0")
                        .build());
                // 直播标题
                String liveTitle = formRecord.getStringValue("Live_title");
                mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                        .value(liveTitle)
                        .flag("1")
                        .build());
                // 直播开始时间
                String liveStartTime = formRecord.getStringValue("Live_start_time");
                mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                        .value(liveStartTime)
                        .flag("7")
                        .build());
                // 直播跳转地址
                String liveAddress = formRecord.getStringValue("Portal_address");
                liveAddress = Optional.ofNullable(liveAddress).filter(StringUtils::isNotBlank).map(v -> "https://44798jeq.mh.chaoxing.com" + v).orElse("");
                mhGeneralAppResultDatas.add(MhGeneralAppResultDataDTO.builder()
                        .id(activity.getId())
                        .orsUrl(liveAddress)
                        .type(3)
                        .fields(mhGeneralAppResultDataFields)
                        .build());
                result.put("results", mhGeneralAppResultDatas);
                totalPages = 1;
                totalRecords = 1;
            }
        }
        result.put("totalPages", totalPages);
        result.put("totalRecords", totalRecords);
        return RestRespDTO.success(result);
    }

}
