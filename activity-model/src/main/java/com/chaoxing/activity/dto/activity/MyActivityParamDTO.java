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

    private String hide;

    private String title;

    private String areaCode;

    private String flag;

    private String wfwFormUrl;

    private Boolean managAble;

    public String buildBackUrl(String url) throws UnsupportedEncodingException {
        url += "?managAble=" + (managAble == null ? Boolean.FALSE : managAble);
        if (StringUtils.isNotBlank(hide)) {
            url += "&hide=" + hide;
        }
        if (StringUtils.isNotBlank(title)) {
            url += "&title=" + title;
        }
        if (StringUtils.isNotBlank(areaCode)) {
            url += "&areaCode=" + areaCode;
        }
        if (StringUtils.isNotBlank(flag)) {
            url += "&flag=" + flag;
        }
//        if (formId != null) {
//            url += "&formId=" + formId;
//        }
        if (StringUtils.isNotBlank(wfwFormUrl)) {
            url += "&wfwFormUrl=" + URLEncoder.encode(wfwFormUrl, StandardCharsets.UTF_8.name());
        }
        return url;
    }
}
