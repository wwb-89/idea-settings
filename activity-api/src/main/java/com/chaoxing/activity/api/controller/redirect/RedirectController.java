package com.chaoxing.activity.api.controller.redirect;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.manager.sign.SignDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.manager.GroupApiService;
import com.chaoxing.activity.util.BaiduMapUtils;
import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.util.UrlUtils;
import com.chaoxing.activity.util.UserAgentUtils;
import com.chaoxing.activity.util.constant.ActivityMhUrlConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.chaoxing.activity.util.exception.LoginRequiredException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**重定向
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
    private GroupApiService groupApiService;
    @Resource
    private MarketHandleService marketHandleService;

    /**转发到双选会会场
     * @Description
     * @author wwb
     * @Date 2021-04-08 17:23:51
     * @param pageId
     * @return org.springframework.web.servlet.view.RedirectView
     */
    @RequestMapping("dual-select/forward")
    public RedirectView forward(Integer pageId) {
        Activity activity = activityQueryService.getByPageId(pageId);
        Integer fid = activity.getCreateFid();
        String url = String.format(UrlConstant.DUAL_SELECT_INDEX_URL, activity.getId(), fid);
        return new RedirectView(url);
    }

    /**跳转到活动地址详情
     * @Description
     * 1、pc端跳转到百度地图
     * 2、移动端跳转到
     * @author wwb
     * @Date 2021-07-22 09:40:25
     * @param request
     * @param activityId
     * @return org.springframework.web.servlet.view.RedirectView
    */
    @RequestMapping("activity/{activityId}/address")
    public RedirectView activityAddressDetail(HttpServletRequest request, @PathVariable Integer activityId) throws UnsupportedEncodingException {
        Activity activity = activityQueryService.getById(activityId);
        String activityAddress = Optional.ofNullable(activity.getAddress()).orElse("") + Optional.ofNullable(activity.getDetailAddress()).orElse("");
        activityAddress = URLEncoder.encode(activityAddress, StandardCharsets.UTF_8.name());
        BigDecimal lng = activity.getLongitude();
        BigDecimal lat = activity.getDimension();
        if (UserAgentUtils.isMobileAccess(request)) {
            String url = DomainConstant.SIGN_WEB +  "/map/location?address=%s&lng=%s&lat=%s";
            url = String.format(url, activityAddress, lng, lat);
            return new RedirectView(url);
        } else {
            String name = URLEncoder.encode(activity.getName(), StandardCharsets.UTF_8.name());
            String activityAddressLink = BaiduMapUtils.generateAddressUrl(lng, lat, name, activityAddress);
            return new RedirectView(activityAddressLink);
        }
    }

    /**重定向到报名管理
     * @Description
     * @author huxiaolong
     * @Date 2021-12-01 14:26:10
     * @return org.springframework.web.servlet.view.RedirectView
     */
    @RequestMapping("sign-up-manage/by/activity/{activityId}")
    public RedirectView redirectToSignUpManage(@PathVariable Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null || activity.getSignId() == null) {
            throw new BusinessException("活动或报名签到不存在");
        }
        return new RedirectView(SignDTO.getSignUpManageUrl(activity.getSignId()));
    }

    /**门户活动海报地址
     * @Description
     * @author wwb
     * @Date 2021-09-18 11:25:18
     * @param websiteId
     * @return org.springframework.web.servlet.view.RedirectView
     */
    @RequestMapping("mh/activity/poster")
    public RedirectView mhPosterUrl(@RequestParam Integer websiteId) {
        Activity activity = activityQueryService.getByWebsiteId(websiteId);
        Integer activityId = Optional.ofNullable(activity).map(Activity::getId).orElse(1);
        return new RedirectView(String.format(ActivityMhUrlConstant.ACTIVITY_POSTERS_URL, activityId));
    }

    /**重定向到小组主页
     * @Description 
     * @author wwb
     * @Date 2021-10-22 10:47:17
     * @param request
     * @param bbsid
     * @return org.springframework.web.servlet.view.RedirectView
    */
    @RequestMapping("group/{bbsid}")
    public RedirectView redirectGroupIndex(HttpServletRequest request, @PathVariable String bbsid) {
        String url;
        if (UserAgentUtils.isMobileAccess(request)) {
            url = groupApiService.getMobileGroupUrl(bbsid);
        } else {
            url = groupApiService.getPcGroupUrl(bbsid);
        }
        return new RedirectView(url);
    }

    /**
     * 重定向到活动新增页面
     * @Description
     * @author huxiaolong
     * @Date 2021-12-02 11:28:36
     * @param request
     * @param marketId
     * @param flag
     * @return org.springframework.web.servlet.view.RedirectView
     */
    @RequestMapping("activity/add-index")
    public RedirectView redirectActivityCreateView(HttpServletRequest request, Integer marketId, String flag) {
        flag = Optional.ofNullable(flag).orElse(Activity.ActivityFlagEnum.NORMAL.getValue());
        marketId = getOrCreateMarketByFlag(request, marketId, flag);
        if (marketId != null) {
            String redirectUrl = DomainConstant.ADMIN + "/activity/add" + "?marketId=" + marketId + "&flag=" + flag;
            return new RedirectView(UrlUtils.handleRedirectUrl(redirectUrl, request));
        }
        return new RedirectView();
    }

    /**重定向到活动市场主页
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-02 11:28:16
     * @param request
     * @param marketId
     * @param flag
     * @return org.springframework.web.servlet.view.RedirectView
     */
    @RequestMapping("market-index")
    public RedirectView redirectMarketSettingIndex(HttpServletRequest request, Integer marketId, String flag) {
        flag = Optional.ofNullable(flag).orElse(Activity.ActivityFlagEnum.NORMAL.getValue());
        marketId = getOrCreateMarketByFlag(request, marketId, flag);
        return new RedirectView(DomainConstant.ADMIN + "/market/" + marketId + "/setting");
    }

    /**根据fid、flag获取活动市场id
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-02 11:27:35
     * @param request
     * @param marketId
     * @param flag
     * @return
     */
    private Integer getOrCreateMarketByFlag(HttpServletRequest request, Integer marketId, String flag) {
        if (marketId == null && StringUtils.isNotBlank(flag)) {
            // 若不存在，则判断市场是否存在，市场不存在则创建市场
            Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(flag);
            Integer uid = CookieUtils.getUid(request);
            Integer fid = CookieUtils.getFid(request);
            if (uid == null || fid == null ) {
                throw new LoginRequiredException("请登录");
            }
            return marketHandleService.getOrCreateMarket(fid, activityFlagEnum, LoginUserDTO.buildDefault(uid, fid));

        }
        return marketId;
    }

}