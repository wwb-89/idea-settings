package com.chaoxing.activity.api.controller.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;

/**鄂尔多斯区域enum
 * @description:
 * @author: huxiaolong
 * @date: 2022/1/5 20:06 下午
 * @version: 1.0
 */
@Getter
public enum ErdosAreaEnum {

    /** 鄂尔多斯区域code */
    DONG_SHENG("东胜区", "00170040", 2241),
    KANG_BA_SHI("康巴什区", "00170036", 19706),
    YI_JIN_HUO_LUO("伊金霍洛旗", "00170034", 33272),
    ZHUN_GE_ER("准格尔旗", "00170001", 35584),
    E_KE_TUO_KE_QIAN("鄂托克前旗", "00170038", 21012),
    E_KE_TUO_KE("鄂托克旗", "00170039", 21169),
    DA_LA_TE("达拉特旗", "00170046", 34030),
    WU_SHEN("乌审旗", "00170035", 21957),
    HANG_JIN("杭锦旗", "00170037", 11962);

    private final String name;
    private final String areaCode;
    private final Integer fid;

    ErdosAreaEnum(String name, String areaCode, Integer fid) {
        this.name = name;
        this.areaCode = areaCode;
        this.fid = fid;
    }

    public static JSONArray buildRegionCondition() {
        JSONArray regionJsonArray = new JSONArray();
        JSONObject firstLevel = new JSONObject();
        firstLevel.put("id", "");
        firstLevel.put("name", "地区");
        firstLevel.put("description", "");
        firstLevel.put("count", ErdosAreaEnum.values().length);
        JSONArray subs = new JSONArray();
        for (ErdosAreaEnum areaEnum : ErdosAreaEnum.values()) {
            JSONObject item = new JSONObject();
            item.put("id", areaEnum.getAreaCode());
            item.put("name", areaEnum.getName());
            item.put("description", "");
            subs.add(item);
        }
        firstLevel.put("subs", subs);
        regionJsonArray.add(firstLevel);
        return regionJsonArray;
    }

}
