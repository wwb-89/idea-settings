package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.model.NoticeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知记录
 *
 * @className: NoticeRecordMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2022-02-11 17:01:23
 * @version: ver 1.0
 */
@Mapper
public interface NoticeRecordMapper extends BaseMapper<NoticeRecord> {

	/**分页查询
	 * @Description 
	 * @author wwb
	 * @Date 2022-02-15 11:32:25
	 * @param page
	 * @param fid
	 * @param flags
	 * @param type
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.NoticeRecord>
	*/
	Page<NoticeRecord> paging(Page<NoticeRecord> page, @Param("fid") Integer fid, @Param("flags") List<String> flags, @Param("type") String type);

	/**分页查询（包含通知内容）
	 * @Description 
	 * @author wwb
	 * @Date 2022-02-15 15:40:32
	 * @param page
	 * @param fid
	 * @param flags
	 * @param type
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.model.NoticeRecord>
	*/
	Page<NoticeRecord> pagingWithContent(Page<NoticeRecord> page, @Param("fid") Integer fid, @Param("flags") List<String> flags, @Param("type") String type);
}