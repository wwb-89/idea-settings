package com.chaoxing.activity.api.controller.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.dto.manager.sign.UserSignParticipationStatDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**广东外语外贸大学图书馆
 * @author wwb
 * @version ver 1.0
 * @className GdwywmLibraryMhApiController
 * @description
 * @blame wwb
 * @date 2021-07-22 19:00:27
 */
@RestController
@RequestMapping("mh/gdwywmlibrary")
public class GdwywmLibraryMhApiController {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private SignApiService signApiService;
    @Resource
    private ActivityValidationService activityValidationService;

    /**活动信息
     * @Description 
     * @author wwb
     * @Date 2021-07-22 19:08:42
     * @param activityId
     * @param data
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("activity/{activityId}/info")
    public RestRespDTO activityInfo(@PathVariable Integer activityId, @RequestBody(required = false) String data) {
        Integer uid = null;
        if (StringUtils.isNotBlank(data)) {
            JSONObject params = JSON.parseObject(data);
            uid = params.getInteger("uid");
        }
        Activity activity = activityQueryService.getById(activityId);
        boolean isManager = activityValidationService.isManageAble(activity, uid);
        JSONObject jsonObject = new JSONObject();
        List<MhGeneralAppResultDataDTO> mhGeneralAppResultDataDtos = Lists.newArrayList();
        jsonObject.put("results", mhGeneralAppResultDataDtos);
        // 报名、签到人数
        Integer signId = activity.getSignId();
        mhGeneralAppResultDataDtos.addAll(packageBtns(activity, signId, uid, isManager));
        return RestRespDTO.success(jsonObject);
    }

    /**封装按钮
     * @Description
     * @author wwb
     * @Date 2021-03-09 18:39:37
     * @param activity
     * @param signId
     * @param uid
     * @param isManager
     * @return java.util.List<com.chaoxing.activity.dto.mh.MhGeneralAppResultDataDTO>
     */
    private List<MhGeneralAppResultDataDTO> packageBtns(Activity activity, Integer signId, Integer uid, boolean isManager) {
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
        // 报名信息
        boolean existSignUp = CollectionUtils.isNotEmpty(signUpIds);
        boolean existSignUpInfo = false;
        boolean canSignUp = false;
        boolean signedUp = false;
        boolean showSignUpBtn = true;
        String signUpBtnName = "报名参加";
        if (existSignUp) {
            if (userSignParticipationStat.getSignedUp()) {
                // 已报名
                signedUp = true;
                existSignUpInfo = true;
                showSignUpBtn = false;
            } else if (userSignParticipationStat.getSignUpAudit()) {
                // 审核中
                signUpBtnName = "报名审核中";
                existSignUpInfo = true;
                showSignUpBtn = false;
            } else if (activityEnded && userSignParticipationStat.getSignUpEnded()) {
                // 活动和报名都结束的情况显示活动已结束
                signUpBtnName = "活动已结束";
            } else if (userSignParticipationStat.getSignUpEnded()) {
                signUpBtnName = "报名已结束";
            } else if (userSignParticipationStat.getSignUpNotStart()) {
                signUpBtnName = "报名未开始";
            } else if (!userSignParticipationStat.getInParticipationScope() && uid != null) {
                signUpBtnName = "不在参与范围内";
            } else if (userSignParticipationStat.getNoPlaces()) {
                signUpBtnName = "名额已满";
            } else {
                canSignUp = true;
            }
        }
        if (signedUp || !existSignUp) {
            if (CollectionUtils.isNotEmpty(signInIds)) {
                result.add(buildBtn("去签到", userSignParticipationStat.getSignInUrl()));
            }
            if (existSignUpInfo) {
                result.add(buildBtn("报名信息", userSignParticipationStat.getSignUpResultUrl()));
            }
        }
        // 是不是管理员
        if (isManager) {
            result.add(buildBtn("管理", activityQueryService.getActivityManageUrl(activity.getId())));
        }
        if (openWork && workId != null) {
            if (signedUp || !existSignUp) {
                result.add(buildBtn("提交作品", getWorkIndexUrl(workId)));
            }
        }
        if (existSignUp && showSignUpBtn) {
            if (canSignUp) {
                result.add(buildBtn(signUpBtnName, userSignParticipationStat.getSignUpUrl()));
            } else {
                result.add(buildBtn(signUpBtnName, ""));
            }

        }
        return result;
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

    private MhGeneralAppResultDataDTO buildBtn(String value, String url) {
        // 按钮
        MhGeneralAppResultDataDTO mhGeneralAppResultDataDto = new MhGeneralAppResultDataDTO();
        // 外部链接
        mhGeneralAppResultDataDto.setType(3);
        mhGeneralAppResultDataDto.setOrsUrl(url);
        mhGeneralAppResultDataDto.setPop(0);
        mhGeneralAppResultDataDto.setPopUrl("");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("results", Lists.newArrayList(mhGeneralAppResultDataDto));
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = Lists.newArrayList();
        mhGeneralAppResultDataDto.setFields(mhGeneralAppResultDataFields);
        mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key(value)
                .value(value)
                .flag("1")
                .build());
        return mhGeneralAppResultDataDto;
    }

}