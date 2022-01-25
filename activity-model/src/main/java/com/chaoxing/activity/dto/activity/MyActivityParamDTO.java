package com.chaoxing.activity.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    /** 当前单位的活动 */
    private Integer fid;
    /** 控制tab隐藏, 报名:signedUp, 收藏:collected, 管理:managing */
    private String hide;
    /** 我的活动界面标题 */
    private String title;
    /** 添加按钮名称 */
    private String addBtnName;
    /** 活动标识，当前flag的活动 */
    private String flag;
    /** 万能表单地址(填写表单内容) */
    private String addUrl;

    // 接口请求参数
    // 回传参数
    /** 搜索内容 */
    private String sw;
    /** 是否加载报名待审批的活动 */
    private Boolean loadWaitAudit;
    // 后台接口填充参数
    /** 用户uid */
    private Integer uid;


}

