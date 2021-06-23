package com.chaoxing.activity.service.volunteer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.activity.VolunteerServiceDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.manager.FormApiService;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**志愿者服务
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/19 2:42 下午
 * <p>
 */
@Slf4j
@Service
public class VolunteerService {

    @Resource
    private OrgDataRepoConfigQueryService orgDataRepoConfigQueryService;

    @Resource
    private FormApiService formApiService;

    /**获取服务市场记录列表
     * @Description
     * @author huxiaolong
     * @Date 2021-05-19 14:10:11
     * @param uid
     * @param fid
     * @return void
     */
    public List<VolunteerServiceDTO> listServiceTimeLength(Integer uid, Integer fid, String serviceType) {
        List<VolunteerServiceDTO> volunteerServiceList = new ArrayList<>();
        String formType = OrgDataRepoConfigDetail.RepoTypeEnum.FORM.getValue();
        // 查找配置为表单类型且数据类型为参与时长的配置列表
        List<OrgDataRepoConfigDetail> configDetailList = orgDataRepoConfigQueryService.listOrgConfigDetail(fid, OrgDataRepoConfigDetail.DataTypeEnum.PARTICIPATE_TIME_LENGTH);
        if (CollectionUtils.isNotEmpty(configDetailList)) {
            Page<VolunteerServiceDTO> page = new Page<>(1, 10);
            for (OrgDataRepoConfigDetail configDetail: configDetailList) {
                if (Objects.equals(formType, configDetail.getRepoType())) {
                    // 查出来的repo值应为formId
                    Integer formId = Integer.valueOf(configDetail.getRepo());
                    List<FormStructureDTO> formStructures = formApiService.getFormInfo(fid, formId);
                    while (true) {
                        page = formApiService.pageVolunteerRecord(page, fid, uid, formId, formStructures, serviceType);
                        if (CollectionUtils.isEmpty(page.getRecords())) {
                            break;
                        }
                        volunteerServiceList.addAll(page.getRecords());
                        page.setCurrent(page.getCurrent() + 1);
                    }
                }
            }
        }
        return volunteerServiceList;

    }
    
    public List<VolunteerServiceDTO> listServiceTimeLength(Integer uid, Integer fid) {
        return listServiceTimeLength(uid, fid, null);
    }

    public List<String> listVolunteerServiceType(Integer fid) {
        OrgDataRepoConfigDetail configDetail = orgDataRepoConfigQueryService.getOrgConfigDetail(fid, OrgDataRepoConfigDetail.DataTypeEnum.PARTICIPATE_TIME_LENGTH, OrgDataRepoConfigDetail.RepoTypeEnum.FORM);
        if (configDetail == null) {
            return new ArrayList<>();
        }
        Integer formId = Integer.valueOf(configDetail.getRepo());
        return listVolunteerServiceType(fid, formId);
    }

    private List<String> listVolunteerServiceType(Integer fid, Integer formId) {
        List<String> serviceTypeList = new ArrayList<>();

        // 获取检索表单结构
        JSONArray formDataArray = formApiService.getFormInfoData(fid, formId);

        for (Object obj: formDataArray) {
            JSONObject jsonObject = (JSONObject) obj;
            String alias = jsonObject.getString("alias");
            if ("type".equals(alias)) {
                JSONObject fieldObj = jsonObject.getJSONObject("field");
                JSONArray options = fieldObj.getJSONArray("options");
                if (CollectionUtils.isNotEmpty(options)) {
                    for (Object item: options) {
                        JSONObject option = (JSONObject) item;
                        String type = option.getString("title");
                        if (StringUtils.isNotBlank(type)) {
                            serviceTypeList.add(type);
                        }
                    }
                }
                break;
            }
        }
        return serviceTypeList;
    }
}
