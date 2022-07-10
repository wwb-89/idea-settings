package com.chaoxing.activity.dto.manager.cloud;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**云盘图片对象
 * @author wwb
 * @version ver 1.0
 * @className CloudImageDTO
 * @description
 * @blame wwb
 * @date 2021-12-09 16:54:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudImageDTO {

    private static final Pattern PATTERN = Pattern.compile(".*?\\.");

    /** 云盘资源id */
    @JSONField(name = "objectid")
    private String objectId;
    /** 文件名 */
    @JSONField(name = "filename")
    private String fileName;
    /** 长度 */
    private Long length;
    private String crc;
    /** 访问地址 */
    private String http;
    /** 下载地址 */
    private String download;

    /**获取后缀
     * @Description 
     * @author wwb
     * @Date 2021-12-09 16:58:50
     * @param 
     * @return java.lang.String
    */
    public String getSuffix() {
        if (StringUtils.isNotBlank(getFileName())) {
            Matcher matcher = PATTERN.matcher(getFileName());
            return matcher.replaceAll("");
        }
        return "";
    }

}