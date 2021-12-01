package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.manager.sign.SignDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.DataPushRecord;
import com.chaoxing.activity.service.queue.activity.handler.WfwFormSyncActivityQueueService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.DataPushRecordQueryService;
import com.chaoxing.activity.service.manager.GroupApiService;
import com.chaoxing.activity.util.BaiduMapUtils;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.constant.ActivityMhUrlConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.chaoxing.activity.util.exception.WfwFormActivityNotGeneratedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private WfwFormSyncActivityQueueService activityFormSyncService;
    @Resource
    private GroupApiService groupApiService;

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
        if (activity == null) {
            throw new WfwFormActivityNotGeneratedException();
        } else {
            url = activity.getPreviewUrl();
            return "redirect:" + url;
        }
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
            String url = DomainConstant.SIGN_WEB_DOMAIN +  "/map/location?address=%s&lng=%s&lat=%s";
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
    @RequestMapping("activity-portal/from/wfw-form")
    public String redirectToActivityPortal(Integer fid, Integer formId, Integer formUserId) {
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
    @RequestMapping("activity-index/from/wfw-form")
    public String redirectToActivityIndex(Integer fid, Integer formId, Integer formUserId) {
        Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
        return "redirect:" + activity.getAdminUrl();
    }

    /**重定向签到管理列表
     * @Description
     * @author huxiaolong
     * @Date 2021-10-15 16:25:32
     * @param fid
     * @param formId
     * @param formUserId
     * @return java.lang.String
     */
    @RequestMapping("sign-in-list/from/wfw-form")
    public String redirectToSignInList(Integer fid, Integer formId, Integer formUserId) {
        Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
        return "redirect:" + SignDTO.getSignInListUrl(activity.getSignId());
    }

    /**重定向到报名管理
     * @Description
     * @author huxiaolong
     * @Date 2021-10-15 16:26:10
     * @param fid
     * @param formId
     * @param formUserId
     * @return java.lang.String
     */
    @RequestMapping("sign-up-manage/from/wfw-form")
    public String redirectToSignUpManage(Integer fid, Integer formId, Integer formUserId) {
        Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
        return "redirect:" + SignDTO.getSignUpManageUrl(activity.getSignId());
    }
    /**重定向到报名管理
     * @Description
     * @author huxiaolong
     * @Date 2021-12-01 14:26:10
     * @return java.lang.String
     */
    @RequestMapping("sign-up-manage/by/activity/{activityId}")
    public String redirectToSignUpManage(@PathVariable Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        return "redirect:" + SignDTO.getSignUpManageUrl(activity.getSignId());
    }

    /**门户活动海报地址
     * @Description
     * @author wwb
     * @Date 2021-09-18 11:25:18
     * @param websiteId
     * @return java.lang.String
     */
    @RequestMapping("mh/activity/poster")
    public String mhPosterUrl(@RequestParam Integer websiteId) {
        Activity activity = activityQueryService.getByWebsiteId(websiteId);
        Integer activityId = Optional.ofNullable(activity).map(Activity::getId).orElse(1);
        return "redirect:" + String.format(ActivityMhUrlConstant.ACTIVITY_POSTERS_URL, activityId);
    }

    /**重定向到作品征集管理
     * @Description
     * @author huxiaolong
     * @Date 2021-10-15 16:26:40
     * @param fid
     * @param formId
     * @param formUserId
     * @return java.lang.String
     */
    @RequestMapping("work-manage/from/wfw-form")
    public String redirectToWorkManage(Integer fid, Integer formId, Integer formUserId) {
        Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
        if (activity.getOpenWork() && activity.getWorkId() != null) {
            return "redirect:" + UrlConstant.getWorkManageUrl(activity.getWorkId());
        }
        return "";
    }

    /**重定向到小组主页
     * @Description 
     * @author wwb
     * @Date 2021-10-22 10:47:17
     * @param request
     * @param bbsid
     * @return java.lang.String
    */
    @RequestMapping("group/{bbsid}")
    public String redirectGroupIndex(HttpServletRequest request, @PathVariable String bbsid) {
        String url;
        if (UserAgentUtils.isMobileAccess(request)) {
            url = groupApiService.getMobileGroupUrl(bbsid);
        } else {
            url = groupApiService.getPcGroupUrl(bbsid);
        }
        return "redirect:" + url;
    }

}