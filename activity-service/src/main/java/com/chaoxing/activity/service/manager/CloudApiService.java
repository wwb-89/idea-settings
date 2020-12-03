package com.chaoxing.activity.service.manager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

/**云盘api服务
 * @author wwb
 * @version ver 1.0
 * @className CloudApiService
 * @description
 * @blame wwb
 * @date 2020-11-10 19:59:05
 */
@Slf4j
@Service
public class CloudApiService {

	/** 上传url */
	private static final String UPLOAD_URL = "http://cs.ananas.chaoxing.com/upload?uid=-1&prdid=40";
	/** 资源状态url */
	private static final String GET_CLOUD_RESOURCE_STATUS_URL = "http://cs.ananas.chaoxing.com/status/";
	/** 云盘图片url */
	private static final String IMG_URL = "http://d0.ananas.chaoxing.com/download/";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	/**上传云盘
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 20:15:52
	 * @param file
	 * @return java.lang.String
	*/
	public String upload(MultipartFile file) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setConnection("Keep-Alive");
		headers.setCacheControl("no-cache");
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
		parts.add("file", file.getResource());
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(parts, headers);
		ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(UPLOAD_URL, httpEntity, String.class);
		String result = stringResponseEntity.getBody();
		return result;
	}

	/**上传文件
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 20:18:34
	 * @param file
	 * @return java.lang.String
	*/
	public String upload(File file) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setConnection("Keep-Alive");
		headers.setCacheControl("no-cache");
		FileSystemResource resource = new FileSystemResource(file);
		MultiValueMap<String, FileSystemResource> data = new LinkedMultiValueMap<>();
		data.add("file", resource);
		HttpEntity<MultiValueMap<String, FileSystemResource>> httpEntity = new HttpEntity<>(data);
		ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(UPLOAD_URL, httpEntity, String.class);
		String result = stringResponseEntity.getBody();
		return result;
	}

	/**获取云盘资源状态
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 20:08:05
	 * @param cloudId
	 * @return java.lang.String
	*/
	public String getStatus(String cloudId) {
		StringBuilder urlStringBuilder = new StringBuilder();
		urlStringBuilder.append(GET_CLOUD_RESOURCE_STATUS_URL);
		urlStringBuilder.append(cloudId);
		return restTemplate.getForObject(urlStringBuilder.toString(), String.class);
	}

	/**获取云盘图片url
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 17:50:20
	 * @param cloudId
	 * @return java.lang.String
	*/
	public String getCloudImgUrl(String cloudId) {
		if (StringUtils.isBlank(cloudId)) {
			return "";
		}
		StringBuilder urlStringBuilder = new StringBuilder();
		urlStringBuilder.append(IMG_URL);
		urlStringBuilder.append(cloudId);
		return urlStringBuilder.toString();
	}

}