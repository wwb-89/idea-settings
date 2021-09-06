package com.chaoxing.activity.api.controller.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.util.constant.ActivityMhUrlConstant;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
    private static final String WORK_BTN_URL = "http://api.reading.chaoxing.com/activity/user/permission?activityId=%d&uid=%s&fid=%d";

    @Resource
    private ActivityQueryService activityQueryService;

    @Resource
    private RestTemplate restTemplate;

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
        Integer websiteId = params.getInteger("websiteId");
        // 根据websiteId查询活动id
        Activity activity = activityQueryService.getByWebsiteId(websiteId);
        Optional.ofNullable(activity).orElseThrow(() -> new BusinessException("活动不存在"));
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
        mhGeneralAppResultDataFields.add(buildField(fieldCodeNameRelation.get("activity_name"), activity.getName(), "1"));
        // 开始时间
        mhGeneralAppResultDataFields.add(buildField(fieldCodeNameRelation.get("activity_time_scope"), DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getStartTime()), "100"));
        // 结束时间
        mhGeneralAppResultDataFields.add(buildField("活动结束时间", DateTimeFormatterConstant.YYYY_MM_DD_HH_MM.format(activity.getEndTime()), "101"));
        // 活动地点
        String activityAddress = Optional.ofNullable(activity.getAddress()).orElse("") + Optional.ofNullable(activity.getDetailAddress()).orElse("");
        mhGeneralAppResultDataFields.add(buildField("", "", "102"));
        mhGeneralAppResultDataFields.add(buildField("", "", "103"));
        mhGeneralAppResultDataFields.add(buildField("活动地点", activityAddress, "104"));
        // 主办方
        mhGeneralAppResultDataFields.add(buildField(fieldCodeNameRelation.get("activity_organisers"), activity.getOrganisers(), "105"));
        mhGeneralAppResultDataFields.add(buildField("", "", "106"));
        mhGeneralAppResultDataFields.add(buildField("", "", "107"));
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> btns;
        List<Integer> availableFlags = Lists.newArrayList(109, 111, 113, 115, 116, 118, 119);
        Integer workId = activity.getWorkId();
        Integer readingId = activity.getReadingId();
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
        if (workId != null) {
            // 作品征集定制
            List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> workBtns = listWorkBtn(uid, wfwfid, workId, availableFlags);
            mhGeneralAppResultDataFields.addAll(workBtns);
        }
        String activityAddressLink = "https://api.hd.chaoxing.com/redirect/activity/"+ activityId +"/address";
        // 活动地点链接（线下的活动有）
        mhGeneralAppResultDataFields.add(buildField("活动地点链接", activityAddressLink, "117"));
        // 海报
        mhGeneralAppResultDataFields.add(buildField("海报", "海报", "130"));
        mhGeneralAppResultDataFields.add(buildField("海报", String.format(ActivityMhUrlConstant.ACTIVITY_POSTERS_URL, activity.getId()), "131"));
        mhGeneralAppResultDataDTO.setFields(mhGeneralAppResultDataFields);
        return RestRespDTO.success(jsonObject);
    }

    private String getReadingTestUrl(Activity activity) {
        return "http://xueya.chaoxing.com/school-base/school-reading/" + activity.getReadingId() + "/" + activity.getReadingModuleId() + "/book-list";
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
        String url = String.format(WORK_BTN_URL, workId, Optional.ofNullable(uid).map(String::valueOf).orElse(""), fid);
        String result = restTemplate.postForObject(url, null, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Objects.equals(true, jsonObject.getBoolean("success"))) {
            JSONArray data = jsonObject.getJSONArray("data");
            if (data != null && jsonObject.size() > 0) {
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    String flag = getFlag(availableFlags);
                    JSONObject btnJsonObject = data.getJSONObject(i);
                    btns.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                            .key(btnJsonObject.getString("buttonName"))
                            .value(btnJsonObject.getString("linkUrl"))
                            .flag(flag)
                            .build());
                    Integer intFlag = Integer.parseInt(flag);
                    if (intFlag.compareTo(MULTI_BTN_MAX_FLAG) < 0) {
                        btns.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                                .value("1")
                                .flag(String.valueOf(intFlag + 1))
                                .build());
                    }
                }
            }
        }
        return btns;
    }

}