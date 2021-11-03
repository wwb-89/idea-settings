package com.chaoxing.activity.service.data;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.mapper.OrgUserDataPushRecordMapper;
import com.chaoxing.activity.model.OrgUserDataPushRecord;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**机构用户数据推送记录服务
 * @author wwb
 * @version ver 1.0
 * @className OrgUserDataPushRecordService
 * @description
 * @blame wwb
 * @date 2021-11-02 18:07:49
 */
@Slf4j
@Service
public class OrgUserDataPushRecordService {

    @Resource
    private OrgUserDataPushRecordMapper orgUserDataPushRecordMapper;

    /**新增或更新
     * @Description 
     * @author wwb
     * @Date 2021-11-02 18:14:44
     * @param orgUserDataPushRecord
     * @return void
    */
    public void addOrUpdate(OrgUserDataPushRecord orgUserDataPushRecord) {
        Integer uid = orgUserDataPushRecord.getUid();
        Integer activityId = orgUserDataPushRecord.getActivityId();
        OrgUserDataPushRecord existOrgUserDataPushRecord = get(uid, activityId);
        if (existOrgUserDataPushRecord == null) {
            orgUserDataPushRecordMapper.insert(orgUserDataPushRecord);
        } else {
            orgUserDataPushRecordMapper.update(null, new LambdaUpdateWrapper<OrgUserDataPushRecord>()
                    .eq(OrgUserDataPushRecord::getUid, uid)
                    .eq(OrgUserDataPushRecord::getActivityId, activityId)
                    .set(OrgUserDataPushRecord::getFormUserId, orgUserDataPushRecord.getFormUserId())
            );
        }
    }

    /**根据uid和机构id查询
     * @Description 
     * @author wwb
     * @Date 2021-11-02 18:12:17
     * @param uid
     * @param activityId
     * @return com.chaoxing.activity.model.OrgUserDataPushRecord
    */
    public OrgUserDataPushRecord get(Integer uid, Integer activityId) {
        List<OrgUserDataPushRecord> orgUserDataPushRecords = orgUserDataPushRecordMapper.selectList(new LambdaQueryWrapper<OrgUserDataPushRecord>()
                .eq(OrgUserDataPushRecord::getUid, uid)
                .eq(OrgUserDataPushRecord::getActivityId, activityId)
        );
        return Optional.ofNullable(orgUserDataPushRecords).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
    }

}