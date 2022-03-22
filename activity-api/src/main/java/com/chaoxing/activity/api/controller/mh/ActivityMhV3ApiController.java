package com.chaoxing.activity.api.controller.mh;

import cn.hutool.http.HtmlUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.api.controller.enums.MhBtnSequenceEnum;
import com.chaoxing.activity.api.util.MhPreParamsUtils;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.dto.manager.sign.SignStatDTO;
import com.chaoxing.activity.dto.manager.sign.SignUpDTO;
import com.chaoxing.activity.dto.manager.sign.UserSignParticipationStatDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.stat.SignActivityStatDTO;
import com.chaoxing.activity.dto.work.WorkBtnDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.collection.ActivityCollectionQueryService;
import com.chaoxing.activity.service.activity.engine.CustomAppConfigQueryService;
import com.chaoxing.activity.service.activity.market.MarketSignupConfigService;
import com.chaoxing.activity.service.activity.rating.ActivityRatingQueryService;
import com.chaoxing.activity.service.activity.stat.ActivityStatQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.service.util.MhDataBuildUtil;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.chaoxing.activity.util.enums.MhAppIconEnum;
import com.chaoxing.activity.util.enums.SignUpBtnEnum;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/13 16:26
 * <p>
 */
@RestController
@RequestMapping("mh/v3")
@CrossOrigin
public class ActivityMhV3ApiController {

    private static final Pattern IMG_TAG_PATTERN = Pattern.compile("<img.*src\\s*=\\s*(.*?)[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern IMG_TAG_SRC_PATTERN = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)");

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private ActivityRatingQueryService activityRatingQueryService;
    @Resource
    private CloudApiService cloudApiService;
    @Resource
    private WorkApiService workApiService;
    @Resource
    private SignApiService signApiService;
    @Resource
    private ActivityStatQueryService activityStatQueryService;
    @Resource
    private ActivityCollectionQueryService activityCollectionQueryService;
    @Resource
    private MarketSignupConfigService marketSignupConfigService;
    @Resource
    private CustomAppConfigQueryService customAppConfigQueryService;

    private static final String EMPTY_INTRODUCTION_TEXT = "<span style=\"color: rgb(165, 165, 165);\">暂无介绍</span>";

    @RequestMapping("activity/brief/info")
    public RestRespDTO briefInfo(@RequestBody String data) {
        Activity activity = getActivityByData(data);
        MhGeneralAppResultDataDTO mainFields = MhGeneralAppResultDataDTO.buildDefault();
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
        mainFields.setFields(fields);
        JSONObject jsonObject = new JSONObject();
        if (activity == null) {
            jsonObject.put("results", mainFields);
            return RestRespDTO.success(jsonObject);
        }
        String signUpKeyword = getSignUpKeyword(activity.getMarketId());
        Map<String, String> fieldCodeNameMap = activityQueryService.getFieldCodeNameRelation(activity);
        // 封面
        buildField(0, "", "", "", fields);
        // 活动名称
        buildField(1, fieldCodeNameMap.getOrDefault("activity_name", "活动名称"), activity.getName(), "", fields);
        // 子标题
        buildField(2, "", "", "", fields);
        // 作者
        buildField(3, "", "", "", fields);
        // 简介
        buildField(4, "", "", "", fields);
        // 数值
        buildField(5, "", "", "", fields);
        // 发布时间
        buildField(6, "", "", "", fields);
        // 开始时间
        buildField(100, fieldCodeNameMap.getOrDefault("activity_time_scope", "活动时间"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getStartTime()), "", fields);;
        // 结束时间
        buildField(101, fieldCodeNameMap.getOrDefault("activity_time_scope", "活动时间"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getEndTime()), "", fields);;
        // 标签1
        buildField(102, "", "", "", fields);
        // 标签2
        buildField(103, "", "", "", fields);
        // 报名参与情况
        String signedUpNumDescribe = "";
        if (activity.getSignId() != null) {
            SignStatDTO signStat = signApiService.getSignParticipation(activity.getSignId());
            if (CollectionUtils.isNotEmpty(signStat.getSignUpIds())) {
                signedUpNumDescribe = String.valueOf(signStat.getSignedUpNum());
                if (signStat.getLimitNum() != null && signStat.getLimitNum() > 0) {
                    signedUpNumDescribe += "/" + signStat.getLimitNum();
                }
            }
        }

        // 已报名人数
        buildField(104, "已" + signUpKeyword, signedUpNumDescribe, "", fields);
        // 报名时间
        buildField(105, "", "", "", fields);
        jsonObject.put("results", Lists.newArrayList(mainFields));
        return RestRespDTO.success(jsonObject);
    }

    /**通用活动门户按钮数据源
     * @Description
     * @author huxiaolong
     * @Date 2022-02-14 15:31:32
     * @param data
     * @return
     */
    @RequestMapping("activity/btns")
    public RestRespDTO mhActivityBtns(@RequestBody String data) {
       return RestRespDTO.success(packageBtns(data, false));
    }

    /**厦门门户活动按钮数据源
     * @Description
     * @author huxiaolong
     * @Date 2022-02-14 15:31:45
     * @param data
     * @return
     */
    @RequestMapping("xm/activity/btns")
    public RestRespDTO xmMhActivityBtns(@RequestBody String data) {
        return RestRespDTO.success(packageBtns(data, true));
    }
    
    public JSONObject packageBtns(String data, Boolean isMultiOrg) {
        Activity activity = getActivityByData(data);
        JSONObject jsonObject = new JSONObject();
        if (activity == null) {
            jsonObject.put("results", Lists.newArrayList());
            return jsonObject;
        }
        JSONObject params = JSON.parseObject(data);
        Integer uid = params.getInteger("uid");
        Integer wfwfid = params.getInteger("wfwfid");
        // 获取需要排序的按钮名称列表
        String preParams = params.getString("preParams");
        JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
        // 状态
        String excludeBtnNamesStr = urlParams.getString("excludeBtnNames");
        List<String> excludeBtnNames = MhPreParamsUtils.resolveStringV(excludeBtnNamesStr);

        jsonObject.put("results", packageBtns(activity, uid, wfwfid, excludeBtnNames, isMultiOrg));
        return jsonObject;
    }


    @RequestMapping("activity/info")
    public RestRespDTO activityInfo(@RequestBody String data) {
        Activity activity = getActivityByData(data);
        JSONObject jsonObject = new JSONObject();
        if (activity == null) {
            jsonObject.put("results", Lists.newArrayList());
            return RestRespDTO.success(jsonObject);
        }
        String signUpKeyword = getSignUpKeyword(activity.getMarketId());
        Map<String, String> fieldCodeNameMap = activityQueryService.getFieldCodeNameRelation(activity);
        List<MhGeneralAppResultDataDTO> mainFields = Lists.newArrayList();
        // 主办方
        if (StringUtils.isNotBlank(activity.getOrganisers())) {
            MhDataBuildUtil.buildField(cloudApiService.buildImageUrl(MhAppIconEnum.ONE.ORGANISER.getValue()), fieldCodeNameMap.getOrDefault("activity_organisers", "主办方"), activity.getOrganisers(), mainFields);
        }
        // 地址
        String address = "";
        // 经纬度不为空时才显示地址
        String activityAddressLink = "";
        if (activity.getLongitude() != null && activity.getDimension() != null) {
            activityAddressLink = DomainConstant.API +  "/redirect/activity/"+ activity.getId() +"/address";
        }
        if (!Objects.equals(Activity.ActivityTypeEnum.ONLINE.getValue(), activity.getActivityType())) {
            address = Optional.ofNullable(activity.getAddress()).orElse("") + Optional.ofNullable(activity.getDetailAddress()).orElse("");
        }
        if (StringUtils.isNotBlank(address)) {
            MhDataBuildUtil.buildFieldWithOsrUrl(cloudApiService.buildImageUrl(MhAppIconEnum.ONE.LOCATION.getValue()), "地址", address, activityAddressLink, mainFields);

        }
        // 报名时间
        if (activity.getSignId() != null) {
            SignStatDTO signStat = signApiService.getSignParticipation(activity.getSignId());
            if (CollectionUtils.isNotEmpty(signStat.getSignUpIds())) {
                MhDataBuildUtil.buildField(cloudApiService.buildImageUrl(MhAppIconEnum.ONE.SIGN_TIME_TIME.getValue()), fieldCodeNameMap.getOrDefault("sign_up_time_scope", signUpKeyword + "时间"),  DateUtils.activityTimeScope(signStat.getSignUpStartTime(), signStat.getSignUpEndTime()), mainFields);
            }
        }
        // 积分
        if (activity.getIntegral() != null && activity.getIntegral().compareTo(new BigDecimal(0)) != 0) {
            MhDataBuildUtil.buildField(cloudApiService.buildImageUrl(MhAppIconEnum.ONE.INTEGRAL.getValue()), fieldCodeNameMap.getOrDefault("integral", "积分"), Optional.ofNullable(activity.getIntegral()).map(String::valueOf).orElse(""), mainFields);
        }
        // 评价
        Boolean openRating = Optional.ofNullable(activity.getOpenRating()).orElse(false);
        String ratingContent = "";
        if (openRating) {
            ActivityRating activityRating = activityRatingQueryService.getByActivityId(activity.getId());
            if (activityRating != null) {
                ratingContent = Optional.ofNullable(activityRating.getScoreNum()).orElse(0) + "人；" + Optional.ofNullable(activityRating.getScore()).orElse(new BigDecimal(0)) + "分";
            } else {
                ratingContent = "0人；0分";
            }
            MhDataBuildUtil.buildFieldWithOsrUrl(cloudApiService.buildImageUrl(MhAppIconEnum.ONE.RATING_1.getValue()), fieldCodeNameMap.getOrDefault("activity_rating", "评价"), ratingContent, activity.getRatingUrl(), mainFields);
        }
        jsonObject.put("results", mainFields);
        return RestRespDTO.success(jsonObject);
    }


    /**活动相关统计数据接口
    * @Description
    * @author huxiaolong
    * @Date 2021-09-18 16:29:02
    * @param data
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("activity/stat/info")
    public RestRespDTO activityStatInfo(@RequestBody String data) {
        Activity activity = getActivityByData(data);
        List<MhGeneralAppResultDataDTO> mainFields = Lists.newArrayList();
        JSONObject jsonObject = new JSONObject();
        if (activity == null) {
            jsonObject.put("results", mainFields);
            return RestRespDTO.success(jsonObject);
        }
        String signUpKeyword = getSignUpKeyword(activity.getMarketId());
        // 获取浏览量
        String startTimeStr = activity.getStartTime().format(DateUtils.FULL_TIME_FORMATTER);
        String endTimeStr = activity.getEndTime().format(DateUtils.FULL_TIME_FORMATTER);
        String pvNum = Optional.ofNullable(activityStatQueryService.getPvByActivity(activity)).map(String::valueOf).orElse("0");
        // 获取收藏量
        Integer collectedNum = Optional.ofNullable(activityCollectionQueryService.listCollectedUid(activity.getId())).orElse(Lists.newArrayList()).size();
        // 获取报名量
        SignActivityStatDTO signActivityStat = signApiService.singleActivityStat(activity.getSignId(), startTimeStr, endTimeStr);
        String signedUpNum = "0";
        if (signActivityStat != null) {
            signedUpNum = Optional.ofNullable(signActivityStat.getSignedUpNum()).map(String::valueOf).orElse("0");
        }
        MhDataBuildUtil.buildField(cloudApiService.buildImageUrl(MhAppIconEnum.ONE.BROWSE.getValue()), "浏览", pvNum , mainFields);
        MhDataBuildUtil.buildField(cloudApiService.buildImageUrl(MhAppIconEnum.ONE.COLLECTED.getValue()), "收藏", String.valueOf(collectedNum), mainFields);
        MhDataBuildUtil.buildField(cloudApiService.buildImageUrl(MhAppIconEnum.ONE.SIGNED_UP_NUM.getValue()), signUpKeyword, signedUpNum, mainFields);

        jsonObject.put("results", mainFields);
        return RestRespDTO.success(jsonObject);
    }

    /**活动简介
    * @Description
    * @author huxiaolong
    * @Date 2021-09-15 18:38:59
    * @param data
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("activity/introduction")
    public RestRespDTO activityIntroduction(@RequestBody String data) {
        Activity activity = getActivityByData(data);
        JSONObject jsonObject = new JSONObject();
        List<MhGeneralAppResultDataDTO> mainFields = Lists.newArrayList();
        if (activity == null) {
            jsonObject.put("results", mainFields);
            return RestRespDTO.success(jsonObject);
        }

        ActivityDetail activityDetail = activityQueryService.getDetailByActivityId(activity.getId());
        if (activityDetail != null && StringUtils.isNotBlank(activityDetail.getIntroduction())) {
            MhGeneralAppResultDataDTO mhGeneralAppResultData = MhGeneralAppResultDataDTO.buildDefault();
            mhGeneralAppResultData.setOrsUrl("");
            String introductionHtml = StringUtils.isNotBlank(activityDetail.getIntroduction()) ? activityDetail.getIntroduction() : EMPTY_INTRODUCTION_TEXT;
            String introductionText = HtmlUtil.cleanHtmlTag(introductionHtml).replaceAll(HtmlUtil.NBSP, " ");
            String firstImg = filterFirstImgFromHtmlStr(introductionHtml);

            mhGeneralAppResultData.setContent(introductionHtml);
            List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
            Integer flag = 0;
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("封面")
                    .flag(String.valueOf(flag))
                    .value(StringUtils.isNotBlank(firstImg) ? firstImg : "")
                    .type("3")
                    .build());
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("内容")
                    .flag(String.valueOf(++flag))
                    .value(introductionText)
                    .type("3")
                    .build());
            mhGeneralAppResultData.setType(1);
            mhGeneralAppResultData.setFields(fields);
            mainFields.add(mhGeneralAppResultData);
        }
        jsonObject.put("results", mainFields);
        return RestRespDTO.success(jsonObject);
    }

    /**门户简介详情
    * @Description
    * @author huxiaolong
    * @Date 2021-09-15 18:38:43
    * @param data
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("activity/introduction/detail")
    public RestRespDTO activityIntroductionDetail(@RequestBody String data) {
        Activity activity = getActivityByData(data);
        JSONObject jsonObject = new JSONObject();
        List<MhGeneralAppResultDataDTO> mainFields = Lists.newArrayList();
        if (activity == null) {
            jsonObject.put("results", mainFields);
            return RestRespDTO.success(jsonObject);
        }

        ActivityDetail activityDetail = activityQueryService.getDetailByActivityId(activity.getId());
        if (activityDetail != null) {
            MhGeneralAppResultDataDTO mhGeneralAppResultData = MhGeneralAppResultDataDTO.buildDefault();
            mhGeneralAppResultData.setContent(activityDetail.getIntroduction());
            mainFields.add(mhGeneralAppResultData);
        }
        jsonObject.put("results", mainFields);
        return RestRespDTO.success(jsonObject);
    }


    /**门户报名
    * @Description
    * @author huxiaolong
    * @Date 2021-09-15 18:35:17
    * @param uid
    * @param websiteId
    * @param fid
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("sign-up")
    public RestRespDTO mhSignUp(@RequestParam("uid") Integer uid, @RequestParam("websiteId") Integer websiteId, @RequestParam("fid") Integer fid) {
        Integer signId = activityQueryService.getByWebsiteId(websiteId).getSignId();
        List<SignUpDTO> signUps = signApiService.getById(signId).getSignUps();
        return signApiService.mhSignUp(signUps.get(0).getId(), uid, fid);
    }

    private String filterFirstImgFromHtmlStr(String htmlStr) {
        Set<String> pics = new HashSet<>();
        Matcher imageTag = IMG_TAG_PATTERN.matcher(htmlStr);
        while (imageTag.find()) {
            // 得到<img />数据
            String img = imageTag.group();
            // 匹配<img>中的src数据
            Matcher m = IMG_TAG_SRC_PATTERN.matcher(img);
            while (m.find()) {
                pics.add(m.group(1));
            }
        }
        if (CollectionUtils.isNotEmpty(pics)) {
            return Lists.newArrayList(pics).get(0);
        }
        return null;

    }


    /**封装按钮
     * @Description
     * @author wwb
     * @Date 2021-03-09 18:39:37
     * @param activity
     * @param uid
     * @param wfwfid
     * @param excludeBtnNames 排除的按钮名称
     * @param isMultiOrg 是否多机构(厦门项目报名多机构选择)
     * @return java.util.List<com.chaoxing.activity.dto.mh.MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO>
     */
    private List<MhGeneralAppResultDataDTO> packageBtns(Activity activity, Integer uid, Integer wfwfid, List<String> excludeBtnNames, Boolean isMultiOrg) {
        Integer signId = activity.getSignId();
        List<MhGeneralAppResultDataDTO> result = Lists.newArrayList();
        Integer status = activity.getStatus();
        Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
        boolean activityEnded = Objects.equals(Activity.StatusEnum.ENDED, statusEnum);
        UserSignParticipationStatDTO userSignParticipationStat = signApiService.userParticipationStat(signId, uid);
        if (userSignParticipationStat == null) {
            return result;
        }
        Integer activityId = activity.getId();
        Integer activityCreateFid = activity.getCreateFid();
        List<Integer> signInIds = userSignParticipationStat.getSignInIds();
        List<Integer> formCollectionIds = userSignParticipationStat.getFormCollectionIds();
        List<Integer> signUpIds = userSignParticipationStat.getSignUpIds();
        Boolean openWork = activity.getOpenWork();
        openWork = Optional.ofNullable(openWork).orElse(Boolean.FALSE);
        Integer workId = activity.getWorkId();
        /*boolean isManager = activityValidationService.isManageAble(activity, uid);*/
        boolean existSignUp = CollectionUtils.isNotEmpty(signUpIds);
        boolean signedUp = true;
        // 报名信息
        boolean existSignUpInfo = false;
        String signUpKeyword = getSignUpKeyword(activity.getMarketId());
        if (existSignUp) {
            // 如果开启了学生报名则需要报名（报名任意一个报名）才能看见"进入会场"
            if (activity.isDualSelect()) {
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
                        result.add(MhDataBuildUtil.buildBtnField("进入会场", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), UrlConstant.getDualSelectIndexUrl(activityId, activityCreateFid), "1", false, MhBtnSequenceEnum.SIGN_IN.getSequence()));
                    }
                } else {
                    result.add(MhDataBuildUtil.buildBtnField("进入会场", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), UrlConstant.getDualSelectIndexUrl(activityId, activityCreateFid), "1", false, MhBtnSequenceEnum.SIGN_IN.getSequence()));
                }
            }
            if (userSignParticipationStat.getSignedUp()) {
                // 已报名
                if (CollectionUtils.isNotEmpty(signInIds)) {
                    result.add(MhDataBuildUtil.buildBtnField("去签到", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.SIGN_IN.getValue()), userSignParticipationStat.getSignInUrl(), "1", false, MhBtnSequenceEnum.SIGN_IN.getSequence()));
                }
                if (CollectionUtils.isNotEmpty(formCollectionIds)) {
                    result.add(MhDataBuildUtil.buildBtnField("填写表单", cloudApiService.buildImageUrl(MhAppIconEnum.ONE.DEFAULT_ICON.getValue()), userSignParticipationStat.getFormCollectionUrl(), "1", false, MhBtnSequenceEnum.FORM_COLLECTION.getSequence()));
                }
                existSignUpInfo = true;
            } else{
                signedUp = false;
                if (userSignParticipationStat.getSignUpAudit()) {
                    // 审核中
                    result.add(MhDataBuildUtil.buildBtnField(signUpKeyword +"审核中", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), "", "0", false, MhBtnSequenceEnum.SIGN_UP.getSequence()));
                    existSignUpInfo = true;
                } else if (activityEnded && userSignParticipationStat.getSignUpEnded()) {
                    // 活动和报名都结束的情况显示活动已结束
                    result.add(MhDataBuildUtil.buildBtnField("活动已结束", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), "", "0", false, MhBtnSequenceEnum.ACTIVITY.getSequence()));
                } else if (userSignParticipationStat.getSignUpEnded()) {
                    result.add(MhDataBuildUtil.buildBtnField(signUpKeyword + "已结束", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), "", "0", false, MhBtnSequenceEnum.SIGN_UP.getSequence()));
                } else if (userSignParticipationStat.getSignUpNotStart()) {
                    result.add(MhDataBuildUtil.buildBtnField(signUpKeyword + "未开始", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), "", "0", false, MhBtnSequenceEnum.SIGN_UP.getSequence()));
                } else if (userSignParticipationStat.getNoPlaces()) {
                    result.add(MhDataBuildUtil.buildBtnField("名额已满", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), "", "0", false, MhBtnSequenceEnum.SIGN_UP.getSequence()));
                } else if (!userSignParticipationStat.getInParticipationScope() && uid != null) {
                    result.add(MhDataBuildUtil.buildBtnField("不在参与范围内", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), "", "0", false, MhBtnSequenceEnum.SIGN_UP.getSequence()));
                } else {
                    String showName = signUpKeyword + "参加";
                    List<SignUpCreateParamDTO> signUps = userSignParticipationStat.getSignUps();
                    boolean setSignUpBtn = Boolean.FALSE;
                    if (signUpIds.size() == 1) {
                        SignUpCreateParamDTO signUp = signUps.get(0);
                        String btnName = signUp.getBtnName();
                        if (StringUtils.isNotBlank(btnName)) {
                            showName = btnName;
                        }
                        if (!signUps.get(0).getFillInfo() && uid != null) {
                            setSignUpBtn = Boolean.TRUE;
                            result.add(MhDataBuildUtil.buildBtnField(showName, cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), UrlConstant.MH_AJAX_SIGN_UP,  "1", true, MhBtnSequenceEnum.SIGN_UP.getSequence()));
                        }
                    }
                    if (!setSignUpBtn) {
                        userSignParticipationStat.handleSignUpUrl(isMultiOrg);
                        result.add(MhDataBuildUtil.buildBtnField(showName, cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), userSignParticipationStat.getSignUpUrl(), "1", false, MhBtnSequenceEnum.SIGN_UP.getSequence()));
                    }
                }
            }
        } else {
            if (activity.isDualSelect()) {
                result.add(MhDataBuildUtil.buildBtnField("进入会场", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), UrlConstant.getDualSelectIndexUrl(activityId, activityCreateFid), "1", false, MhBtnSequenceEnum.SIGN_IN.getSequence()));
            }
            if (CollectionUtils.isNotEmpty(signInIds)) {
                result.add(MhDataBuildUtil.buildBtnField("去签到", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.SIGN_IN.getValue()), userSignParticipationStat.getSignInUrl(), "1", false, MhBtnSequenceEnum.SIGN_IN.getSequence()));
            }
            if (CollectionUtils.isNotEmpty(formCollectionIds)) {
                result.add(MhDataBuildUtil.buildBtnField("填写表单", cloudApiService.buildImageUrl(MhAppIconEnum.ONE.DEFAULT_ICON.getValue()), userSignParticipationStat.getFormCollectionUrl(), "1", false, MhBtnSequenceEnum.FORM_COLLECTION.getSequence()));
            }
        }
        if (openWork && workId != null) {
            List<WorkBtnDTO> workBtnDtos = workApiService.listBtns(workId, uid, wfwfid);
            for (WorkBtnDTO workBtnDto : workBtnDtos) {
                Boolean enable = Optional.ofNullable(workBtnDto.getEnable()).orElse(false);
                Boolean needValidate = Optional.ofNullable(workBtnDto.getNeedValidate()).orElse(false);
                if (needValidate && !signedUp) {
                    continue;
                }
                String buttonIcon;
                String btnName = workBtnDto.getButtonName();
                if (Objects.equals(btnName, "我的作品")) {
                    buttonIcon = cloudApiService.buildImageUrl(MhAppIconEnum.THREE.MY_WORK.getValue());
                } else if (Objects.equals(btnName, "全部作品")) {
                    buttonIcon = cloudApiService.buildImageUrl(MhAppIconEnum.THREE.ALL_WORK.getValue());
                } else if (/*Objects.equals(btnName, "征集管理") || */Objects.equals(btnName, "提交作品")) {
                    buttonIcon = cloudApiService.buildImageUrl(MhAppIconEnum.THREE.SUBMIT_WORK.getValue());
                } else if (Objects.equals(btnName, "作品审核")) {
                    buttonIcon = cloudApiService.buildImageUrl(MhAppIconEnum.THREE.WORK_REVIEW.getValue());
                } else if (Objects.equals(btnName, "作品优选")) {
                    buttonIcon = cloudApiService.buildImageUrl(MhAppIconEnum.THREE.WORK_PREFERRED_SELECTION.getValue());
                } else {
                    buttonIcon = cloudApiService.buildImageUrl(MhAppIconEnum.ONE.DEFAULT_ICON.getValue());
                }
                result.add(MhDataBuildUtil.buildBtnField(btnName, buttonIcon, workBtnDto.getLinkUrl(), enable ? "1" : "0", false, MhBtnSequenceEnum.WORK.getSequence()));
            }
        }
        // 讨论小组
        Boolean openGroup = Optional.ofNullable(activity.getOpenGroup()).orElse(false);
        String groupBbsid = activity.getGroupBbsid();
        if (openGroup && StringUtils.isNotBlank(groupBbsid) && signedUp) {
            result.add(MhDataBuildUtil.buildBtnField("讨论小组", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.UNIVERSAL.getValue()), UrlConstant.getGroupUrl(groupBbsid), "1", false, MhBtnSequenceEnum.GROUP.getSequence()));
        }
        // 是不是管理员
        /*if (isManager) {
            result.add(MhDataBuildUtil.buildBtnField("管理", cloudApiService.buildImageUrl(MhAppIconEnum.ONE.MANAGE_TRANSPARENT.getValue()), activity.getManageUrl(), "1", false, MhBtnSequenceEnum.MANAGE.getSequence()));
        }*/
        // 评价
        Boolean openRating = activity.getOpenRating();
        openRating = Optional.ofNullable(openRating).orElse(Boolean.FALSE);
        if (openRating) {
            result.add(MhDataBuildUtil.buildBtnField("评价", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.RATING.getValue()), activity.getRatingUrl(), "1", false, MhBtnSequenceEnum.RATING.getSequence()));
        }
        if (existSignUpInfo) {
            result.add(MhDataBuildUtil.buildBtnField(signUpKeyword + "信息", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.SIGN_UP_INFO.getValue()), userSignParticipationStat.getSignUpResultUrl(), "1", false, MhBtnSequenceEnum.SIGN_UP_INFO.getSequence()));
        }
        // 阅读测评
        Boolean openReading = Optional.ofNullable(activity.getOpenReading()).orElse(false);
        if (openReading && activity.getReadingId() != null) {
            result.add(MhDataBuildUtil.buildBtnField("阅读测评", cloudApiService.buildImageUrl(MhAppIconEnum.THREE.READING_TEST.getValue()), UrlConstant.getReadingTestUrl(activity.getReadingId(), activity.getReadingModuleId()), "1", false, MhBtnSequenceEnum.READING_TEST.getSequence()));
        }
        // 班级互动
        Boolean openClazzInteraction = Optional.ofNullable(activity.getOpenClazzInteraction()).orElse(false);
        if (openClazzInteraction && signedUp) {
            result.add(MhDataBuildUtil.buildBtnField("进入主页", cloudApiService.buildImageUrl(MhAppIconEnum.ONE.DEFAULT_ICON.getValue()), DomainConstant.XIAMEN_TRAINING_PLATFORM_API + "/activity/detail?id=" + activityId, "1", false, MhBtnSequenceEnum.ACTIVITY.getSequence()));
        }
        // 查询自定义配置列表中的前端按钮地址
        List<CustomAppConfig> frontendAppConfigs = customAppConfigQueryService.listFrontendAppConfigsByActivity(activityId);
        for (CustomAppConfig config : frontendAppConfigs) {
            boolean showAfterSignUp = Optional.ofNullable(config.getShowAfterSignUp()).orElse(false);
            String iconCloudUrl = StringUtils.isBlank(config.getDefaultIconCloudId()) ? "" : cloudApiService.buildImageUrl(config.getDefaultIconCloudId());
            String url = Optional.ofNullable(config.getUrl()).orElse("");
            if (StringUtils.isNotBlank(url)) {
                url += "?activityId=" + activityId + "&uid=" + uid + "&state=" + activityCreateFid;
            }
            if (showAfterSignUp) {
                if (signedUp) {
                    result.add(MhDataBuildUtil.buildBtnField(config.getTitle(), iconCloudUrl, url, "1", false, MhBtnSequenceEnum.CUSTOM_APP.getSequence()));
                }
            } else {
                result.add(MhDataBuildUtil.buildBtnField(config.getTitle(), iconCloudUrl, url, "1", false, MhBtnSequenceEnum.CUSTOM_APP.getSequence()));
            }
        }
        // 排序
        result.sort(Comparator.comparingInt(MhGeneralAppResultDataDTO::getSequence));
        // 删除排除的按钮
        if (excludeBtnNames == null) {
            excludeBtnNames = Lists.newArrayList();
        }
        Iterator<MhGeneralAppResultDataDTO> iterator = result.iterator();
        while (iterator.hasNext()) {
            MhGeneralAppResultDataDTO next = iterator.next();
            List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = next.getFields();
            boolean matchExclude = false;
            if (CollectionUtils.isNotEmpty(fields)) {
                for (MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO field : fields) {
                    if (excludeBtnNames.contains(field.getValue())) {
                        matchExclude = true;
                        break;
                    }
                }
            }
            if (matchExclude) {
                iterator.remove();
            }
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

    private String getSignUpKeyword(Integer marketId) {
        String signUpKeyword = SignUpBtnEnum.BTN_1.getKeyword();
        if (marketId == null) {
            return signUpKeyword;
        }
        MarketSignUpConfig marketSignUpConfig = marketSignupConfigService.get(marketId);
        if (marketSignUpConfig != null && StringUtils.isNotBlank(marketSignUpConfig.getSignUpKeyword())) {
            signUpKeyword = marketSignUpConfig.getSignUpKeyword();
        }
        return signUpKeyword;
    }

    private void buildField(Integer flag,
                            String key,
                            String value,
                            String osrUrl,
                            List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields) {
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key(key)
                .flag(String.valueOf(flag))
                .value(value)
                .orsUrl(osrUrl)
                .type("3")
                .build());
    }

}
