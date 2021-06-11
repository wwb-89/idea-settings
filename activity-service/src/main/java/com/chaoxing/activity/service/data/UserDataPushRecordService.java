package com.chaoxing.activity.service.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.UserDataPushRecordMapper;
import com.chaoxing.activity.model.UserDataPushRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**用户数据推送记录服务
 * @author wwb
 * @version ver 1.0
 * @className UserDataPushRecordService
 * @description
 * @blame wwb
 * @date 2021-06-11 11:08:13
 */
@Slf4j
@Service
public class UserDataPushRecordService {

	@Resource
	private UserDataPushRecordMapper userDataPushRecordMapper;

	public void add(UserDataPushRecord userDataPushRecord) {
		Integer uid = userDataPushRecord.getUid();
		String repoType = userDataPushRecord.getRepoType();
		String repo = userDataPushRecord.getRepo();
		UserDataPushRecord existUserDataPushRecord = get(uid, repoType, repo);
		if (existUserDataPushRecord == null) {
			userDataPushRecordMapper.insert(userDataPushRecord);
		}
	}

	public UserDataPushRecord get(Integer uid, String repoType, String repo) {
		List<UserDataPushRecord> userDataPushRecords = userDataPushRecordMapper.selectList(new QueryWrapper<UserDataPushRecord>()
				.lambda()
				.eq(UserDataPushRecord::getUid, uid)
				.eq(UserDataPushRecord::getRepoType, repoType)
				.eq(UserDataPushRecord::getRepo, repo)
		);
		if (CollectionUtils.isNotEmpty(userDataPushRecords)) {
			return userDataPushRecords.get(0);
		}
		return null;
	}

}