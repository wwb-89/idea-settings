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
	 * @param areaCode 区域编码
	 * @return java.util.List<com.chaoxing.activity.model.GroupRegionFilter>
	*/
	public List<GroupRegionFilter> listByGroupCode(String areaCode) {
		return groupRegionFilterMapper.listByGroupCode(areaCode);
	}

	/**根据code查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-25 18:35:32
	 * @param code
	 * @return com.chaoxing.activity.model.GroupRegionFilter
	*/
	public GroupRegionFilter getByCode(String code) {
		return groupRegionFilterMapper.getByCode(code);
	}

}