package com.chaoxing.activity.admin.vo.stat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**机构用户统计视图对象
 * @author wwb
 * @version ver 1.0
 * @className OrgUserStatVO
 * @description
 * @blame wwb
 * @date 2021-05-28 10:36:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgUserStatVO {

    /** 用户uid */
    private Integer uid;
    /** 登录名 */
    private String uname;
    /** 真实姓名 */
    private String realName;
    /** 学号 */
    private String studentNo;
    /** 组织架构 */
    private String organizationStructure;
    /** 手机号 */
    private String mobile;
    /** 参与的活动数 */
    private Integer participateActivityNum;
    /** 签到数量 */
    private Integer signedInNum;
    /** 签到率 */
    private BigDecimal signedInRate;
    /** 评价数量 */
    private Integer ratingNum;
    /** 合格的数量 */
    private Integer qualifiedNum;
    /** 总参与时长 */
    private Integer totalParticipateTimeLength;

}
