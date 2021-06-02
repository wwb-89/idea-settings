package com.chaoxing.activity.service.export;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ExportRecordMapper;
import com.chaoxing.activity.model.ExportRecord;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/1 5:17 下午
 * <p>
 */
@Slf4j
@Service
public class ExportRecordQueryService {

    @Resource
    private ExportRecordMapper exportRecordMapper;

    /**根据用户id，查询用户导出记录
     * @Description
     * @author huxiaolong
     * @Date 2021-06-01 17:02:06
     * @param uid
     * @return void
     */
    public List<ExportRecord> listRecord(Integer uid, String exportType) {
        ExportRecord.ExportTypeEnum exportTypeEnum = ExportRecord.ExportTypeEnum.fromValue(exportType);
        if (exportTypeEnum == null) {
            return Lists.newArrayList();
        }
        return exportRecordMapper.selectList(new QueryWrapper<ExportRecord>()
                .lambda()
                .eq(ExportRecord::getCreateUid, uid)
                .eq(ExportRecord::getExportType, exportType)
        );
    }
}
