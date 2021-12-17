package com.chaoxing.activity.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/1 16:33
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyActivityParamDTO {

    /** 控制tab隐藏, 报名:signedUp, 收藏:collected, 管理:managing */
    private String hide;
    /** 我的活动界面标题 */
    private String title;
    /** 活动标识 */
    private String flag;
    /** 万能表单地址(填写表单内容) */
    private String wfwFormUrl;
    /** 是否可管理，即是否展示发布操作按钮 */
    private Boolean managAble;
    /** 我的活动pc端样式，临时参数，后续合并删除 */
    private String style;

    public String buildBackUrl(String url) throws UnsupportedEncodingException {
        url += "?managAble=" + (managAble == null ? Boolean.FALSE : managAble);
        if (StringUtils.isNotBlank(hide)) {
            url += "&hide=" + hide;
        }
        if (StringUtils.isNotBlank(title)) {
            url += "&title=" + title;
        }
        if (StringUtils.isNotBlank(flag)) {
            url += "&flag=" + flag;
        }
        if (StringUtils.isNotBlank(wfwFormUrl)) {
            url += "&wfwFormUrl=" + URLEncoder.encode(wfwFormUrl, StandardCharsets.UTF_8.name());
        }
        return url;
    }
}
