package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.util.constant.DomainConstant;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 证书发放表
 * @className: CertificateIssue, table_name: t_certificate_issue
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-12-15 15:14:00
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_certificate_issue")
public class CertificateIssue {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 发放用户id; column: uid*/
    private Integer uid;
    /** 证书编号; column: no*/
    private String no;
    /** 序号; column: serial_no*/
    private Integer serialNo;
    /** 二维码状态; column: qr_code_status*/
    private Integer qrCodeStatus;
    /** 二维码云盘id; column: qr_code_cloud_id*/
    private String qrCodeCloudId;
    /** 发放时间; column: issue_time*/
    private LocalDateTime issueTime;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    @Getter
    public enum QrCodeStatusEnum {

        /** 失败 */
        FAIL("失败", 0),
        SUCCESS("成功", 1),
        WAIT("待处理", 2);

        private final String name;
        private final Integer value;

        QrCodeStatusEnum(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

    }

    /**获取证书的预览地址
     * @Description 
     * @author wwb
     * @Date 2021-12-17 10:34:45
     * @param 
     * @return java.lang.String
    */
    public String getShowUrl() {
        return getShowUrl(uid, activityId);
    }

    public static String getShowUrl(Integer uid, Integer activityId) {
        return DomainConstant.ADMIN + "/api/certificate/show?uid=" + uid + "&activityId=" + activityId;
    }

    /**获取证书的下载地址
     * @Description 
     * @author wwb
     * @Date 2021-12-17 10:35:02
     * @param 
     * @return java.lang.String
    */
    public String getDownloadUrl() {
        return getDownloadUrl(uid, activityId);
    }

    public static String getDownloadUrl(Integer uid, Integer activityId) {
        return DomainConstant.API + "/certificate/download?uid=" + uid + "&activityId=" + activityId;
    }

}