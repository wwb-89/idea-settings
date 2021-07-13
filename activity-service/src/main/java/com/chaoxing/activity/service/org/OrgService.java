package com.chaoxing.activity.service.org;

import com.chaoxing.activity.service.manager.WfwAreaApiService;
import com.chaoxing.activity.util.constant.WebTemplateCustomConfigConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**机构服务
 * @author wwb
 * @version ver 1.0
 * @className OrgService
 * @description
 * @blame wwb
 * @date 2021-06-30 18:05:31
 */
@Slf4j
@Service
public class OrgService {

    @Resource
    private WfwAreaApiService wfwAreaApiService;

    /**是否是定制机构
     * @Description 
     * @author wwb
     * @Date 2021-06-30 18:07:13
     * @param fid
     * @return boolean
    */
    public boolean isCustomOrg(Integer fid) {
        List<String> codes = wfwAreaApiService.listCodeByFid(fid);
        return isCustomOrg(codes);
    }

    /**是否是定制机构
     * @Description 
     * @author wwb
     * @Date 2021-06-30 18:07:54
     * @param codes
     * @return boolean
    */
    public boolean isCustomOrg(List<String> codes) {
        boolean isCustomOrg = false;
        if (CollectionUtils.isNotEmpty(codes)) {
            for (String code : codes) {
                for (String customAreaCode : WebTemplateCustomConfigConstant.CUSTOM_AREA_CODES) {
                    if (StringUtils.isNotBlank(code) && code.startsWith(customAreaCode)) {
                        isCustomOrg = true;
                        break;
                    }
                }
            }
        }
        return isCustomOrg;
    }

}
