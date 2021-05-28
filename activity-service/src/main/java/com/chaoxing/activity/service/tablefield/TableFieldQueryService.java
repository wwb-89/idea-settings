package com.chaoxing.activity.service.tablefield;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.tablefield.TableFieldDTO;
import com.chaoxing.activity.mapper.OrgTableFieldMapper;
import com.chaoxing.activity.mapper.TableFieldDetailMapper;
import com.chaoxing.activity.mapper.TableFieldMapper;
import com.chaoxing.activity.model.OrgTableField;
import com.chaoxing.activity.model.TableField;
import com.chaoxing.activity.model.TableFieldDetail;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/25 10:36 上午
 * <p>
 */

@Slf4j
@Service
public class TableFieldQueryService {

    @Autowired
    private TableFieldMapper tableFieldMapper;

    @Autowired
    private TableFieldDetailMapper tableFieldDetailMapper;

    @Autowired
    private OrgTableFieldMapper orgTableFieldMapper;

    /**查询机构的字段配置
     * @Description
     * @author huxiaolong
     * @Date 2021-05-25 10:42:32
     * @param fid
     * @return void
     */
    public Map<String, Object> searchActivityStatField(Integer fid) {
        TableField tableField = getOrgActivityStatTableField();
        // 获取tableField 对应的默认字段配置详情
        List<TableFieldDetail> defaultTableFields = tableFieldDetailMapper.selectList(new QueryWrapper<TableFieldDetail>()
                .lambda()
                .eq(TableFieldDetail::getTableFieldId, tableField.getId()));
        // 机构对应的字段配置列表
        List<OrgTableField> orgFields = listOrgTableField(fid, tableField.getId());

        List<TableFieldDTO> displayTableFields = new ArrayList<>();
        Map<String, Object> result = Maps.newHashMap();
        // 不存在机构对应的字段配置，则设置默认字段配置展示
        if (CollectionUtils.isEmpty(orgFields)) {
            for (TableFieldDetail detail : defaultTableFields) {
                if (detail.getDefaultChecked()) {
                    TableFieldDTO item = new TableFieldDTO();
                    BeanUtils.copyProperties(detail, item);
                    displayTableFields.add(item);
                }
            }

            result.put("settingData", defaultTableFields);
            result.put("tableFields", displayTableFields);
            return result;
        }
        // list 转 map，便于查找对象是否存在
        Map<Integer, OrgTableField> orgFieldMap = Maps.newHashMap();
        for (OrgTableField field : orgFields) {
            orgFieldMap.put(field.getTableFieldDetailId(), field);
        }

        for (TableFieldDetail detail : defaultTableFields) {
            OrgTableField orgField = orgFieldMap.get(detail.getId());
            if (orgField != null) {
                detail.setDefaultChecked(Boolean.TRUE);
                detail.setDefaultTop(orgField.getTop());

                TableFieldDTO item = new TableFieldDTO();
                BeanUtils.copyProperties(detail, item);
                item.setSequence(orgField.getSequence());
                displayTableFields.add(item);
            }
        }

        result.put("settingData", defaultTableFields);
        result.put("tableFields", displayTableFields);

        return result;
    }

    /**根据fid、tableFieldId查询机构对应的字段配置列表
     * @Description
     * @author huxiaolong
     * @Date 2021-05-25 17:18:37
     * @param fid
     * @param tableFieldId
     * @return java.util.List<com.chaoxing.activity.model.OrgTableField>
     */
    public List<OrgTableField> listOrgTableField(Integer fid, Integer tableFieldId) {
        // 机构对应的字段配置列表
        return orgTableFieldMapper.selectList(new QueryWrapper<OrgTableField>().lambda()
                .eq(OrgTableField::getFid, fid)
                .eq(OrgTableField::getTableFieldId, tableFieldId)
                .orderByAsc(OrgTableField::getSequence));

    }

    /**查询关联类型为机构、类型为活动统计的tableField
    * @Description 
    * @author huxiaolong
    * @Date 2021-05-25 17:17:41
    * @param 
    * @return com.chaoxing.activity.model.TableField
    */
    private TableField getOrgActivityStatTableField() {
        return getTableField(TableField.Type.ACTIVITY_STAT, TableField.AssociatedType.ORG);
    }

    /**根据类型和关联的类型查询TableField
     * @Description 
     * @author wwb
     * @Date 2021-05-27 17:40:46
     * @param type
     * @param associatedType
     * @return com.chaoxing.activity.model.TableField
    */
    public TableField getTableField(TableField.Type type, TableField.AssociatedType associatedType) {
        List<TableField> tableFields = tableFieldMapper.selectList(new QueryWrapper<TableField>()
                .lambda()
                .eq(TableField::getType, type.getValue())
                .eq(TableField::getAssociatedType, associatedType.getValue())
                .eq(TableField::getDeleted, Boolean.FALSE));
        if (CollectionUtils.isNotEmpty(tableFields)) {
            return tableFields.get(0);
        }
        return null;
    }

    /**根据tableFieldId查询tableFieldDetail列表
     * @Description 
     * @author wwb
     * @Date 2021-05-27 18:05:35
     * @param tableFieldId
     * @return java.util.List<com.chaoxing.activity.model.TableFieldDetail>
    */
    public List<TableFieldDetail> listByTableFieldId(Integer tableFieldId) {
        return tableFieldDetailMapper.selectList(new QueryWrapper<TableFieldDetail>()
                .lambda()
                .eq(TableFieldDetail::getTableFieldId, tableFieldId)
                .eq(TableFieldDetail::getDeleted, false)
        );
    }

    /**查询机构配置的tableFieldDetail
     * @Description 
     * @author wwb
     * @Date 2021-05-27 18:09:10
     * @param fid
     * @param type
     * @param associatedType
     * @return java.util.List<com.chaoxing.activity.model.OrgTableField>
    */
    public List<OrgTableField> listOrgTableField(Integer fid, TableField.Type type, TableField.AssociatedType associatedType) {
        List<OrgTableField> result = Lists.newArrayList();
        // 根据type和associatedType查询TableField
        TableField tableField = getTableField(type, associatedType);
        if (tableField == null) {
            return result;
        }
        Integer tableFieldId = tableField.getId();
        return listOrgTableField(fid, tableFieldId);
    }

    /**根据类型和关联类型查询tableFieldDetail列表
     * @Description 
     * @author wwb
     * @Date 2021-05-27 18:11:14
     * @param type
     * @param associatedType
     * @return java.util.List<com.chaoxing.activity.model.TableFieldDetail>
    */
    public List<TableFieldDetail> listTableFieldDetail(TableField.Type type, TableField.AssociatedType associatedType) {
        List<TableFieldDetail> tableFieldDetails = Lists.newArrayList();
        TableField tableField = getTableField(type, associatedType);
        if (tableField == null) {
            return tableFieldDetails;
        }
        return listByTableFieldId(tableField.getId());
    }

}
