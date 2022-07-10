package com.chaoxing.activity.service.notice.record;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.mapper.NoticeRecordMapper;
import com.chaoxing.activity.model.NoticeRecord;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**通知记录查询服务
 * @author wwb
 * @version ver 1.0
 * @className NoticeRecordQueryService
 * @description
 * @blame wwb
 * @date 2022-02-11 17:19:35
 */
@Slf4j
@Service
public class NoticeRecordQueryService {

	@Resource
	private NoticeRecordMapper noticeRecordMapper;

	/**分页查询
	 * @Description 
	 * @author wwb
	 * @Date 2022-02-15 15:34:16
	 * @param page 分页
	 * @param fid 机构fid
	 * @param flags 活动标识（多个标识以逗号分割）
	 * @param type 通知类型
	 * @param content 是否包含内容
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.NoticeRecord>
	*/
	public Page<NoticeRecord> paging(Page<NoticeRecord> page, Integer fid, String flags, String type, boolean content) {
		long size = page.getSize();
		if (size > CommonConstant.MAX_PAGE_SIZE) {
			page.setSize(CommonConstant.MAX_PAGE_SIZE);
		}
		List<String> activityFlags = Optional.ofNullable(flags).filter(StringUtils::isNotBlank).map(v -> Lists.newArrayList(Arrays.asList(v.split(CommonConstant.DEFAULT_SEPARATOR)))).orElse(Lists.newArrayList());
		if (content) {
			page = noticeRecordMapper.pagingWithContent(page, fid, activityFlags, type);
		} else {
			page = noticeRecordMapper.paging(page, fid, activityFlags, type);
		}
		return page;
	}

	/**根据id查询通知记录
	 * @Description 
	 * @author wwb
	 * @Date 2022-02-15 16:08:30
	 * @param id
	 * @return com.chaoxing.activity.model.NoticeRecord
	*/
	public NoticeRecord getById(Integer id) {
		return noticeRecordMapper.selectById(id);
	}

}
