package com.chaoxing.activity.util;

import com.google.common.io.CharStreams;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @className RSAUtils
 * @description
 * @author wwb
 * @date 2019-06-24 18:54:58
 * @version ver 1.0
 */
public class RsaUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(RsaUtils.class);
	private static final int MIN_SIGN_LENGTH = 32;

	private RsaUtils() {}

	/**验签
	 * @Description 
	 * @author wwb
	 * @Date 2019-10-22 15:04:41
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @return boolean
	*/
	public static boolean verifySign(String content, String sign, String publicKey) {
		if (sign.length() < MIN_SIGN_LENGTH) {
			return false;
		}
		String vc3key = "Mq~am!PoYhu";
		String vc3Final = sign.substring(0, sign.length() - MIN_SIGN_LENGTH);
		String vc3md5 = sign.substring(sign.length() - MIN_SIGN_LENGTH);
		String checkMd5 = DigestUtils.md5Hex(vc3Final + vc3key);
		if (vc3md5.equals(checkMd5)) {
			try {
				return rsaCheck(content, vc3Final, publicKey);
			} catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
				LOGGER.error("验签失败:", e);
			}
		}
		return false;
	}

	/**
	 * Base64编码.
	 */
	public static String encodeBase64(byte[] input) {
		byte[] encode = Base64.getEncoder().encode(input);
		return new String(encode);
	}

	/**
	 * Base64解码.
	 * 如果字符不合法，抛出IllegalArgumentException
	 */
	public static byte[] decodeBase64(String input) {
		return Base64.getDecoder().decode(input);
	}

	private static boolean rsaCheck(String content, String sign, String publicKey) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException, SignatureException {
		PublicKey pubKey = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));
		Signature signature = Signature.getInstance("SHA256WithRSA");
		signature.initVerify(pubKey);
		signature.update(content.getBytes(StandardCharsets.UTF_8));
		return signature.verify(decodeBase64(sign));
	}

	private static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		StringWriter writer = new StringWriter();
		CharStreams.copy(new InputStreamReader(ins), writer);
		byte[] encodedKey = writer.toString().getBytes();
		encodedKey = decodeBase64(new String(encodedKey));
		return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
	}

}
