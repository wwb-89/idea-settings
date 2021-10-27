package com.chaoxing.activity.util.enums;

import com.chaoxing.activity.util.exception.BusinessException;
import lombok.Getter;

import java.util.Objects;

/**活动引擎接口签名枚举
 * https://suijimimashengcheng.bmcx.com/  随机生成key
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/10/27 14:28
 * <p>
 */
@Getter
public enum ActivityApiSignEnum {
    /** 枚举 */
    TESTER("tester", "activityEngineApi_test", "Xji!LPaYJuc^v2qnro");

    /** 所属人 */
    private String owner;

    /** sign */
    private String sign;

    /** key */
    private String key;

    ActivityApiSignEnum(String owner, String sign, String key) {
        this.owner = owner;
        this.sign = sign;
        this.key = key;
    }

    public static ActivityApiSignEnum fromSign(String sign) {
        for (ActivityApiSignEnum item : ActivityApiSignEnum.values()) {
            if (Objects.equals(sign, item.getSign())) {
                return item;
            }
        }
        throw new BusinessException("不存在sign: " + sign + "对应的 key");
    }
}
