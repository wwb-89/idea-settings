package com.chaoxing.activity.service.user.result;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.UserFormCollectionGroupDTO;
import com.chaoxing.activity.dto.UserResultDTO;
import com.chaoxing.activity.dto.export.ExportDataDTO;
import com.chaoxing.activity.dto.query.UserResultQueryDTO;
import com.chaoxing.activity.mapper.UserResultMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.TableField;
import com.chaoxing.activity.model.TableFieldDetail;
import com.chaoxing.activity.model.UserResult;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
	private SignApiService signApiService;

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
		return Objects.equals(UserResult.QualifiedStatusEnum.QUALIFIED.getValue(), Optional.ofNullable(userResults).orElse(Lists.newArrayList()).stream().findFirst().map(UserResult::getQualifiedStatus).orElse(null));
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
		Integer qualifiedStatus = Optional.ofNullable(userResults).orElse(Lists.newArrayList()).stream().findFirst().map(UserResult::getQualifiedStatus).orElse(null);
		return Optional.ofNullable(UserResult.QualifiedStatusEnum.fromValue(qualifiedStatus)).orElse(UserResult.QualifiedStatusEnum.WAIT).getName();
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
	 * @param queryParams
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.stat.UserResultDTO>
	 */
	public Page<UserResultDTO> pageUserResult(Page<UserResultDTO> page, UserResultQueryDTO queryParams) {
		if (queryParams.getOrderFieldId() != null) {
			TableFieldDetail tableFieldDetail = tableFieldQueryService.getFieldDetailById(queryParams.getOrderFieldId());
			queryParams.setOrderField(tableFieldDetail.getCode());
		}
		page = userResultMapper.pageUserResult(page, queryParams);
		List<Integer> uids = page.getRecords().stream().map(UserResultDTO::getUid).filter(Objects::nonNull).collect(Collectors.toList());
		List<UserFormCollectionGroupDTO> groupUserFormCollections = signApiService.groupUserFormCollections(uids);
		if (CollectionUtils.isNotEmpty(groupUserFormCollections)) {
			Map<Integer, Integer> uidFilledCountMap = groupUserFormCollections.stream().collect(Collectors.toMap(UserFormCollectionGroupDTO::getUid, UserFormCollectionGroupDTO::getFilledFormNum, (v1, v2) -> v2));
			page.getRecords().forEach(v -> {
				v.setFilledFormCollectionNum(Optional.ofNullable(uidFilledCountMap.get(v.getUid())).orElse(0));
			});
		}
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
		List<String> activityHeader = Lists.newArrayList();
		activityHeader.add("活动id");
		headers.add(activityHeader);
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
						itemData.add(Optional.ofNullable(qualifiedStatusEnum).map(UserResult.QualifiedStatusEnum::getName).orElse(""));
						break;
					case "studentNo":
						itemData.add(record.getStudentNo());
						break;
					case "mobile":
						itemData.add(record.getMobile());
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
	 * @param queryParam
	 * @return com.chaoxing.activity.dto.export.ExportDataDTO
	 */
	public ExportDataDTO packageExportData(UserResultQueryDTO queryParam) {
		List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listActivityShowTableFieldDetail(queryParam.getActivityId(), TableField.Type.RESULT_MANAGE, TableField.AssociatedType.ACTIVITY);
		Page<UserResultDTO> page = pageUserResult(new Page<>(1, Integer.MAX_VALUE), queryParam);
		return ExportDataDTO.builder()
				.headers(listResultInspectionHeader(tableFieldDetails))
				.data(listData(page.getRecords(), tableFieldDetails))
				.build();
	}

	/**根据活动id查询所有的活动成绩
	 * @Description 按得分降序排序
	 * @author wwb
	 * @Date 2021-06-25 16:06:55
	 * @param activityId
	 * @return java.util.List<com.chaoxing.activity.model.UserResult>
	*/
	public List<UserResult> listByActivityId(Integer activityId) {
		return userResultMapper.selectList(new QueryWrapper<UserResult>()
			.lambda()
				.eq(UserResult::getActivityId, activityId)
				.orderByDesc(UserResult::getTotalScore)
		);
	}

	/**根据活动和合格状态，计算对应合格状态的数量
	* @Description
	* @author huxiaolong
	* @Date 2021-06-29 10:04:25
	* @param activityId
	* @param qualifiedStatusEnum
	* @return int
	*/
	public int countQualifiedStatusNum(Integer activityId, UserResult.QualifiedStatusEnum qualifiedStatusEnum) {
		return userResultMapper.selectCount(new QueryWrapper<UserResult>()
				.lambda()
				.eq(UserResult::getActivityId, activityId)
				.eq(UserResult::getQualifiedStatus, qualifiedStatusEnum.getValue()));
	}

	/**查询活动下合格的用户uid列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-05 14:18:34
	 * @param activityId
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listActivityQualifiedUid(Integer activityId) {
		List<UserResult> userResults = userResultMapper.selectList(new QueryWrapper<UserResult>()
				.lambda()
				.eq(UserResult::getActivityId, activityId)
				.eq(UserResult::getQualifiedStatus, UserResult.QualifiedStatusEnum.QUALIFIED.getValue())
				.select(UserResult::getUid)
		);
		return Optional.ofNullable(userResults).orElse(Lists.newArrayList()).stream().map(UserResult::getUid).collect(Collectors.toList());
	}
}
