package com.chaoxing.activity.api.controller.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
import com.chaoxing.activity.dto.manager.sign.UserSignParticipationStatDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityRating;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.activity.flag.ActivityFlagValidateService;
import com.chaoxing.activity.service.activity.rating.ActivityRatingQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.chaoxing.activity.util.enums.MhAppIconEnum;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/13 16:26
 * <p>
 */
@RestController
@RequestMapping("mh/v3")
public class ActivityMhV3ApiController {


    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityValidationService activityValidationService;
    @Resource
    private ActivityFlagValidateService activityFlagValidateService;
    @Resource
    private ActivityRatingQueryService activityRatingQueryService;

    @Resource
    private SignApiService signApiService;

    @RequestMapping("/activity/brief/info")
    public RestRespDTO briefInfo22(@RequestBody String data) {
        Activity activity = getActivityByData(data);
        List<MhGeneralAppResultDataDTO> mainFields = Lists.newArrayList();
        if (activity == null) {
            return RestRespDTO.success(mainFields);
        }
        // 开始结束时间
        buildField(activity.getCoverUrl(), "", DateUtils.activityTimeScope(activity.getStartTime(), activity.getEndTime()), buildCloudImgUrl(MhAppIconEnum.ONE.TIME_TRANSPARENT.getValue()), mainFields);
        String signedUpNumDescribe = "";
        if (activity.getSignId() != null) {
            SignStatDTO signStat = signApiService.getSignParticipation(activity.getSignId());
            signedUpNumDescribe = String.valueOf(signStat.getSignedUpNum());
            if (signStat.getLimitNum() != null && signStat.getLimitNum() > 0) {
                signedUpNumDescribe += "/" + signStat.getLimitNum();
            }
        }
        // 活动报名参与情况
        buildField(activity.getCoverUrl(), "已报名", signedUpNumDescribe, buildCloudImgUrl(MhAppIconEnum.ONE.SIGNED_UP_USER.getValue()), mainFields);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("results", mainFields);
        return RestRespDTO.success(jsonObject);
    }


    @RequestMapping("/activity/btns")
    public RestRespDTO briefInfo333(@RequestBody String data) {
        Activity activity = getActivityByData(data);
        if (activity == null) {
            return RestRespDTO.success();
        }
        JSONObject params = JSON.parseObject(data);
        Integer uid = params.getInteger("uid");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("results", packageBtns(activity, activity.getSignId(), uid));
        return RestRespDTO.success(jsonObject);
    }


    @RequestMapping("/activity/info")
    public RestRespDTO briefInfo444(@RequestBody String data) {
        Activity activity = getActivityByData(data);
        if (activity == null) {
            return RestRespDTO.success();
        }
        List<MhGeneralAppResultDataDTO> mainFields = Lists.newArrayList();
        // 主办方
        buildField(buildCloudImgUrl(MhAppIconEnum.ONE.ORGANISER.getValue()), "主办", activity.getOrganisers(), mainFields);
        // 地址
        String address = Optional.ofNullable(activity.getAddress()).orElse("") + Optional.ofNullable(activity.getDetailAddress()).orElse("");
        buildField(buildCloudImgUrl(MhAppIconEnum.ONE.LOCATION.getValue()), "地址", address, mainFields);
        // 报名时间
        if (activity.getSignId() != null) {
            SignStatDTO signStat = signApiService.getSignParticipation(activity.getSignId());
            String signUpTime = signStat.getSignUpStartTime() == null ? "" : DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(signStat.getSignUpStartTime());
            buildField(buildCloudImgUrl(MhAppIconEnum.ONE.TIME.getValue()), "报名时间", signUpTime, mainFields);
        }
        // 积分
        buildField(buildCloudImgUrl(MhAppIconEnum.ONE.INTEGRAL.getValue()), "积分", Optional.of(activity.getIntegral()).map(String::valueOf).orElse(""), mainFields);
        // 评价
        ActivityRating activityRating = activityRatingQueryService.getByActivityId(activity.getId());
        String ratingContent = Optional.ofNullable(activityRating.getScoreNum()).orElse(0) + "人；" + Optional.ofNullable(activityRating.getScore()).orElse(new BigDecimal(0)) + "分";
        buildFieldWithUrl(buildCloudImgUrl(MhAppIconEnum.ONE.RATING.getValue()), "评价", ratingContent, activityQueryService.getActivityRatingUrl(activity.getId()), mainFields);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("results", mainFields);
        return RestRespDTO.success(jsonObject);
    }



    /**封装按钮
     * @Description
     * @author wwb
     * @Date 2021-03-09 18:39:37
     * @param activity
     * @param signId
     * @param uid
     * @return java.util.List<com.chaoxing.activity.dto.mh.MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO>
     */
    private List<MhGeneralAppResultDataDTO> packageBtns(Activity activity, Integer signId, Integer uid) {
        List<MhGeneralAppResultDataDTO> result = Lists.newArrayList();
        Integer status = activity.getStatus();
        Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
        boolean activityEnded = Objects.equals(Activity.StatusEnum.ENDED, statusEnum);
        UserSignParticipationStatDTO userSignParticipationStat = signApiService.userParticipationStat(signId, uid);
        if (userSignParticipationStat == null) {
            return result;
        }
        List<Integer> signInIds = userSignParticipationStat.getSignInIds();
        List<Integer> signUpIds = userSignParticipationStat.getSignUpIds();
        Boolean openWork = activity.getOpenWork();
        openWork = Optional.ofNullable(openWork).orElse(Boolean.FALSE);
        Integer workId = activity.getWorkId();
        boolean isManager = activityValidationService.isManageAble(activity, uid);
        // 报名信息
        boolean existSignUp = CollectionUtils.isNotEmpty(signUpIds);
        boolean existSignUpInfo = false;
        if (existSignUp) {
            // 如果开启了学生报名则需要报名（报名任意一个报名）才能看见"进入会场"
            if (activityFlagValidateService.isDualSelect(activity)) {
                // 双选会
                List<SignUpCreateParamDTO> signUps = userSignParticipationStat.getSignUps();
                boolean openedStudengSignUp = false;
                if (CollectionUtils.isNotEmpty(signUps)) {
                    for (SignUpCreateParamDTO signUp : signUps) {
                        if (!Objects.equals(SignUpCreateParamDTO.CustomSignUpTypeEnum.DUAL_SELECT_COMPANY.getValue(), signUp.getCustomSignUpType())) {
                            openedStudengSignUp = true;
                            break;
                        }
                    }
                }
                if (openedStudengSignUp) {
                    // 必须要报名
                    if (userSignParticipationStat.getSignedUp()) {
                        buildBtnField("进入会场", getDualSelectIndexUrl(activity), "1", false, result);
                    }
                } else {
                    buildBtnField("进入会场", getDualSelectIndexUrl(activity), "1", false, result);
                }
            }
            if (userSignParticipationStat.getSignedUp()) {
                // 已报名
                if (CollectionUtils.isNotEmpty(signInIds)) {
                    buildBtnField("去签到", userSignParticipationStat.getSignInUrl(), "1", false, result);
                }
                if (openWork && workId != null && !isManager) {
                    buildBtnField("提交作品", getWorkIndexUrl(workId), "1", false, result);
                }
                existSignUpInfo = true;
            } else if (userSignParticipationStat.getSignUpAudit()) {
                // 审核中
                buildBtnField("报名审核中", "", "0", false, result);
                existSignUpInfo = true;
            } else if (activityEnded && userSignParticipationStat.getSignUpEnded()) {
                // 活动和报名都结束的情况显示活动已结束
                buildBtnField("活动已结束", "", "0", false, result);
            } else if (userSignParticipationStat.getSignUpEnded()) {
                buildBtnField("报名已结束", "", "0", false, result);
            } else if (userSignParticipationStat.getSignUpNotStart()) {
                buildBtnField("报名未开始", "", "0", false, result);
            } else if (!userSignParticipationStat.getInParticipationScope() && uid != null) {
                buildBtnField("不在参与范围内", "", "0", false, result);
            } else if (userSignParticipationStat.getNoPlaces()) {
                buildBtnField("名额已满", "", "0", false, result);
            } else {
                String showName = "报名参加";
                List<SignUpCreateParamDTO> signUps = userSignParticipationStat.getSignUps();
                boolean setSignUpBtn = Boolean.FALSE;
                if (signUpIds.size() == 1) {
                    SignUpCreateParamDTO signUp = signUps.get(0);
                    String btnName = signUp.getBtnName();
                    if (StringUtils.isNotBlank(btnName)) {
                        showName = btnName;
                    }
                    if (!signUps.get(0).getFillInfo()) {
                        setSignUpBtn = Boolean.TRUE;
                        buildBtnField(showName, UrlConstant.MH_AJAX_SIGN_UP,  "1", true, result);
                    }
                }
                if (!setSignUpBtn) {
                    buildBtnField(showName, userSignParticipationStat.getSignUpUrl(), "1", false, result);
                }
            }
        }else {
            if (activityFlagValidateService.isDualSelect(activity)) {
                buildBtnField("进入会场", getDualSelectIndexUrl(activity), "1", false, result);
            }
            if (CollectionUtils.isNotEmpty(signInIds)) {
                buildBtnField("去签到", userSignParticipationStat.getSignInUrl(), "1", false, result);
            }
            if (openWork && workId != null && !isManager) {
                buildBtnField("提交作品", getWorkIndexUrl(workId), "1", false, result);
            }
        }
        // 是不是管理员
        if (isManager) {
            if (openWork && workId != null) {
                buildBtnField("提交作品", getWorkIndexUrl(workId), "1", false, result);
            }
            buildBtnField("管理", activityQueryService.getActivityManageUrl(activity.getId()), "2", false, result);
        }
        // 评价
        Boolean openRating = activity.getOpenRating();
        openRating = Optional.ofNullable(openRating).orElse(Boolean.FALSE);
        if (openRating) {
            buildBtnField("评价", activityQueryService.getActivityRatingUrl(activity.getId()), "2", false, result);
        }
        if (existSignUpInfo) {
            buildBtnField("报名信息", userSignParticipationStat.getSignUpResultUrl(), "2", false, result);
        }
        return result;
    }


    private Activity getActivityByData(String data) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        JSONObject params = JSON.parseObject(data);
        Integer websiteId = params.getInteger("websiteId");
        // 根据websiteId查询活动id
        return activityQueryService.getByWebsiteId(websiteId);
    }

    private String buildCloudImgUrl(String cloudId) {
        return "http://p.ananas.chaoxing.com/star3/origin/" + cloudId;
    }





    /**获取双选会主页地址
     * @Description
     * @author wwb
     * @Date 2021-04-02 16:48:20
     * @param activity
     * @return java.lang.String
     */
    private String getDualSelectIndexUrl(Activity activity) {
        return String.format(UrlConstant.DUAL_SELECT_INDEX_URL, activity.getId(), activity.getCreateFid());
    }


    /**获取作品征集主页地址
     * @Description
     * @author wwb
     * @Date 2021-04-09 15:30:32
     * @param workId
     * @return java.lang.String
     */
    private String getWorkIndexUrl(Integer workId) {
        return String.format(UrlConstant.WORK_INDEX_URL, workId);
    }


    private void buildField(String coverUrl,
                                 String key,
                                 String value,
                                 String iconUrl,
                                 List<MhGeneralAppResultDataDTO> mainFields) {
        MhGeneralAppResultDataDTO item = MhGeneralAppResultDataDTO.buildDefault();
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
        Integer flag = 0;
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("封面")
                .flag(String.valueOf(flag))
                .value(coverUrl)
                .type("3")
                .build());
        String contentVal = StringUtils.isNotBlank(key) ? (key + "：" + value) : value;
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("内容")
                .flag(String.valueOf(++flag))
                .value(contentVal)
                .type("3")
                .build());
        if (StringUtils.isNotBlank(iconUrl)) {
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("图标")
                    .value(iconUrl)
                    .flag(String.valueOf(++flag))
                    .type("3")
                    .build());
        }
        item.setFields(fields);
        mainFields.add(item);
    }

    private void buildField(String iconUrl,
                                String key,
                                String value,
                                List<MhGeneralAppResultDataDTO> mainFields) {
        MhGeneralAppResultDataDTO item = MhGeneralAppResultDataDTO.buildDefault();
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
        int flag = 0;
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("图标")
                .value(iconUrl)
                .type("3")
                .flag(String.valueOf(flag))
                .build());
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("标题")
                .value(key)
                .type("3")
                .flag(String.valueOf(++flag))
                .build());
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("内容")
                .value(value)
                .type("3")
                .flag(String.valueOf(++flag))
                .build());
        item.setFields(fields);
        mainFields.add(item);
    }

    private void buildFieldWithUrl(String iconUrl, String key, String value, String osrUrl, List<MhGeneralAppResultDataDTO> mainFields) {
        MhGeneralAppResultDataDTO item = MhGeneralAppResultDataDTO.buildDefault();
        if (StringUtils.isNotBlank(osrUrl)) {
            item.setOrsUrl(osrUrl);
        }
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
        int flag = 0;
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("图标")
                .value(iconUrl)
                .type("3")
                .flag(String.valueOf(flag))
                .build());
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("标题")
                .value(key)
                .type("3")
                .flag(String.valueOf(++flag))
                .build());
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("内容")
                .value(value)
                .type("3")
                .flag(String.valueOf(++flag))
                .build());
        item.setFields(fields);
        mainFields.add(item);
    }

    private void buildBtnField(String key, String url, String type, boolean isAjax, List<MhGeneralAppResultDataDTO> result) {
        MhGeneralAppResultDataDTO item = MhGeneralAppResultDataDTO.buildDefault();
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
        int flag = 0;
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key(key)
                .orsUrl(isAjax ? url : "")
                .value(isAjax? "" : url)
                .type(isAjax ? "7" : "3")
                .flag(String.valueOf(flag))
                .build());
        if (StringUtils.isNotBlank(type)) {
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("按钮类型")
                    .value(type)
                    .type(isAjax ? "7" : "3")
                    .flag(String.valueOf(++flag))
                    .build());
        }
        item.setFields(fields);
        result.add(item);
    }

}
