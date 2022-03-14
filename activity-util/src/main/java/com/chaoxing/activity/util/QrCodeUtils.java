package com.chaoxing.activity.util;

import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**二维码工具
 * @author wwb
 * @version ver 1.0
 * @className QrCodeUtils
 * @description
 * @blame wwb
 * @date 2022-03-14 10:02:22
 */
public class QrCodeUtils {

	/**
	 * CODE_WIDTH：二维码宽度，单位像素
	 * CODE_HEIGHT：二维码高度，单位像素
	 * FRONT_COLOR：二维码前景色，0x000000 表示黑色
	 * BACKGROUND_COLOR：二维码背景色，0xFFFFFF 表示白色
	 * 演示用 16 进制表示，和前端页面 CSS 的取色是一样的，注意前后景颜色应该对比明显，如常见的黑白
	 */
	private static final int CODE_WIDTH = 300;
	private static final int CODE_HEIGHT = 300;
	private static final int FRONT_COLOR = 0x000000;
	private static final int BACKGROUND_COLOR = 0xFFFFFF;

	private QrCodeUtils() {

	}

	/**生成微微吗
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-14 10:09:18
	 * @param code 二维码的内容
	 * @param savePath 保存路径
	 * @return void
	*/
	public static void generate(String code, String savePath) throws WriterException, IOException {
		code = Optional.ofNullable(code).filter(StringUtils::isNotBlank).map(String::trim).orElseThrow(() -> new BusinessException("二维码内容不能为空"));
		Map<EncodeHintType, Object> hints = Maps.newHashMap();
		hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
		hints.put(EncodeHintType.MARGIN, 1);

		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		BitMatrix bitMatrix = multiFormatWriter.encode(code, BarcodeFormat.QR_CODE, CODE_WIDTH, CODE_HEIGHT, hints);
		BufferedImage bufferedImage = new BufferedImage(CODE_WIDTH, CODE_HEIGHT, BufferedImage.TYPE_INT_BGR);
		for (int x = 0; x < CODE_WIDTH; x++) {
			for (int y = 0; y < CODE_HEIGHT; y++) {
				bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? FRONT_COLOR : BACKGROUND_COLOR);
			}
		}
		File file = new File(savePath);
		if (file.exists()) {
			file.delete();
		} else {
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				file.mkdirs();
			}
		}
		ImageIO.write(bufferedImage, "png", file);
	}

}