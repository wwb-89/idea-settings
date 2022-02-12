package com.chaoxing.activity.service.notice.record;

import com.chaoxing.activity.mapper.NoticeRecordMapper;
import com.chaoxing.activity.model.NoticeRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**通知记录处理服务
 * @author wwb
 * @version ver 1.0
 * @className NoticeRecordHandleService
 * @description
 * @blame wwb
 * @date 2022-02-11 17:19:58
 */
@Slf4j
@Service
public class NoticeRecordHandleService {

	@Resource
	private NoticeRecordMapper noticeRecordMapper;

	/**添加
	 * @Description 
	 * @author wwb
	 * @Date 2022-02-11 17:24:50
	 * @param noticeRecord
	 * @return void
	*/
	public void add(NoticeRecord noticeRecord) {
		noticeRecordMapper.insert(noticeRecord);
	}

}
