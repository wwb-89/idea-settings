package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**证书
 * @author wwb
 * @version ver 1.0
 * @className UserCertificateDTO
 * @description
 * @blame wwb
 * @date 2021-12-16 11:30:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCertificateDTO {

    /** 用户id */
    private Integer uid;
    /** 用户姓名 */
    private String realName;
    /** 账号 */
    private String uname;
    /** 是否已发放 */
    private Boolean issued;
    /** 发放时间 */
    private LocalDateTime issueTime;
    /** 发放时间戳 */
    private Long issueTimestamp;

}