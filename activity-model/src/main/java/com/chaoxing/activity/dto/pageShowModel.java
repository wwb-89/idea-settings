package com.chaoxing.activity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class pageShowModel {

    /** 封面云盘id; column: cover_cloud_id*/
    private String coverCloudId;

    /*活动名称*/
    private String name;

    /** 状态。0：已删除，1：待发布，2：已发布，3：进行中，4：已结束; column: status*/
    private Integer status;

    /** 创建机构名; column: create_org_name*/
    private String createOrgName;

    /*活动地址*/
    private String address;


}
