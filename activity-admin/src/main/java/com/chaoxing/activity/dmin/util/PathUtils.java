package com.chaoxing.activity.dmin.util;

import com.chaoxing.activity.util.FileUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.ActivityAdminApplication;

/**
 * @className PathUtils
 * @description  * @author chaoxing
 * @date 2018-04-28 14:22:08
 * @version ver 1.0
 */
public class PathUtils {

    private static final String UPLOAD_PATH = "upload";

    private PathUtils() {

    }

    /** 项目的部署路径 */
    private static final String PROJECT_DEPLOY_PATH = ActivityAdminApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    /** 获取项目部署的目录
     * @Description
     * @author
     * @Date 2018-04-28 14:32:13
     * @param
     * @return java.lang.String
     */
    public static String getProjectDeployDir() {
        return FileUtils.getFileDirectory(PROJECT_DEPLOY_PATH);
    }
    /** 获取上传根路径
     * @Description
     * @author
     * @Date 2018-04-28 14:57:31
     * @param
     * @return java.lang.String
     */
    public static String getUploadRootPath() {
        return getProjectDeployDir() + CommonConstant.DEFAULT_FILE_SEPARATOR + UPLOAD_PATH;
    }

}
