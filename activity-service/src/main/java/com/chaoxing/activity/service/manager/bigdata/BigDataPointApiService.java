package com.chaoxing.activity.service.manager.bigdata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
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

    /** 新增积分接口 */
    private static final String ADD_POINT_URL = DomainConstant.BIGDATA_SCORE_DOMAIN + "/gt/point?fid=%d&pid=%d&userid=%d&dataType=%d&pointType=%d&point=%d&changeTime=%d&enc=%s";
    /** 消耗积分接口 */
    private static final String SPEND_POINT_URL = DomainConstant.BIGDATA_SCORE_DOMAIN + "/house/gt/point/spend?fid=%d&pid=%d&userid=%d&dataType=%d&pointType=%d&point=%d&changeTime=%d&enc=%s";
    private static final Map<Integer, String> KEY_MAP = Maps.newHashMap();

    static {
        KEY_MAP.put(23274, "y$$Ojy$s");
        KEY_MAP.put(170690, "Gyees$OysG");
    }

    @Resource(name = "restTemplateProxy")
    private RestTemplate restTemplate;

    /**新增积分
     * @Description 
     * @author wwb
     * @Date 2021-10-12 19:53:02
     * @param pointPushParam
     * @return void
    */
    public void addPoint(PointPushParamDTO pointPushParam) {
        String key = KEY_MAP.get(pointPushParam.getFid());
        if (StringUtils.isBlank(key)) {
            log.warn("机构:{} 没有配置大数据积分key", pointPushParam.getFid());
            return;
        }
        String enc = getEnc(pointPushParam, key);
        String url = String.format(ADD_POINT_URL, pointPushParam.getFid(), pointPushParam.getPid(), pointPushParam.getUid(), pointPushParam.getPointType().getDataType().getValue(),
                pointPushParam.getPointType().getValue(), pointPushParam.getPoint(), pointPushParam.getTime(), enc);
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Objects.equals(1, jsonObject.getInteger("status"))) {
            String message = jsonObject.getString("msg");
            log.error("根据url:{} 新增大数据积分失败:{}", url, message);
            throw new BusinessException("大数据积分新增失败");
        }
    }

    /**消费积分
     * @Description 
     * @author wwb
     * @Date 2021-10-15 14:17:42
     * @param pointPushParam
     * @return void
    */
    public void spendPoint(PointPushParamDTO pointPushParam) {
        String key = KEY_MAP.get(pointPushParam.getFid());
        if (StringUtils.isBlank(key)) {
            log.warn("机构:{} 没有配置大数据积分key", pointPushParam.getFid());
            return;
        }
        String enc = getEnc(pointPushParam, key);
        String url = String.format(SPEND_POINT_URL, pointPushParam.getFid(), pointPushParam.getPid(), pointPushParam.getUid(), pointPushParam.getPointType().getDataType().getValue(),
                pointPushParam.getPointType().getValue(), pointPushParam.getPoint(), pointPushParam.getTime(), enc);
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        if (!jsonObject.getBoolean("status")) {
            String message = jsonObject.getString("msg");
            log.error("根据url:{} 消费大数据积分失败:{}", url, message);
            if (message.contains("积分不足")) {
                return;
            }
            throw new BusinessException("大数据积分消费失败");
        }
    }

    private String getEnc(PointPushParamDTO pointPushParam, String key) {
        TreeMap<String, Object> param = Maps.newTreeMap();
        param.put("changeTime", pointPushParam.getTime());
        param.put("dataType", pointPushParam.getPointType().getDataType().getValue());
        param.put("fid", pointPushParam.getFid());
        param.put("pid", pointPushParam.getPid());
        param.put("point", pointPushParam.getPoint());
        param.put("pointType", pointPushParam.getPointType().getValue());
        param.put("userid", pointPushParam.getUid());
        StringBuilder clearText = new StringBuilder();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            clearText.append(entry.getValue());
        }
        clearText.append(key);
        return DigestUtils.md5Hex(clearText.toString()).toUpperCase();
    }

    @Data
    public static class PointPushParamDTO {

        private Integer uid;
        private Integer fid;
        private PointTypeEnum pointType;
        private Long time;
        private Integer pid = 1;
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

    @Getter
    public enum DataTypeEnum {

        /** 行为积分 */
        BEHAVIOR("行为积分", 1),
        SPEND("消耗积分", 1);

        private final String name;
        private final Integer value;

        DataTypeEnum(String name, Integer value) {
            this.name = name;
            this.value = value;
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
        ORGANIZE_ACTIVITY("组织活动", 39, 42, DataTypeEnum.BEHAVIOR),
        PARTICIPATION("参与活动（签到率100%）", 40, 43, DataTypeEnum.BEHAVIOR),
        PART_PARTICIPATION("参与活动（签到率低于100%）", 41, 44, DataTypeEnum.BEHAVIOR),
        CANCEL_ORGANIZE_ACTIVITY("取消活动", 42, 39, DataTypeEnum.SPEND),
        CANCEL_PARTICIPATION("取消活动", 43, 40, DataTypeEnum.SPEND),
        CANCEL_PART_PARTICIPATION("取消活动", 44, 41, DataTypeEnum.SPEND);

        private final String name;
        private final Integer value;
        private final Integer reverseValue;
        private DataTypeEnum dataType;

        PointTypeEnum(String name, Integer value, Integer reverseValue, DataTypeEnum dataType) {
            this.name = name;
            this.value = value;
            this.reverseValue = reverseValue;
            this.dataType = dataType;
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