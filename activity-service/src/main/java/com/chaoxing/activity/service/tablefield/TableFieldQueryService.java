package com.chaoxing.activity.service.tablefield;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.OrgTableFieldMapper;
import com.chaoxing.activity.mapper.TableFieldDetailMapper;
import com.chaoxing.activity.mapper.TableFieldMapper;
import com.chaoxing.activity.model.OrgTableField;
import com.chaoxing.activity.model.TableField;
import com.chaoxing.activity.model.TableFieldDetail;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private TableFieldMapper tableFieldMapper;
    @Resource
    private TableFieldDetailMapper tableFieldDetailMapper;
    @Resource
    private OrgTableFieldMapper orgTableFieldMapper;

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
                .orderByAsc(TableFieldDetail::getSequence)
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

    /**查询出机构需要显示的table field detail列表
     * @Description 
     * @author wwb
     * @Date 2021-06-03 13:23:43
     * @param fid
     * @param type
     * @param associatedType
     * @return java.util.List<com.chaoxing.activity.model.TableFieldDetail>
    */
    public List<TableFieldDetail> listOrgShowTableFieldDetail(Integer fid, TableField.Type type, TableField.AssociatedType associatedType) {
        List<TableFieldDetail> result = Lists.newArrayList();
        List<TableFieldDetail> tableFieldDetails = listTableFieldDetail(type, associatedType);
        if (CollectionUtils.isEmpty(tableFieldDetails)) {
            return result;
        }
        TableFieldDetail firstTableFieldDetail = tableFieldDetails.get(0);
        Integer tableFieldId = firstTableFieldDetail.getTableFieldId();
        List<OrgTableField> orgTableFields = listOrgTableField(fid, tableFieldId);
        // 以orgTableFields作为排序依据
        if (CollectionUtils.isEmpty(orgTableFields)) {
            for (TableFieldDetail fieldDetail : tableFieldDetails) {
                if (fieldDetail.getDefaultChecked()) {
                    result.add(fieldDetail);
                }
            }
        } else {
            Map<Integer, TableFieldDetail> detailIdDetailMap = tableFieldDetails.stream().collect(Collectors.toMap(TableFieldDetail::getId, v -> v));
            for (OrgTableField orgTableField : orgTableFields) {
                Integer tableFieldDetailId = orgTableField.getTableFieldDetailId();
                TableFieldDetail tableFieldDetail = detailIdDetailMap.get(tableFieldDetailId);
                if (tableFieldDetail != null) {
                    result.add(tableFieldDetail);
                }
            }
        }
        return result;
    }

}
