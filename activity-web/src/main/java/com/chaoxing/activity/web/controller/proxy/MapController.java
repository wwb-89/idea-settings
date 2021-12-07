package com.chaoxing.activity.web.controller.proxy;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Optional;

/**地图
 * @author wwb
 * @version ver 1.0
 * @className MapController
 * @description
 * @blame wwb
 * @date 2021-09-17 10:22:55
 */
@Controller
@RequestMapping("proxy/map")
public class MapController {

    @Resource
    private ActivityQueryService activityQueryService;

    /**地图位置
     * @Description 门户活动
     * @author wwb
     * @Date 2021-09-17 10:56:42
     * @param model
     * @param websiteId
     * @return java.lang.String
    */
    @RequestMapping("location/from-mh")
    public String location(Model model,  @RequestParam Integer websiteId) {
        Activity activity = activityQueryService.getByWebsiteId(websiteId);
        BigDecimal lng = Optional.ofNullable(activity).map(Activity::getLongitude).orElse(CommonConstant.DEFAULT_LNG);
        BigDecimal lat = Optional.ofNullable(activity).map(Activity::getDimension).orElse(CommonConstant.DEFAULT_LAT);
        model.addAttribute("lng", lng);
        model.addAttribute("lat", lat);
        String address = Optional.ofNullable(activity).map(Activity::getAddress).filter(StringUtils::isNotBlank).orElse("") +
                Optional.ofNullable(activity).map(Activity::getDetailAddress).filter(StringUtils::isNotBlank).orElse("");
        model.addAttribute("address", address);
        model.addAttribute("activity", activity);
        model.addAttribute("signWebDomain", DomainConstant.SIGN_WEB);
        return "proxy/map-location";
    }

}