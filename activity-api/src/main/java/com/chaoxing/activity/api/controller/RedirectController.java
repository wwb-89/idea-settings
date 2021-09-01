package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.DataPushRecord;
import com.chaoxing.activity.service.activity.ActivityFormSyncService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.DataPushRecordQueryService;
import com.chaoxing.activity.util.BaiduMapUtils;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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
    @Resource
    private ActivityFormSyncService activityFormSyncService;

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

    /**跳转到活动地址详情
     * @Description
     * 1、pc端跳转到百度地图
     * 2、移动端跳转到
     * @author wwb
     * @Date 2021-07-22 09:40:25
     * @param request
     * @param activityId
     * @return java.lang.String
    */
    @RequestMapping("activity/{activityId}/address")
    public String activityAddressDetail(HttpServletRequest request, @PathVariable Integer activityId) throws UnsupportedEncodingException {
        Activity activity = activityQueryService.getById(activityId);
        String activityAddress = Optional.ofNullable(activity.getAddress()).orElse("") + Optional.ofNullable(activity.getDetailAddress()).orElse("");
        activityAddress = URLEncoder.encode(activityAddress, StandardCharsets.UTF_8.name());
        BigDecimal lng = activity.getLongitude();
        BigDecimal lat = activity.getDimension();
        if (UserAgentUtils.isMobileAccess(request)) {
            String url = "https://reading.chaoxing.com/qd/map/location?address=%s&lng=%s&lat=%s";
            url = String.format(url, activityAddress, lng, lat);
            return "redirect:" + url;
        } else {
            String name = URLEncoder.encode(activity.getName(), StandardCharsets.UTF_8.name());
            String activityAddressLink = BaiduMapUtils.generateAddressUrl(lng, lat, name, activityAddress);
            return "redirect:" + activityAddressLink;
        }
    }

    /**重定向到活动门户主页
    * @Description
    * @author huxiaolong
    * @Date 2021-09-01 15:39:34
    * @param fid
    * @param formId
    * @param formUserId
    * @return java.lang.String
    */
    @RequestMapping("/activity-portal/from/wfw-form")
    public String redirectToActivityIndex(Integer fid, Integer formId, Integer formUserId) {
        Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
        return "redirect:" + activity.getPreviewUrl();
    }

    /**重定向到活动管理主页
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-01 15:38:27
    * @param fid
    * @param formId
    * @param formUserId
    * @return java.lang.String
    */
    @RequestMapping("/activity-index/from/wfw-form")
    public String activitySyncOperate(Integer fid, Integer formId, Integer formUserId) {
        Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
        return "redirect:http://manage.hd.chaoxing.com/activity/" + activity.getId();
    }

}