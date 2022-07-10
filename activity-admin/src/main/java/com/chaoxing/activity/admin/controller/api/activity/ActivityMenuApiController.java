package com.chaoxing.activity.admin.controller.api.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import com.chaoxing.activity.service.activity.menu.ActivityMenuHandleService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**活动菜单api
 * @description:
 * @author: huxiaolong
 * @date: 2022/1/19 11:06 AM
 * @version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("api/activity/menu")
public class ActivityMenuApiController {

    @Resource
    private ActivityMenuHandleService activityMenuHandleService;

    /**新增活动自定义菜单
     * @Description
     * @author huxiaolong
     * @Date 2022-01-19 11:10:45
     * @param activityMenuStr
     * @return
     */
    @RequestMapping("add/activity-menu")
    public RestRespDTO addActivityCustomMenu(String activityMenuStr) {
        ActivityMenuDTO activityMenu = JSON.parseObject(activityMenuStr, ActivityMenuDTO.class);
        return RestRespDTO.success(activityMenuHandleService.addActivityCustomMenu(activityMenu));
    }

    /**更新活动自定义菜单
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-19 15:54:52
     * @param activityMenuStr
     * @return 
     */
    @RequestMapping("update/activity-menu")
    public RestRespDTO updateActivityCustomMenu(String activityMenuStr) {
        ActivityMenuDTO activityMenu = JSON.parseObject(activityMenuStr, ActivityMenuDTO.class);
        activityMenuHandleService.updateActivityCustomMenu(activityMenu);
        return RestRespDTO.success();
    }

    /**移除活动自定义菜单
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-19 15:55:06
     * @param menuId
     * @param activityMenuId
     * @return
     */
    @RequestMapping("remove/activity-menu")
    public RestRespDTO removeActivityMenu(Integer menuId, Integer activityMenuId) {
        activityMenuHandleService.removeActivityMenu(menuId, activityMenuId);
        return RestRespDTO.success();
    }

    /**更新活动菜单配置启用状态
     * @Description
     * @author huxiaolong
     * @Date 2022-01-19 15:55:33
     * @param activityMenuStr
     * @return
     */
    @RequestMapping("update/menu-enable")
    public RestRespDTO updateMenuEnableStatus(String activityMenuStr) {
        ActivityMenuDTO activityMenu = JSON.parseObject(activityMenuStr, ActivityMenuDTO.class);
        return RestRespDTO.success(activityMenuHandleService.updateMenuEnableStatus(activityMenu));
    }

    /**更新活动菜单配置显示规则
     * @Description
     * @author huxiaolong
     * @Date 2022-01-19 15:55:59
     * @param activityMenuStr
     * @return
     */
    @RequestMapping("update/show-rule")
    public RestRespDTO updateMenuShowRule(String activityMenuStr) {
        ActivityMenuDTO activityMenu = JSON.parseObject(activityMenuStr, ActivityMenuDTO.class);
        return RestRespDTO.success(activityMenuHandleService.updateMenuShowRule(activityMenu));

    }

    @RequestMapping("update/sort")
    public RestRespDTO updateMenuSort(String menusJsonStr) {
        List<ActivityMenuDTO> activityMenus = JSON.parseArray(menusJsonStr, ActivityMenuDTO.class);
        activityMenuHandleService.updateMenusSort(activityMenus);
        return RestRespDTO.success();

    }

}
