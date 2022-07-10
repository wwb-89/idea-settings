package com.chaoxing.activity.util.exception;

import lombok.Getter;

/**证书未发放异常
 * @author wwb
 * @version ver 1.0
 * @className CertificateNotIssueException
 * @description
 * @blame wwb
 * @date 2021-12-15 15:20:20
 */
@Getter
public class CertificateNotIssueException extends BusinessException {

    public CertificateNotIssueException() {
        super("暂无证书");
    }

}