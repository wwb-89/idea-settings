package com.chaoxing.activity.api.controller.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.api.controller.enums.MhBtnSequenceEnum;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.dto.work.WorkBtnDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.util.constant.ActivityMhUrlConstant;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.enums.MhAppIconEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**鄂尔多斯活动信息
 * @author wwb
 * @version ver 1.0
 * @className ErdosActivityInfoApiController
 * @description
 * @blame wwb
 * @date 2021-09-06 17:45:15
 */
@RestController
@RequestMapping("mh/erdos")
public class ErdosActivityInfoApiController {

    private static final Integer MULTI_BTN_MAX_FLAG = 115;

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private WorkApiService workApiService;
    @Resource
    private CloudApiService cloudApiService;

    /**活动信息
     * @Description 
     * @author wwb
     * @Date 2021-09-06 17:46:47
     * @param data
     * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("activity/info")
    public RestRespDTO activityInfo(@RequestBody String data) {
        JSONObject params = JSON.parseObject(data);
        Activity activity = getActivityByParams(params);
        Integer activityId = activity.getId();
        Integer uid = params.getInteger("uid");
        Integer wfwfid = params.getInteger("wfwfid");
        Map<String, String> fieldCodeNameRelation = activityQueryService.getFieldCodeNameRelation(activity);
        JSONObject jsonObject = new JSONObject();
        MhGeneralAppResultDataDTO mhGeneralAppResultDataDTO = new MhGeneralAppResultDataDTO();
        mhGeneralAppResultDataDTO.setType(3);
        mhGeneralAppResultDataDTO.setOrsUrl("");
        mhGeneralAppResultDataDTO.setPop(0);
        mhGeneralAppResultDataDTO.setPopUrl("");
        jsonObject.put("results", Lists.newArrayList(mhGeneralAppResultDataDTO));
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = Lists.newArrayList();
        // 活动名称
        mhGeneralAppResultDataFields.add(buildField("", "", "0"));
        mhGeneralAppResultDataFields.add(buildField(fieldCodeNameRelation.get("activity_name"), activity.getName(), "1"));
        mhGeneralAppResultDataFields.add(buildField("", "", "2"));
        mhGeneralAppResultDataFields.add(buildField("", "", "3"));
        mhGeneralAppResultDataFields.add(buildField("", "", "4"));
        mhGeneralAppResultDataFields.add(buildField("", "", "5"));
        mhGeneralAppResultDataFields.add(buildField("", "", "6"));
        // 开始时间
        mhGeneralAppResultDataFields.add(buildField(fieldCodeNameRelation.get("activity_time_scope"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getStartTime()), "100"));
        // 结束时间
        mhGeneralAppResultDataFields.add(buildField("活动结束时间", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getEndTime()), "101"));
        // 活动地点
        String activityAddress = "";
        if (Objects.equals(Activity.ActivityTypeEnum.OFFLINE.getValue(), activity.getActivityType())) {
            activityAddress = Optional.ofNullable(activity.getAddress()).orElse("") + Optional.ofNullable(activity.getDetailAddress()).orElse("");
        }
        mhGeneralAppResultDataFields.add(buildField("", "", "102"));
        mhGeneralAppResultDataFields.add(buildField("", "", "103"));
        mhGeneralAppResultDataFields.add(buildField("活动地点", activityAddress, "104"));
        // 主办方
        mhGeneralAppResultDataFields.add(buildField(fieldCodeNameRelation.get("activity_organisers"), activity.getOrganisers(), "105"));
        mhGeneralAppResultDataFields.add(buildField("", "", "106"));
        mhGeneralAppResultDataFields.add(buildField("", "", "107"));
        mhGeneralAppResultDataFields.add(buildField("", "", "108"));
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> btns;
        List<Integer> availableFlags = Lists.newArrayList(109, 111, 113, 115, 116);
        Integer workId = activity.getWorkId();
        Integer readingId = activity.getReadingId();
        if (workId != null) {
            // 作品征集定制
            List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> workBtns = listWorkBtn(uid, wfwfid, workId, availableFlags);
            mhGeneralAppResultDataFields.addAll(workBtns);
        }
        if (Optional.ofNullable(activity.getOpenReading()).orElse(false) && readingId != null) {
            String flag = getFlag(availableFlags);
            mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("阅读测评")
                    .value(getReadingTestUrl(activity))
                    .flag(flag)
                    .build());
            Integer intFlag = Integer.parseInt(flag);
            if (intFlag.compareTo(MULTI_BTN_MAX_FLAG) < 0) {
                mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                        .value("1")
                        .flag(String.valueOf(intFlag + 1))
                        .build());
            }
        }
        // 没使用完的按钮
        String remainFlag = getFlag(availableFlags);
        while (StringUtils.isNotBlank(remainFlag)) {
            mhGeneralAppResultDataFields.add(buildField("", "", remainFlag));
            remainFlag = getFlag(availableFlags);
        }
        String activityAddressLink = DomainConstant.API_DOMAIN +  "/redirect/activity/"+ activityId +"/address";
        // 活动地点链接（线下的活动有）
        mhGeneralAppResultDataFields.add(buildField("活动地点链接", activityAddressLink, "117"));
        mhGeneralAppResultDataFields.add(buildField("", "", "118"));
        // 海报
        mhGeneralAppResultDataFields.add(buildField("海报", "海报", "130"));
        mhGeneralAppResultDataFields.add(buildField("海报", String.format(ActivityMhUrlConstant.ACTIVITY_POSTERS_URL, activity.getId()), "131"));
        mhGeneralAppResultDataDTO.setFields(mhGeneralAppResultDataFields);
        return RestRespDTO.success(jsonObject);
    }

    /**鄂尔多斯门户按钮
     * @Description
     * @author huxiaolong
     * @Date 2021-12-06 14:51:56
     * @param data
     * @return
     */
    @RequestMapping("activity/btns")
    public RestRespDTO mhActivityBtns(@RequestBody String data) {
        JSONObject params = JSON.parseObject(data);
        Activity activity = getActivityByParams(params);
        JSONObject jsonObject = new JSONObject();
        if (activity == null) {
            jsonObject.put("results", Lists.newArrayList());
            return RestRespDTO.success(jsonObject);
        }
        Integer uid = params.getInteger("uid");
        Integer wfwfid = params.getInteger("wfwfid");
        jsonObject.put("results", packageWorkBtns(activity, uid, wfwfid));
        return RestRespDTO.success(jsonObject);
    }

    private Activity getActivityByParams(JSONObject params) {
        Integer websiteId = params.getInteger("websiteId");
        // 根据websiteId查询活动id
        Activity activity = activityQueryService.getByWebsiteId(websiteId);
        Optional.ofNullable(activity).orElseThrow(() -> new BusinessException("活动不存在"));
        return activity;
    }


    /**封装鄂尔多斯作品征集按钮
     * @Description
     * @author wwb
     * @Date 2021-03-09 18:39:37
     * @param activity
     * @param uid
     * @param wfwfid
     * @return java.util.List<com.chaoxing.activity.dto.mh.MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO>
     */
    private List<MhGeneralAppResultDataDTO> packageWorkBtns(Activity activity, Integer uid, Integer wfwfid) {
        List<MhGeneralAppResultDataDTO> result = Lists.newArrayList();
        Boolean openWork = activity.getOpenWork();
        openWork = Optional.ofNullable(openWork).orElse(Boolean.FALSE);
        Integer workId = activity.getWorkId();

        if (openWork && workId != null) {
            List<WorkBtnDTO> workBtnDtos = workApiService.listErdosBtns(workId, uid, wfwfid);
            for (WorkBtnDTO workBtnDto : workBtnDtos) {
                Boolean enable = Optional.ofNullable(workBtnDto.getEnable()).orElse(false);
                String buttonIcon = "";
                String btnName = workBtnDto.getButtonName();
                if (Objects.equals(btnName, "我的作品")) {
                    buttonIcon = cloudApiService.buildImageUrl(MhAppIconEnum.THREE.MY_WORK.getValue());
                } else if (Objects.equals(btnName, "全部作品")) {
                    buttonIcon = cloudApiService.buildImageUrl(MhAppIconEnum.THREE.ALL_WORK.getValue());
                } else if (Objects.equals(btnName, "征集管理") || Objects.equals(btnName, "提交作品")) {
                    buttonIcon = cloudApiService.buildImageUrl(MhAppIconEnum.THREE.SUBMIT_WORK.getValue());
                } else if (Objects.equals(btnName, "作品审核")) {
                    buttonIcon = cloudApiService.buildImageUrl(MhAppIconEnum.THREE.WORK_REVIEW.getValue());
                } else if (Objects.equals(btnName, "作品优选")) {
                    buttonIcon = cloudApiService.buildImageUrl(MhAppIconEnum.THREE.WORK_PREFERRED_SELECTION.getValue());
                }
                result.add(buildBtnField(btnName, buttonIcon, workBtnDto.getLinkUrl(), enable ? "1" : "0", MhBtnSequenceEnum.WORK.getSequence()));
            }
        }
        // 排序
        result.sort(Comparator.comparingInt(MhGeneralAppResultDataDTO::getSequence));
        return result;
    }


    private String getReadingTestUrl(Activity activity) {
        return DomainConstant.XUEYA_DOMAIN + "/school-base/school-reading/" + activity.getReadingId() + "/" + activity.getReadingModuleId() + "/book-list";
    }

    private String getFlag(List<Integer> availableFlags) {
        if (availableFlags.isEmpty()) {
            return "";
        } else {
            Integer flag = availableFlags.get(0);
            availableFlags.remove(0);
            return String.valueOf(flag);
        }
    }

    private MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO buildField(String key, String value, String flag) {
        return MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key(key)
                .value(value)
                .flag(flag)
                .build();
    }

    private List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> listWorkBtn(Integer uid, Integer fid, Integer workId, List<Integer> availableFlags) {
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> btns = Lists.newArrayList();
        List<WorkBtnDTO> workBtnDtos = workApiService.listErdosBtns(workId, uid, fid);
        if (CollectionUtils.isNotEmpty(workBtnDtos)) {
            for (WorkBtnDTO workBtnDto : workBtnDtos) {
                String flag = getFlag(availableFlags);
                Boolean enable = Optional.ofNullable(workBtnDto.getEnable()).orElse(false);
                btns.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                        .key(workBtnDto.getButtonName())
                        .value(workBtnDto.getLinkUrl())
                        .flag(flag)
                        .build());
                Integer intFlag = Integer.parseInt(flag);
                if (intFlag.compareTo(MULTI_BTN_MAX_FLAG) < 0) {
                    btns.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                            .value(enable ? "1" : "0")
                            .flag(String.valueOf(intFlag + 1))
                            .build());
                }
            }
        }
        return btns;
    }

    private MhGeneralAppResultDataDTO buildBtnField(String key, String iconUrl, String url, String type, Integer sequence) {
        MhGeneralAppResultDataDTO item = MhGeneralAppResultDataDTO.buildDefault();
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
        item.setOrsUrl(url);
        int flag = 0;
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("封面")
                .value(iconUrl)
                .type("3")
                .flag(String.valueOf(flag))
                .build());
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("标题")
                .orsUrl("")
                .value(key)
                .type("3")
                .flag(String.valueOf(++flag))
                .build());
        if (StringUtils.isNotBlank(type)) {
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("按钮类型")
                    .value(type)
                    .type("3")
                    .flag(String.valueOf(++flag))
                    .build());
        }
        item.setSequence(sequence);
        item.setFields(fields);
        return item;
    }


}