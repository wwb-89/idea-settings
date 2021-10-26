package com.chaoxing.activity;

import com.chaoxing.activity.service.manager.bigdata.BigDataPointApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author wwb
 * @version ver 1.0
 * @className BigDataPointApiServiceTests
 * @description
 * @blame wwb
 * @date 2021-10-12 19:52:57
 */
@SpringBootTest
public class BigDataPointApiServiceTests {

    @Resource
    private BigDataPointApiService bigDataPointApiService;

    @Test
    public void addPoint() {
        Integer uid = 25418810;
        Integer fid = 170690;
        BigDataPointApiService.PointTypeEnum pointType = BigDataPointApiService.PointTypeEnum.ORGANIZE_ACTIVITY;
        LocalDateTime time = LocalDateTime.now();
        BigDataPointApiService.PointPushParamDTO param = new BigDataPointApiService.PointPushParamDTO(uid, fid, pointType, time);
        bigDataPointApiService.addPoint(param);
    }

    @Test
    public void spendPoint() {
        Integer uid = 25418810;
        Integer fid = 170690;
        BigDataPointApiService.PointTypeEnum pointType = BigDataPointApiService.PointTypeEnum.CANCEL_ORGANIZE_ACTIVITY;
        LocalDateTime time = LocalDateTime.now();
        BigDataPointApiService.PointPushParamDTO param = new BigDataPointApiService.PointPushParamDTO(uid, fid, pointType, time);
        bigDataPointApiService.spendPoint(param);
    }

}
