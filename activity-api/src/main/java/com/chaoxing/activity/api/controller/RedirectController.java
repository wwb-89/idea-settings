package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.DataPushRecord;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.DataPushRecordQueryService;
import com.chaoxing.activity.util.constant.UrlConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className RedirectController
 * @description
 * @blame wwb
 * @date 2021-05-17 15:02:04
 */
@Controller
@RequestMapping("redirect")
public class RedirectController {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private DataPushRecordQueryService dataPushRecordQueryService;

    /**根据表单行id重定向到活动的详情页面
     * @Description 
     * @author wwb
     * @Date 2021-05-17 15:15:34
     * @param formUserId
     * @return java.lang.String
    */
    @RequestMapping("activity-detail/from/form")
    public String form2ActivityDetail(Integer formUserId) {
        String url = "";
        DataPushRecord dataPushRecord = dataPushRecordQueryService.getByRecord(String.valueOf(formUserId));
        Activity activity = null;
        if (dataPushRecord != null) {
            activity = activityQueryService.getById(Integer.parseInt(dataPushRecord.getIdentify()));
        }
        if (activity != null) {
            url = activity.getPreviewUrl();
        }
        return "redirect:" + url;
    }

    /**转发到双选会会场
     * @Description
     * @author wwb
     * @Date 2021-04-08 17:23:51
     * @param pageId
     * @return java.lang.String
     */
    @RequestMapping("dual-select/forward")
    public String forward(Integer pageId) {
        Activity activity = activityQueryService.getByPageId(pageId);
        Integer fid = activity.getCreateFid();
        String url = String.format(UrlConstant.DUAL_SELECT_INDEX_URL, activity.getId(), fid);
        return "redirect:" + url;
    }

}