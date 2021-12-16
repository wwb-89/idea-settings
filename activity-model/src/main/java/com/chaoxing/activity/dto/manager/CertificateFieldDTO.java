package com.chaoxing.activity.dto.manager;

import com.chaoxing.activity.model.Activity;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**证书字段对象
 * @author wwb
 * @version ver 1.0
 * @className CertificateFieldDTO
 * @description
 * @blame wwb
 * @date 2021-12-15 11:03:31
 */
@Getter
@ToString
public class CertificateFieldDTO {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** 活动名称 */
    private String activityName;
    /** 用户姓名 */
    private String userName;
    /** 机构名称 */
    private String orgName;
    /** 证书编号 */
    private String no;
    /** 发放日期 */
    private String issueDate;

    private CertificateFieldDTO() {

    }

    public static CertificateFieldDTO build(Activity activity, String no, LocalDateTime issueTime, String userName) {
        CertificateFieldDTO certificateField = new CertificateFieldDTO();
        certificateField.activityName = activity.getName();
        certificateField.userName = userName;
        certificateField.orgName = Optional.ofNullable(activity.getOrganisers()).orElse("");
        certificateField.no = no;
        certificateField.issueDate = issueTime.format(DATE_FORMATTER);
        return certificateField;
    }

}
