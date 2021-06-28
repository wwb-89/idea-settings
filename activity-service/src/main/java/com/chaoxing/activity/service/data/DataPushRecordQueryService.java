package com.chaoxing.activity.service.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.DataPushRecordMapper;
import com.chaoxing.activity.model.DataPushRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**数据推送记录查询服务
 * @author wwb
 * @version ver 1.0
 * @className DataPushRecordQueryService
 * @description
 * @blame wwb
 * @date 2021-06-11 11:08:13
 */
@Slf4j
@Service
public class DataPushRecordQueryService {

	@Resource
	private DataPushRecordMapper dataPushRecordMapper;

	/**根据主键标识、仓库类型、数据类型查询推送记录
	 * @Description
	 * @author wwb
	 * @Date 2021-06-24 20:17:59
	 * @param identify
	 * @param repoType
	 * @param dataType
	 * @return com.chaoxing.activity.model.DataPushRecord
	*/
	public DataPushRecord get(String identify, String repoType, String dataType) {
		List<DataPushRecord> dataPushRecords = dataPushRecordMapper.selectList(new QueryWrapper<DataPushRecord>()
				.lambda()
				.eq(DataPushRecord::getIdentify, identify)
				.eq(DataPushRecord::getRepoType, repoType)
				.eq(DataPushRecord::getDataType, dataType)
		);
		return dataPushRecords.stream().findFirst().orElse(null);
	}

	/**根据推送仓库记录id查询推送记录
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-24 20:59:03
	 * @param record
	 * @return com.chaoxing.activity.model.DataPushRecord
	*/
	public DataPushRecord getByRecord(String record) {
		List<DataPushRecord> dataPushRecords = dataPushRecordMapper.selectList(new QueryWrapper<DataPushRecord>()
				.lambda()
				.eq(DataPushRecord::getRecord, record)
		);
		return dataPushRecords.stream().findFirst().orElse(null);
	}

}