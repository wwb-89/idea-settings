package com.chaoxing.activity.service;

import com.chaoxing.activity.mapper.GroupRegionFilterMapper;
import com.chaoxing.activity.model.GroupRegionFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**地区组别服务
 * @author wwb
 * @version ver 1.0
 * @className GroupRegionFilterService
 * @description
 * @blame wwb
 * @date 2020-11-20 18:11:17
 */
@Slf4j
@Service
public class GroupRegionFilterService {

	@Resource
	private GroupRegionFilterMapper groupRegionFilterMapper;

	/**根据组别code查询地区列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-20 18:12:15
	 * @param groupCode
	 * @return java.util.List<com.chaoxing.activity.model.GroupRegionFilter>
	*/
	public List<GroupRegionFilter> listByGroupCode(String groupCode) {
		return groupRegionFilterMapper.listByGroupCode(groupCode);
	}

}