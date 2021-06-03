package com.chaoxing.activity.util;

import com.chaoxing.activity.util.constant.CommonConstant;
import org.springframework.boot.system.ApplicationHome;

/**
 * @author wwb
 * @version ver 1.0
 * @className PathUtils
 * @description
 * @blame wwb
 * @date 2021-06-01 14:44:04
 */
public class PathUtils {

    /** 项目的部署路径 */
    private static final String PROJECT_DEPLOY_PATH = new ApplicationHome(PathUtils.class).getSource().getParentFile().getAbsolutePath();

    /**获取项目部署的目录
     * @Description 
     * @author wwb
     * @Date 2021-06-01 14:50:01
     * @param 
     * @return java.lang.String
    */
    public static String getProjectDeployDir() {
        return FileUtils.getFileDir(PROJECT_DEPLOY_PATH);
    }
    
    /**获取上传根路径
     * @Description 
     * @author wwb
     * @Date 2021-06-01 14:51:45
     * @param 
     * @return java.lang.String
    */
    public static String getUploadRootPath() {
        return getProjectDeployDir() + CommonConstant.DEFAULT_FILE_SEPARATOR + PropertiesUtils.getStringValue("resource", "upload_root_path");
    }

}
