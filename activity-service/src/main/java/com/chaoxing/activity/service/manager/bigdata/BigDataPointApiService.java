package com.chaoxing.activity.service.manager.bigdata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**大数据积分api服务
 * @author wwb
 * @version ver 1.0
 * @className BigDataPointApiService
 * @description
 * @blame wwb
 * @date 2021-10-12 18:57:01
 */
@Slf4j
@Service
public class BigDataPointApiService {

    /** 推送积分接口 */
    private static final String POINT_PUSH_URL = "http://bigdata-api.chaoxing.com/gt/point?fid=%d&pid=%d&userid=%d&dataType=%d&pointType=%d&point=%d&changeTime=%d&enc=%s";
    private static final String KEY = "y$$Ojy$s";

    @Resource(name = "restTemplateProxy")
    private RestTemplate restTemplate;

    /**积分推送
     * @Description 
     * @author wwb
     * @Date 2021-10-12 19:53:02
     * @param pointPushParam
     * @return void
    */
    public void pointPush(PointPushParamDTO pointPushParam) {
        String enc = getEnc(pointPushParam);
        String url = String.format(POINT_PUSH_URL, pointPushParam.getFid(), pointPushParam.getPid(), pointPushParam.getUid(), pointPushParam.getDataType(),
                pointPushParam.getPointType().getValue(), pointPushParam.getPoint(), pointPushParam.getTime(), enc);
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Objects.equals(1, jsonObject.getInteger("status"))) {
            String message = jsonObject.getString("msg");
            log.error("根据url:{} 推送大数据积分失败:{}", url, message);
            throw new BusinessException("大数据积分推送失败");
        }
    }

    private String getEnc(PointPushParamDTO pointPushParam) {
        TreeMap<String, Object> param = Maps.newTreeMap();
        param.put("changeTime", pointPushParam.getTime());
        param.put("dataType", pointPushParam.getDataType());
        param.put("fid", pointPushParam.getFid());
        param.put("pid", pointPushParam.getPid());
        param.put("point", pointPushParam.getPoint());
        param.put("pointType", pointPushParam.getPointType().getValue());
        param.put("userid", pointPushParam.getUid());
        StringBuilder clearText = new StringBuilder();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            clearText.append(entry.getValue());
        }
        clearText.append(KEY);
        return DigestUtils.md5Hex(clearText.toString()).toUpperCase();
    }

    @Data
    public static class PointPushParamDTO {

        private Integer uid;
        private Integer fid;
        private PointTypeEnum pointType;
        private Long time;
        private Integer pid = 1;
        private Integer dataType = 1;
        private Integer point = 1;

        private PointPushParamDTO() {

        }

        public PointPushParamDTO(Integer uid, Integer fid, PointTypeEnum pointType, LocalDateTime time) {
            this.uid = uid;
            this.fid = fid;
            this.pointType = pointType;
            this.time = DateUtils.date2Timestamp(time);
        }

    }

    /** 积分类型
     * @className BigDataPointApiService
     * @description 
     * @author wwb
     * @blame wwb
     * @date 2021-10-12 18:58:34
     * @version ver 1.0
     */
    @Getter
    public enum PointTypeEnum {

        // 活动组织
        ORGANIZE_ACTIVITY("组织活动", 39, 42),
        PARTICIPATION("参与活动（签到率100%）", 40, 43),
        PART_PARTICIPATION("参与活动（签到率低于100%）", 41, 44),
        CANCEL_ORGANIZE_ACTIVITY("取消活动", 42, 39),
        CANCEL_PARTICIPATION("取消活动", 43, 40),
        CANCEL_PART_PARTICIPATION("取消活动", 44, 41);

        private final String name;
        private final Integer value;
        private final Integer reverseValue;

        PointTypeEnum(String name, Integer value, Integer reverseValue) {
            this.name = name;
            this.value = value;
            this.reverseValue = reverseValue;
        }

        public static PointTypeEnum fromValue(Integer value) {
            PointTypeEnum[] values = PointTypeEnum.values();
            for (PointTypeEnum pointType : values) {
                if (Objects.equals(pointType.getValue(), value)) {
                    return pointType;
                }
            }
            return null;
        }

    }

}