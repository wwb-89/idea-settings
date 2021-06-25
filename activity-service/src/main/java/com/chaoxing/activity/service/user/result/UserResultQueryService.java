package com.chaoxing.activity.service.user.result;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.export.ExportDataDTO;
import com.chaoxing.activity.dto.stat.UserResultDTO;
import com.chaoxing.activity.mapper.UserResultMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.TableField;
import com.chaoxing.activity.model.TableFieldDetail;
import com.chaoxing.activity.model.UserResult;
import com.chaoxing.activity.service.inspection.InspectionConfigQueryService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**用户成绩查询服务
 * @author wwb
 * @version ver 1.0
 * @className UserResultQueryService
 * @description
 * @blame wwb
 * @date 2021-06-23 22:40:49
 */
@Slf4j
@Service
public class UserResultQueryService {

	@Resource
	private UserResultMapper userResultMapper;

	@Resource
	private TableFieldQueryService tableFieldQueryService;

	@Resource
	private InspectionConfigQueryService inspectionConfigQueryService;


	/**用户成绩是否合格
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-23 22:42:19
	 * @param uid
	 * @param activityId
	 * @return boolean
	*/
	public boolean isUserQualified(Integer uid, Integer activityId) {
		List<UserResult> userResults = userResultMapper.selectList(new QueryWrapper<UserResult>()
				.lambda()
				.eq(UserResult::getUid, uid)
				.eq(UserResult::getActivityId, activityId)
		);
		if (CollectionUtils.isEmpty(userResults)) {
			return false;
		}
		UserResult userResult = userResults.get(0);
		Integer qualifiedStatus = userResult.getQualifiedStatus();
		return Objects.equals(UserResult.QualifiedStatusEnum.QUALIFIED.getValue(), qualifiedStatus);
	}

	/**获取用户成绩合格的描述
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-25 09:48:58
	 * @param uid
	 * @param activityId
	 * @return java.lang.String
	*/
	public String getResultQualifiedDescription(Integer uid, Integer activityId) {
		List<UserResult> userResults = userResultMapper.selectList(new QueryWrapper<UserResult>()
				.lambda()
				.eq(UserResult::getUid, uid)
				.eq(UserResult::getActivityId, activityId)
		);
		UserResult.QualifiedStatusEnum qualifiedStatusEnum = null;
		if (CollectionUtils.isNotEmpty(userResults)) {
			UserResult userResult = userResults.get(0);
			qualifiedStatusEnum = UserResult.QualifiedStatusEnum.fromValue(userResult.getQualifiedStatus());
		}
		if (qualifiedStatusEnum == null) {
			qualifiedStatusEnum = UserResult.QualifiedStatusEnum.WAIT;
		}
		return qualifiedStatusEnum.getName();
	}

	/**获取用户成绩
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-24 15:07:31
	 * @param uid
	 * @param activityId
	 * @return com.chaoxing.activity.model.UserResult
	*/
	public UserResult getUserResult(Integer uid, Integer activityId) {
		List<UserResult> userResults = userResultMapper.selectList(new QueryWrapper<UserResult>()
				.lambda()
				.eq(UserResult::getUid, uid)
				.eq(UserResult::getActivityId, activityId)
		);
		return userResults.stream().findFirst().orElse(null);
	}

	/**分页查询活动中用户的成绩
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-24 15:37:06
	 * @param page
	 * @param activityId
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.UserResultDTO>
	 */
	public Page<UserResultDTO> pageUserResult(Page<UserResultDTO> page, Integer activityId) {
		// 查找考核配置
//		InspectionConfig inspectionConfig = inspectionConfigQueryService.getByActivityId(activityId);
		// 查找已报名的用户id
		page = userResultMapper.pageUserResult(page, activityId);
		return page;
	}
	/**获取excel表头
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-01 16:20:48
	 * @param tableFieldDetails
	 * @return java.util.List<java.util.List<java.lang.String>>
	 */
	private List<List<String>> listResultInspectionHeader(List<TableFieldDetail> tableFieldDetails) {
		List<List<String>> headers = Lists.newArrayList();
		for (TableFieldDetail tableFieldDetail : tableFieldDetails) {
			List<String> header = Lists.newArrayList();
			header.add(tableFieldDetail.getName());
			headers.add(header);
		}
		return headers;
	}

	private String valueToString(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

	/**获取数据
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-01 16:19:14
	 * @param records
	 * @param tableFieldDetails
	 * @return java.util.List<java.util.List<java.lang.String>>
	 */
	private List<List<String>> listData(List<UserResultDTO> records, List<TableFieldDetail> tableFieldDetails) {
		List<List<String>> data = Lists.newArrayList();
		if (CollectionUtils.isEmpty(records)) {
			return data;
		}
		for (UserResultDTO record : records) {
			List<String> itemData = Lists.newArrayList();
			itemData.add(String.valueOf(record.getActivityId()));
			for (TableFieldDetail tableFieldDetail : tableFieldDetails) {
				itemData.add(String.valueOf(record.getActivityId()));
				String code = tableFieldDetail.getCode();
				switch (code) {
					case "realName":
						itemData.add(record.getRealName());
						break;
					case "signedInNum":
						itemData.add(Activity.getStatusDescription(record.getSignedInNum()));
						break;
					case "signedInRate":
						itemData.add(valueToString(record.getSignedInRate()));
						break;
					case "ratingNum":
						itemData.add(valueToString(record.getRatingNum()));
						break;
					case "totalScore":
						itemData.add(valueToString(record.getTotalScore()));
						break;
					case "prize":
						itemData.add(record.getPrize());
						break;
					case "qualifiedStatus":
						UserResult.QualifiedStatusEnum qualifiedStatusEnum = UserResult.QualifiedStatusEnum.fromValue(record.getQualifiedStatus());
						if (qualifiedStatusEnum == null) {
							itemData.add("");
						} else {
							itemData.add(qualifiedStatusEnum.getName());
						}
						break;
					default:

				}
			}
			data.add(itemData);
		}
		return data;
	}

	/**封装easyexcel导出所需导出实体
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-01 16:18:42
	 * @param activityId
	 * @return com.chaoxing.activity.dto.export.ExportDataDTO
	 */
	public ExportDataDTO packageExportData(Integer activityId) {
		List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listActivityShowTableFieldDetail(activityId, TableField.Type.RESULT_MANAGE, TableField.AssociatedType.ACTIVITY);
		ExportDataDTO exportData = new ExportDataDTO();
		Page<UserResultDTO> page = new Page<>(1, Integer.MAX_VALUE);
		page = pageUserResult(page, activityId);
		List<List<String>> headers = listResultInspectionHeader(tableFieldDetails);
		exportData.setHeaders(headers);
		List<List<String>> data = listData(page.getRecords(), tableFieldDetails);
		exportData.setData(data);
		return exportData;
	}

}
