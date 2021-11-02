package com.chaoxing.activity.aspect;

import com.chaoxing.activity.util.enums.ActivityApiSignEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/10/27 11:08
 * <p>
 */
@Slf4j
@Component
@Aspect
public class SignatureValidAspect {

    @Pointcut("@annotation(com.chaoxing.activity.annotation.SignatureValid)" )
    public void signatureInterceptor(){

    }

    @Before("signatureInterceptor()")
    public void doBefore(JoinPoint joinPoint) throws Exception {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        Map<String, String[]> parameterMap = request.getParameterMap();
        if (MapUtils.isEmpty(parameterMap)) {
            return;
        }
        // 拿到参数中的sign，查找枚举中的key，对参数进行加密，生成enc
        String sign = null, reqEnc = null;
        String[] signArr = parameterMap.get("sign");
        String[] encArr = parameterMap.get("enc");
        if (signArr != null) {
            sign = Optional.ofNullable(signArr[0]).orElse(null);
            if (StringUtils.isBlank(sign)) {
                throw new BusinessException("参数sign不能为空");
            }
        }
        if (encArr != null) {
            reqEnc = Optional.ofNullable(encArr[0]).orElse(null);
            if (StringUtils.isNotBlank(reqEnc)) {
                throw new BusinessException("参数enc不能为空");
            }
        }
        ActivityApiSignEnum signEnum = ActivityApiSignEnum.fromSign(sign);
        // 对比enc，enc不匹配，则抛出异常
        String realEnc = generateParamEnc(parameterMap, signEnum.getKey());
        if (!Objects.equals(reqEnc, realEnc)) {
            throw new BusinessException("参数签名不一致");
        }
    }


    /**生成enc
     * @Description
     * @author huxiaolong
     * @Date 2021-10-27 14:38:23
     * @param map
     * @param key
     * @return java.lang.String
     */
    private String generateParamEnc(Map<String, String[]> map, String key) throws Exception {
        SortedMap<String, String> paramMap = Maps.newTreeMap();
        for (Map.Entry<String, String[]> e : map.entrySet()) {
            if (Objects.equals(e.getKey(), "enc")) {
                continue;
            }

            String[] value = e.getValue();
            if (value != null && value.length == 1) {
                paramMap.put(e.getKey(), value[0]);
            } else {
                paramMap.put(e.getKey(), Arrays.toString(value));
            }
        }
        return generateEnc(paramMap, key);
    }

    private String generateEnc(Map<String, String> encParamMap, String key) {
        StringBuilder enc = new StringBuilder();
        for (Map.Entry<String, String> entry : encParamMap.entrySet()) {
            enc.append("[").append(entry.getKey()).append("=")
                    .append(entry.getValue()).append("]");
        }
        return DigestUtils.md5Hex(enc + "[" + key + "]");
    }
}
