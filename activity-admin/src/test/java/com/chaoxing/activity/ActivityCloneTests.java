package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.util.constant.DomainConstant;
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
    private static final String CLONE_URL = DomainConstant.API + "/activity/%d/clone?uid=28186469&fid=%d";

    @Test
    public void cloneRegionActivity() throws InterruptedException {
        List<Integer> fids = Lists.newArrayList(44248,34030,21012,21169,12017,19706,118840);
        List<Integer> activityIds = Lists.newArrayList(1306368,1306367,1306366,1306365,1306363,1306362);
        int fidIndex = 2;
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
        List<Integer> fids = Lists.newArrayList(120891,92979,45490,62611,92897,108705,2241,31339,108695,108691,118899,108679,119876,119877,119878,119880,119881,119882,119883,119884,108699,108700,108697,108696,108689,108690,108692,108687,108683,108681,108677,108676,108675,108673,108672,108671,108698,46309,111689,111691,111975,108678,108693,108684,168049,176835,179219,179866,108724,108718,108721,108722,108680,108760,108737,108688,108714,108706,108707,108708,108709,108713,108717,108723,108702,108729,108730,108732,108733,108738,108670,108668,108742,108748,108750,108669,108758,108716,108686,108751,108736,108734,108701,108710,108711,108727,108728,108739,108741,108744,108745,108746,108747,108749,108752,108756,108759,180293,180294,180296,180368,108725,108726,108755,108674,108754,108757,108740,108731,108753,108694,108712,108715,108720,108735,180378,180381,180383,180386,180389,180390,180391,180394,180395,184677,185122,176911,177611,179427,73975,73976,73979,73978,73977,67986,114689,67975,133185,67976,177742,177745,67977,67980,67978,67979,67981,67982,67983,67984,67985,67987,67988,67989,67990,67992,67993,67994,67995,67996,67998,68000,68002,68003,68004,68005,180319,180320,180321,180322,180323,180324,180325,180326,180327,180328,180329,180331,180332,180333,180334,180337,180338,180339,180341,180342,180343,180354,180355,180357,180364,180372,180379,180380,180387,180401,180418,180721,180747,180772,35584,39358,66696,35944,66709,66713,66712,66711,66710,66714,66732,66717,66733,66716,66734,66715,66729,66730,66720,66731,66719,66718,66725,66699,66724,66726,66698,66723,66727,66722,66697,66728,66706,66705,66704,66703,66702,66701,66700,66708,66707,124043,124045,124046,124047,124048,133489,137752,177367,179979,179980,179999,180302,180304,180306,180307,180310,180311,180312,180313,180314,180315,180316,180377,180384,180385,180388,180392,180412,180417,180997,33970,133815,146525,147210,147497,164068,167729,176969,177530,177524,177525,177527,177528,177529,177534,177533,177532,177531,179228,180292,180295,180297,180298,180299,180301,180309,184868,187438,114943,114934,114935,114936,114937,114938,114939,114940,114941,114942,114944,114945,114946,114947,114948,114949,114950,114951,114952,115437,115475,115476,31631,180284,180285,180286,180287,180289,114933,39158,21957,99323,99329,99334,99335,99330,99337,99338,99324,99339,99336,99327,99326,99325,99328,99331,99332,33995,99333,180283,180407,98866,33272,124655,128801,137417,137412,137357,137414,137416,137415,137413,137375,137374,137373,137372,137371,137369,137368,137367,137366,137365,137364,137363,137356,137340,137362,137361,137360,137359,137358,137348,137346,137344,137343,137342,137804,137839,137349,138091,176909,180290,180291,180300,180303,180317,180419,111528,34020,68961,68963,68964,68965,68966,68975,68977,68990,68979,68981,68984,68987,68989,68988,68986,68985,68983,68982,68980,68978,68976,68974,68962,68960,68967,68971,68970,68972,68969,68968,68973,11962,118702,133398,180250,180251,180252,180253,180330,180336,180340,180345,180347,180348,180741,180751);
        List<Integer> activityIds = Lists.newArrayList(1306360,1306359,1306358,1306357,1306356,1306355);
        int fidIndex = 119;
        int activityIndex = 3;
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
