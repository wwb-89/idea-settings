package com.chaoxing.activity.dto.activity;

import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.util.enums.OrderTypeEnum;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Optional;

/**活动广场参数列表
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/11/12 13:42
 * <p>
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivitySquareParamDTO {

    /** wfwfid */
    private Integer wfwfid;
    /** 门户封装的fid */
    private Integer unitId;
    /** 微服务封装的fid */
    private Integer state;
    /** 其他来源封装的fid */
    private Integer fid;
//    /** 区域编码? */
    private String code;

    private Integer banner;
    /** 风格,活动广场样式, 1和2 ,默认 2*/
    private String style;
    /** 活动标示：双选会、第二课堂等 */
    private String flag = "";
    /** 市场id*/
    private Integer marketId;
    /** flag的查询范围，0：默认，1：所有 */
    private Integer scope = 0;
    /** 隐藏的筛选条件 */
    private String hideFilter;
    /** 为1则查询能报名的活动 */
    private Integer strict = 0;
    /** 时间排序,默认逆序 */
    private String timeOrder = OrderTypeEnum.DESC.getValue();

    public Integer getRealFid() {
        return Optional.ofNullable(getWfwfid()).orElse(Optional.ofNullable(getUnitId()).orElse(Optional.ofNullable(getState()).orElse(getFid())));
    }

}
