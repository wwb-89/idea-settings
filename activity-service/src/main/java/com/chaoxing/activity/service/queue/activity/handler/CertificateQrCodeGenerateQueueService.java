package com.chaoxing.activity.service.queue.activity.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.model.CertificateIssue;
import com.chaoxing.activity.service.certificate.CertificateHandleService;
import com.chaoxing.activity.service.certificate.CertificateQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.queue.activity.CertificateQrCodeGenerateQueue;
import com.chaoxing.activity.util.QrCodeUtils;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**证书二维码生成队列服务
 * @author wwb
 * @version ver 1.0
 * @className CertificateQrCodeGenerateQueueService
 * @description
 * @blame wwb
 * @date 2022-03-14 10:29:25
 */
@Slf4j
@Service
public class CertificateQrCodeGenerateQueueService {

	@Value("${certificate.qr_code.path}")
	private String qrCodePath;

	@Resource
	private CertificateQueryService certificateQueryService;
	@Resource
	private CertificateHandleService certificateHandleService;
	@Resource
	private CloudApiService cloudApiService;

	public void handle(CertificateQrCodeGenerateQueue.QueueParamDTO queueParam, String path) throws IOException, WriterException {
		Integer certificateId = queueParam.getCertificateId();
		CertificateIssue certificateIssue = certificateQueryService.getById(certificateId);
		if (certificateIssue == null) {
			return;
		}
		try {
			String code = String.format(UrlConstant.CERTIFICATE_ANTI_FAKE_URL, certificateIssue.getNo());
			String savePath = path + CommonConstant.DEFAULT_FILE_SEPARATOR + qrCodePath + CommonConstant.DEFAULT_FILE_SEPARATOR + certificateIssue.getNo() + CommonConstant.QR_CODE_SUFFIX;
			QrCodeUtils.generate(code, savePath);
			// 将文件上传到云盘
			File file = new File(savePath);
			String result = cloudApiService.upload(file, "10.0.23.235");
			file.delete();
			JSONObject jsonObject = JSON.parseObject(result);
			String status = jsonObject.getString("status");
			if (Objects.equals(status, "success")) {
				certificateHandleService.certificateQrCodeSuccess(certificateId, jsonObject.getString("objectid"));
			} else {
				throw new BusinessException("上传到云盘失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			queueParam.error();
			if (!queueParam.canRepeatExec()) {
				certificateHandleService.certificateQrCodeFail(certificateId);
			}
			throw new BusinessException(e.getMessage());
		}

	}

}