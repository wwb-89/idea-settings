package com.chaoxing.activity.service.data;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.DataPushRecordMapper;
import com.chaoxing.activity.model.DataPushRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**数据推送记录处理服务
 * @author wwb
 * @version ver 1.0
 * @className DataPushRecordHandleService
 * @description
 * @blame wwb
 * @date 2021-06-24 18:30:14
 */
@Slf4j
@Service
public class DataPushRecordHandleService {

    @Resource
    private DataPushRecordMapper dataPushRecordMapper;

    /**新增
     * @Description 
     * @author wwb
     * @Date 2021-06-24 20:34:57
     * @param dataPushRecord
     * @return void
    */
    public void add(DataPushRecord dataPushRecord) {
        dataPushRecordMapper.insert(dataPushRecord);
    }

    /**更新
     * @Description 
     * @author wwb
     * @Date 2021-06-24 20:37:02
     * @param id
     * @param record
     * @return void
    */
    public void update(Long id, String record) {
        dataPushRecordMapper.update(null, new UpdateWrapper<DataPushRecord>()
            .lambda()
                .eq(DataPushRecord::getId, id)
                .set(DataPushRecord::getRecord, record)
        );
    }
    /**删除推送记录
     * @Description 
     * @author wwb
     * @Date 2021-06-24 20:27:14
     * @param id
     * @return void
    */
    public void delete(Long id) {
        dataPushRecordMapper.deleteById(id);
    }

}