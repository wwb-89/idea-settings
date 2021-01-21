package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
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
	private static final String UPLOAD_URL = "http://cs.ananas.chaoxing.com/upload?uid=-1&clientip=%s&prdid=40";
	/** 资源状态url */
	private static final String GET_CLOUD_RESOURCE_STATUS_URL = "http://cs.ananas.chaoxing.com/status/";
	/** 云盘图片状态url key */
	private static final String CLOUD_IMAGE_STATUS_URL_KEY = "http";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	/**上传云盘
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 20:15:52
	 * @param file
	 * @param ip
	 * @return java.lang.String
	*/
	public String upload(MultipartFile file, String ip) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setConnection("Keep-Alive");
		headers.setCacheControl("no-cache");
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
		parts.add("file", file.getResource());
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(parts, headers);
		String url = String.format(UPLOAD_URL, ip);
		ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
		return stringResponseEntity.getBody();
	}

	/**上传文件
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 20:18:34
	 * @param file
	 * @param ip
	 * @return java.lang.String
	*/
	public String upload(File file, String ip) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setConnection("Keep-Alive");
		headers.setCacheControl("no-cache");
		FileSystemResource resource = new FileSystemResource(file);
		MultiValueMap<String, FileSystemResource> data = new LinkedMultiValueMap<>();
		data.add("file", resource);
		HttpEntity<MultiValueMap<String, FileSystemResource>> httpEntity = new HttpEntity<>(data, headers);
		String url = String.format(UPLOAD_URL, ip);
		ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
		return stringResponseEntity.getBody();
	}

	/**获取云盘资源状态
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 20:08:05
	 * @param cloudId
	 * @return java.lang.String
	*/
	private String getStatus(String cloudId) {
		String urlStringBuilder = GET_CLOUD_RESOURCE_STATUS_URL + cloudId;
		return restTemplate.getForObject(urlStringBuilder, String.class);
	}

	/**获取图片url
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-20 11:03:02
	 * @param cloudId
	 * @return java.lang.String
	*/
	public String getImageUrl(String cloudId) {
		String result = getStatus(cloudId);
		JSONObject jsonObject = JSON.parseObject(result);
		if (jsonObject.containsKey(CLOUD_IMAGE_STATUS_URL_KEY)) {
			return jsonObject.getString(CLOUD_IMAGE_STATUS_URL_KEY);
		}
		return "";
	}

}