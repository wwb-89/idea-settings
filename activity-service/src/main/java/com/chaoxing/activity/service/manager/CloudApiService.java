package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.util.Base64Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
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
import java.io.IOException;

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
	/** 下载url key */
	private static final String DOWNLOAD_URL_KEY = "download";
	/** 图片url前缀 */
	private static final String IMAGE_URL_SUFFIX = "http://p.ananas.chaoxing.com/star3/origin/";

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

	/**上传文件
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-16 18:09:49
	 * @param base64
	 * @param rootPath
	 * @param ip
	 * @return java.lang.String
	*/
	public String upload(String base64, String rootPath, String ip) throws IOException {
		String suffixFromBase64Str = Base64Utils.getSuffixFromBase64Str(base64);
		String base64Data = Base64Utils.getBase64Data(base64);
		String fileName = System.currentTimeMillis() + suffixFromBase64Str;
		File file = new File(rootPath + fileName);
		try {
			FileUtils.writeByteArrayToFile(file, Base64.decodeBase64(base64Data));
			return upload(file, ip);
		} finally {
			file.delete();
		}
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

	/**获取excel资源状态
	 * @Description
	 * @author wwb
	 * @Date 2020-11-10 20:08:05
	 * @param cloudId
	 * @return java.lang.String
	*/
	public String getFileDownloadUrl(String cloudId) {
		String result = getStatus(cloudId);
		JSONObject jsonObject = JSON.parseObject(result);
		if (jsonObject.containsKey(DOWNLOAD_URL_KEY)) {
			return jsonObject.getString(DOWNLOAD_URL_KEY);
		}
		return null;
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

	/**构建图片地址
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-16 17:18:13
	 * @param cloudId
	 * @return java.lang.String
	*/
	public String buildImageUrl(String cloudId) {
		return IMAGE_URL_SUFFIX + cloudId;
	}

}