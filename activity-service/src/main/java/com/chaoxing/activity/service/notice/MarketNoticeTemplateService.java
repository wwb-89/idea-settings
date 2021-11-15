package com.chaoxing.activity.service.notice;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.notice.MarketNoticeTemplateDTO;
import com.chaoxing.activity.mapper.MarketNoticeTemplateMapper;
import com.chaoxing.activity.model.MarketNoticeTemplate;
import com.chaoxing.activity.model.SystemNoticeTemplate;
import com.chaoxing.activity.service.activity.market.MarketValidationService;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**活动市场通知模版服务
 * @author wwb
 * @version ver 1.0
 * @className MarketNoticeTemplateService
 * @description
 * @blame wwb
 * @date 2021-11-11 14:24:21
 */
@Slf4j
@Service
public class MarketNoticeTemplateService {

	@Resource
	private MarketNoticeTemplateMapper marketNoticeTemplateMapper;

	@Resource
	private SystemNoticeTemplateService systemNoticeTemplateService;
	@Resource
	private MarketValidationService marketValidationService;

	/**查询通知字段列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-11 15:19:06
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.service.notice.NoticeFieldDTO>
	*/
	public List<NoticeFieldDTO> listNoticeField() {
		return NoticeFieldDTO.buildFromNoticeFieldEnum();
	}

	/**根据市场id查询通知模版列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-11 14:42:04
	 * @param marketId
	 * @return java.util.List<com.chaoxing.activity.dto.notice.MarketNoticeTemplateDTO>
	*/
	public List<MarketNoticeTemplateDTO> listByMarketId(Integer marketId) {
		// 先查询系统的
		List<SystemNoticeTemplate> systemNoticeTemplates = systemNoticeTemplateService.list();
		// 在查询定制的
		List<MarketNoticeTemplate> marketNoticeTemplates = list(marketId);
		// 合并
		List<MarketNoticeTemplateDTO> marketNoticeTemplateDtos = merge(marketId, systemNoticeTemplates, marketNoticeTemplates);
		marketNoticeTemplateDtos.forEach(v -> v.handleShowValue());
		return marketNoticeTemplateDtos;
	}

	private List<MarketNoticeTemplate> list(Integer marketId) {
		return marketNoticeTemplateMapper.selectList(new LambdaQueryWrapper<MarketNoticeTemplate>()
				.eq(MarketNoticeTemplate::getMarketId, marketId)
		);
	}

	private List<MarketNoticeTemplateDTO> merge(Integer marketId, List<SystemNoticeTemplate> systemNoticeTemplates, List<MarketNoticeTemplate> marketNoticeTemplates) {
		List<MarketNoticeTemplateDTO> result = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(systemNoticeTemplates)) {
			Map<String, MarketNoticeTemplate> noticeTypeObjectMap = marketNoticeTemplates.stream().collect(Collectors.toMap(MarketNoticeTemplate::getNoticeType, v -> v, (v1, v2) -> v2));
			for (SystemNoticeTemplate systemNoticeTemplate : systemNoticeTemplates) {
				MarketNoticeTemplateDTO marketNoticeTemplateDto = MarketNoticeTemplateDTO.buildFromSystemNoticeTemplate(marketId, systemNoticeTemplate);
				MarketNoticeTemplate marketNoticeTemplate = noticeTypeObjectMap.get(marketNoticeTemplateDto.getNoticeType());
				if (marketNoticeTemplate != null) {
					BeanUtils.copyProperties(marketNoticeTemplate, marketNoticeTemplateDto);
				}
				result.add(marketNoticeTemplateDto);
			}
		}
		return result;
	}

	/**根据市场id和通知类型查询通知模版
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-11 14:47:12
	 * @param marketId
	 * @param noticeType
	 * @return com.chaoxing.activity.model.MarketNoticeTemplate
	*/
	public MarketNoticeTemplate getByMarketIdAndNoticeType(Integer marketId, String noticeType) {
		List<MarketNoticeTemplate> marketNoticeTemplates = marketNoticeTemplateMapper.selectList(new LambdaQueryWrapper<MarketNoticeTemplate>()
				.eq(MarketNoticeTemplate::getMarketId, marketId)
				.eq(MarketNoticeTemplate::getNoticeType, noticeType)
		);
		return Optional.ofNullable(marketNoticeTemplates).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
	}

	public MarketNoticeTemplateDTO getMarketOrSystemNoticeTemplate(Integer marketId, String noticeType) {
		MarketNoticeTemplate marketNoticeTemplate = getByMarketIdAndNoticeType(marketId, noticeType);
		if (marketNoticeTemplate == null) {
			SystemNoticeTemplate sysNoticeTemplate = systemNoticeTemplateService.getByNoticeType(noticeType);
			if (sysNoticeTemplate == null) {
				return null;
			}
			return MarketNoticeTemplateDTO.buildFromSystemNoticeTemplate(null, sysNoticeTemplate);
		}
		MarketNoticeTemplateDTO result = new MarketNoticeTemplateDTO();
		BeanUtils.copyProperties(marketNoticeTemplate, result);
		return result;
	}

	/**新增或更新
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-11 14:57:27
	 * @param marketNoticeTemplateDto
	 * @param operateUser
	 * @return void
	*/
	public void addOrEdit(MarketNoticeTemplateDTO marketNoticeTemplateDto, OperateUserDTO operateUser) {
		Integer marketId = marketNoticeTemplateDto.getMarketId();
		marketValidationService.manageAble(marketId, operateUser);
		String noticeType = marketNoticeTemplateDto.getNoticeType();
		MarketNoticeTemplate existMarketNoticeTemplate = getByMarketIdAndNoticeType(marketId, noticeType);
		MarketNoticeTemplate marketNoticeTemplate = marketNoticeTemplateDto.buildMarketNoticeTemplate(existMarketNoticeTemplate);
		if (marketNoticeTemplate.getId() == null) {
			// 新增
			marketNoticeTemplateMapper.insert(marketNoticeTemplate);
		} else {
			// 修改
			marketNoticeTemplateMapper.updateById(marketNoticeTemplate);
		}
	}

	public void enable(Integer marketId, String noticeType, OperateUserDTO operateUser) {
		updateEnable(marketId, noticeType, operateUser, true);
	}

	public void disable(Integer marketId, String noticeType, OperateUserDTO operateUser) {
		updateEnable(marketId, noticeType, operateUser, false);
	}

	private void updateEnable(Integer marketId, String noticeType, OperateUserDTO operateUser, boolean enable) {
		marketValidationService.manageAble(marketId, operateUser);
		MarketNoticeTemplate marketNoticeTemplate = getByMarketIdAndNoticeType(marketId, noticeType);
		if (marketNoticeTemplate == null) {
			// 使用系统通知模版来复制
			SystemNoticeTemplate systemNoticeTemplate = systemNoticeTemplateService.getByNoticeType(noticeType);
			Optional.ofNullable(systemNoticeTemplate).orElseThrow(() -> new BusinessException("未知的通知模版"));
			marketNoticeTemplate = new MarketNoticeTemplate();
			BeanUtils.copyProperties(systemNoticeTemplate, marketNoticeTemplate);
			marketNoticeTemplate.setMarketId(marketId);
			marketNoticeTemplate.setEnable(enable);
			marketNoticeTemplateMapper.insert(marketNoticeTemplate);
		} else {
			marketNoticeTemplateMapper.update(null, new LambdaUpdateWrapper<MarketNoticeTemplate>()
					.eq(MarketNoticeTemplate::getId, marketNoticeTemplate.getId())
					.set(MarketNoticeTemplate::getEnable, enable)
			);
		}
	}

}
