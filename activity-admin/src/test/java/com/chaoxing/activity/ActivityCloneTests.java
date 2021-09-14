package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**活动克隆
 * @author wwb
 * @version ver 1.0
 * @className ActivityCloneTests
 * @description
 * @blame wwb
 * @date 2021-09-13 17:48:55
 */
@Slf4j
@SpringBootTest
public class ActivityCloneTests {

    @Resource
    private RestTemplate restTemplate;
    private static final String CLONE_URL = "https://api.hd.chaoxing.com/activity/%d/clone?uid=28186469&fid=%d";

    @Test
    public void cloneRegionActivity() throws InterruptedException {
        List<Integer> fids = Lists.newArrayList(35584, 33272, 11962, 19706, 21957, 21012, 21169, 2241);
        List<Integer> activityIds = Lists.newArrayList(1306362, 1306363, 1306365, 1306366, 1306367, 1306368);
        int fidIndex = 0;
        int activityIndex = 0;
        int fidSize = fids.size();
        int activityIdSize = activityIds.size();
        for (int i = fidIndex; i < fidSize; i++) {
            int fid = fids.get(i);
            for (int j = activityIndex; j < activityIdSize; j++) {
                int activityId = activityIds.get(j);
                String url = String.format(CLONE_URL, activityId, fid);
                String result = restTemplate.getForObject(url, String.class);
                JSONObject jsonObject = JSON.parseObject(result);
                if (!Objects.equals(true, jsonObject.getBoolean("success"))) {
                    String message = jsonObject.getString("message");
                    log.error("根据参数:{}, {} 克隆活动error:{}", i, j, message);
                    throw new BusinessException(message);
                }
                System.out.println("机构:" + fid + "创建活动:" + activityId + "成功");
                Thread.sleep(13 * 1000);
            }
        }
    }

    @Test
    public void cloneSchoolActivity() throws InterruptedException {
        List<Integer> fids = Lists.newArrayList(36045,35996,35991,35993,36001,38101,36046,35999,35989,36003,35994,35995,35987,35982,35988,23550,35986,35983,36002,35984,35985,35981,28043,35992,34789,35972,35977,35976,35973,35978,35974,35975,35971,35980,22791,35990,35979,37991,36001,35970,35969,33337,37851,33360,33350,33355,33341,33339,33340,33342,33343,33012,33344,33356,33345,33347,33351,33353,33359,33346,33357,33348,33354,33352,33358,33338,33349,109432,28902,23426,28903,30432,28906,28916,28917,28915,23425,23427,28901,23428,28905,28904,28918,37856,31219,37852,37857,37859,37853,37855,62786,37854,37858,45314,66195,66191,66252,42102,66196,66254,66255,66256,66193,66257,66258,66200,66253,74461,66259,66260,66735,66261,66262,66962,37860,62785,126692,21955,21954,25508,25502,25504,25506,25512,21959,25510,21960,21953,35194,25514,37968,38016,33979,31628,33980,33981,21708,33982,31629,33983,37972,33984,31626,37973,33986,33987,31627,39775,33988,33989,31630,25649,38018,37967,38019,33990,33985,37970,38017,37971,38020,37964,37965,37969,37963,37962,34022,37992,37966,28952,28907,25475,25476,28953,28909,21013,28908,28910,139487,21172,28912,21171,21176,28913,21173,33997,28896,28914,21174,28899,29257,28911,28898,21170,21175,28897,37954,37929,37952,37930,37927,37953,33991,37931,37932,34652,37933,37926,37935,33992,37951,37934,37959,37939,37941,37940,37943,37942,37955,37944,37957,37958,37948,37947,37936,37961,37950,37949,37956,37937,37946,37925,37928,31786,37960,37938,33993,37945,172058,37405,118828,22577,27950,120017);
        List<Integer> activityIds = Lists.newArrayList(1306355, 1306356, 1306357, 1306358, 1306359, 1306360);
        int fidIndex = 0;
        int activityIndex = 0;
        int fidSize = fids.size();
        int activityIdSize = activityIds.size();
        for (int i = fidIndex; i < fidSize; i++) {
            int fid = fids.get(i);
            for (int j = activityIndex; j < activityIdSize; j++) {
                int activityId = activityIds.get(j);
                String url = String.format(CLONE_URL, activityId, fid);
                String result = restTemplate.getForObject(url, String.class);
                JSONObject jsonObject = JSON.parseObject(result);
                if (!Objects.equals(true, jsonObject.getBoolean("success"))) {
                    String message = jsonObject.getString("message");
                    System.out.println("根据参数:"+ i +", "+ j +" 克隆活动error:" + message);
                    log.error("根据参数:{}, {} 克隆活动error:{}", i, j, message);
                    throw new BusinessException(message);
                }
                System.out.println("机构:" + fid + "创建活动:" + activityId + "成功");
                Thread.sleep(13 * 1000);
            }
        }
    }

}
