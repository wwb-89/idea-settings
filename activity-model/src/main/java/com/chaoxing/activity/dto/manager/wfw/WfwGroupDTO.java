package com.chaoxing.activity.dto.manager.wfw;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.UUID;

/**
 * @author xhl
 * @version ver 1.0
 * @className WfwGroupDTO
 * @description
 * @blame xhl
 * @date 2021-03-10 15:01:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwGroupDTO {
    /** 组id*/
    private String id;
    /** 虚拟id，在活动创建时使用，相同节点会各自建立一个虚拟id, 其余节点均将真实id赋值虚拟id */
    private String virtualId;
    /** 组上一级id*/
    private String gid;
    /** 名称*/
    private String groupname;
    /** 子结点个数 */
    private Integer soncount;
    /** level */
    private Integer groupLevel;
    /** 下级 */
    private List<WfwGroupDTO> children;

    public static List<WfwGroupDTO> perfectWfwGroups(List<WfwGroupDTO> wfwGroups) {
        List<WfwGroupDTO> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(wfwGroups)) {
            return result;
        }
        for (WfwGroupDTO group : wfwGroups) {
            group.setVirtualId(group.getId());
            result.add(group);
            if (group.getSoncount() > 0) {
                WfwGroupDTO item = new WfwGroupDTO();
                BeanUtils.copyProperties(group, item);
                item.setVirtualId(UUID.randomUUID().toString().trim().replaceAll("-", ""));
                item.setSoncount(0);
                item.setGid(item.getId());
                result.add(item);
            }
        }
        return result;
    }
}
