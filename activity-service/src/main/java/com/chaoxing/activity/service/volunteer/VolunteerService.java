package com.chaoxing.activity.service.volunteer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.activity.VolunteerServiceDTO;
import com.chaoxing.activity.dto.manager.form.FormAdvanceSearchFilterConditionDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigQueryService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
    private WfwFormApiService formApiService;

    /**获取服务市场记录列表
     * @Description
     * @author huxiaolong
     * @Date 2021-05-19 14:10:11
     * @param uid
     * @param fid
     * @return void
     */
    public List<VolunteerServiceDTO> listServiceTimeLength(Integer uid, Integer fid, String serviceType) {
        String formType = OrgDataRepoConfigDetail.RepoTypeEnum.FORM.getValue();
        // 查找配置为表单类型且数据类型为参与时长的配置列表
        List<OrgDataRepoConfigDetail> configDetailList = orgDataRepoConfigQueryService.listOrgConfigDetail(fid, OrgDataRepoConfigDetail.DataTypeEnum.PARTICIPATE_TIME_LENGTH);
        if (CollectionUtils.isEmpty(configDetailList)) {
            return Lists.newArrayList();
        }
        List<VolunteerServiceDTO> volunteerServiceList = new ArrayList<>();
        for (OrgDataRepoConfigDetail configDetail: configDetailList) {
            if (Objects.equals(formType, configDetail.getRepoType())) {
                FormAdvanceSearchFilterConditionDTO.Filter userCondition = FormAdvanceSearchFilterConditionDTO.Filter
                        .builder()
                        .alias("user")
                        .compt("belonger")
                        .val(JSON.toJSONString(Collections.singleton(uid)))
                        .express(FormAdvanceSearchFilterConditionDTO.Filter.ExpressEnum.EQ.getValue()).build();
                List<FormAdvanceSearchFilterConditionDTO.Filter> filters = Lists.newArrayList(userCondition);
                if (StringUtils.isNotBlank(serviceType)) {
                    filters.add(FormAdvanceSearchFilterConditionDTO.Filter
                            .builder()
                            .alias("type")
                            .compt("selectbox")
                            .val(JSON.toJSONString(Collections.singleton(serviceType)))
                            .express(FormAdvanceSearchFilterConditionDTO.Filter.ExpressEnum.MATCH.getValue()).build());
                }
                FormAdvanceSearchFilterConditionDTO formAdvanceSearchFilterConditionDto = FormAdvanceSearchFilterConditionDTO.builder()
                        .model(FormAdvanceSearchFilterConditionDTO.ModelEnum.AND.getValue())
                        .filters(new ArrayList<>(Collections.singleton(filters)))
                        .build();
                List<FormDataDTO> formData = formApiService.advancedSearchAll(formAdvanceSearchFilterConditionDto, Integer.valueOf(configDetail.getRepo()), fid);
                volunteerServiceList = VolunteerServiceDTO.buildFromFormData(formData);
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
        List<String> serviceTypeList = Lists.newArrayList();
        // 获取检索表单结构
        List<FormStructureDTO> formStructures = formApiService.getFormStructure(formId, fid);

        for (FormStructureDTO fs: formStructures) {
            if (Objects.equals("type", fs.getAlias()) && fs.getField() != null) {
                JSONArray options = fs.getField().getJSONArray("options");
                if (CollectionUtils.isNotEmpty(options)) {
                    options.forEach(o -> {
                        JSONObject option = (JSONObject) o;
                        String type = option.getString("title");
                        if (StringUtils.isNotBlank(type)) {
                            serviceTypeList.add(type);
                        }
                    });
                }
                break;
            }
        }
        return serviceTypeList;
    }
}
