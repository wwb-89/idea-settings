package com.chaoxing.activity.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**志愿服务时长记录
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/19 2:11 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerServiceDTO {


    /**
     * uid
     */
    private Integer uid;

    /**
     * 用户名
     */
    private String uname;

    /**
     * 记录ID
     */
    private Integer formUserId;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 组织架构
     */
    private String department;

    /**
     * 所属组织
     */
    private String affiliations;

    /**
     * 服务日期
     */
    private String serviceDate;

    /**
     * 类型
     */
    private String type;

    /**
     * 级别
     */
    private String level;

    /**
     * 服务时长
     */
    private Long timeLength;

    /**
     * 服务编号
     */
    private String no;

}
