package com.chaoxing.activity.dto.module;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;

/**班级互动
 * @description:
 * @author: huxiaolong
 * @date: 2021/12/2 5:36 下午
 * @version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClazzInteractionDTO {

    /** 班级id */
    private Integer clazzId;
    /** 课程id */
    private Integer courseId;


    @Data
    @Builder
    public static class ClazzInteractionMenu {
        /** 菜单名称 */
        private String name;
        /** 菜单code */
        private String code;
        /** 菜单地址 */
        private String url;
        /** 菜单默认图标 */
        private String defaultIcon;
        /** 菜单激活图标 */
        private String activeIcon;


        public static ClazzInteractionMenu buildMenu(String name, String code, String url, String defaultIcon, String activeIcon) {
            return ClazzInteractionMenu.builder()
                    .name(name)
                    .code(code)
                    .url(url)
                    .defaultIcon(defaultIcon)
                    .activeIcon(activeIcon)
                    .build();
        }
    }

    public static List<ClazzInteractionMenu> listClazzInteractionMenus(Activity activity, Integer uid, Integer fid) {
        List<ClazzInteractionMenu> menus = Lists.newArrayList();
        Integer clazzId = activity.getClazzId();
        Integer courseId = activity.getCourseId();
        // 任务
        String taskUrl = "https://mobilelearn.chaoxing.com/page/active/activeList?fid="+ fid + "&courseId=" + courseId + "&classId=" + clazzId;
        menus.add(ClazzInteractionMenu.buildMenu("任务", "task", taskUrl, "icon-task-default", "icon-task-active"));

        String enc = DigestUtils.md5Hex(courseId + "&" + uid + "F0hZ~/@-4]Pv");
        // 讨论
        String discussUrl = DomainConstant.MOOC + "/course/isNewCourse?v=2&pageHeader=5&single=1&clazzId=" + clazzId + "&courseId=" + courseId + "&enc=" + enc;
        menus.add(ClazzInteractionMenu.buildMenu("讨论", "discuss", discussUrl, "icon-grade-manage-default", "icon-grade-manage-active"));
        // 作业
        String homeworkUrl = DomainConstant.MOOC + "/course/isNewCourse?v=2&pageHeader=6&single=1&clazzId=" + clazzId + "&courseId=" + courseId + "&enc=" + enc;
        menus.add(ClazzInteractionMenu.buildMenu("作业", "homework", homeworkUrl, "icon-discuss-default", "icon-discuss-active"));
        // 评审管理
        String reviewManagementUrl = DomainConstant.XIAMEN_TRAINING_PLATFORM_API + "/review/list?activityId=" + activity.getId();
        menus.add(ClazzInteractionMenu.buildMenu("评审管理", "review_management", reviewManagementUrl, "icon-statistics-default", "icon-statistics-active"));
        return menus;
    }
}
