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
        menus.add(ClazzInteractionMenu.buildMenu("任务", "task", taskUrl, "670f8264cc7b8a2c381292f3d6f20f4b", "74ca72309d53bb605f94a49098e4b28c"));

        String enc = DigestUtils.md5Hex(courseId + "&" + uid + "F0hZ~/@-4]Pv");
        // 讨论
        String discussUrl = DomainConstant.MOOC + "/course/isNewCourse?v=2&pageHeader=5&single=1&clazzId=" + clazzId + "&courseId=" + courseId + "&enc=" + enc;
        menus.add(ClazzInteractionMenu.buildMenu("讨论", "discuss", discussUrl, "b92e2e9564238d821ab84d58a1fb4f34", "82a721fdf200d19c3c26786e038457d6"));
        // 作业
        String homeworkUrl = DomainConstant.MOOC + "/course/isNewCourse?v=2&pageHeader=6&single=1&clazzId=" + clazzId + "&courseId=" + courseId + "&enc=" + enc;
        menus.add(ClazzInteractionMenu.buildMenu("作业", "homework", homeworkUrl, "1ff6b8167b8b7854131a273f4ca66599", "f5b89f4d64c1bd8732406c0730cff170"));
        // 评审管理
        String xiamenTrainingApiDomain = DomainConstant.XIAMEN_TRAINING_PLATFORM_API.replaceAll("http(|s)://", "");
        String reviewManagementUrl = "//" + xiamenTrainingApiDomain + "/review/list?activityId=" + activity.getId();
        menus.add(ClazzInteractionMenu.buildMenu("评审", "review_management", reviewManagementUrl, "005ca2b504bf6f6f212347ee90e6de15", "da4882cec8f38768dfc5bf515de44374"));
        return menus;
    }
}
