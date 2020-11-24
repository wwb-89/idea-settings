package com.chaoxing.activity.service;

import com.chaoxing.activity.mapper.WebTemplateAppDataMapper;
import com.chaoxing.activity.mapper.WebTemplateAppMapper;
import com.chaoxing.activity.mapper.WebTemplateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className WebTemplateService
 * @description
 * @blame wwb
 * @date 2020-11-23 20:46:02
 */
@Slf4j
@Service
public class WebTemplateService {

	@Resource
	private WebTemplateMapper webTemplateMapper;
	@Resource
	private WebTemplateAppMapper webTemplateAppMapper;
	@Resource
	private WebTemplateAppDataMapper webTemplateAppDataMapper;


}
