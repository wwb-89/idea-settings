package com.chaoxing.activity.api.controller.mh.datacenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.api.util.MhPreParamsUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.dto.manager.mh.MhMarketDataCenterDTO;
import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.dto.stat.ActivityStatSummaryDTO;
import com.chaoxing.activity.dto.stat.UserSummaryStatDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.service.activity.ActivityMhService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.classify.ClassifyQueryService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.rating.ActivityRatingQueryService;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.stat.UserStatSummaryQueryService;
import com.chaoxing.activity.service.util.MhDataBuildUtil;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.enums.MhAppIconEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/16 10:45
 * <p>
 */
@Slf4j
@RestController
@RequestMapping("mh/data-center")
public class ActivityMhDataCenterApiController {

    @Resource
    private WfwAreaApiService wfwAreaApiService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private MarketQueryService marketQueryService;
    @Resource
    private ClassifyQueryService classifyQueryService;
    @Resource
    private SignApiService signApiService;
    @Resource
    private PassportApiService passportApiService;
    @Resource
    private UserStatSummaryQueryService userStatSummaryQueryService;
    @Resource
    private ActivityStatSummaryQueryService activityStatSummaryQueryService;
    @Resource
    private ActivityRatingQueryService activityRatingQueryService;
    @Resource
    private CloudApiService cloudApiService;
    @Resource
    private ActivityMhService activityMhService;

    /** 用户活动总积分排行 */
    private static final String USER_TOTAL_INTEGRAL_RANK = "user_total_integral_rank";
    /** 活动报名数排行 */
    private static final String ACTIVITY_SIGNED_UP_NUM_RANK = "activity_signed_up_num_rank";


    /**获取机构下市场的门户数据源接口地址
    * @Description
    * @author huxiaolong
    * @Date 2021-09-16 11:40:16
    * @param data
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping()
    public RestRespDTO listMhMarketDataUrl(@RequestBody String data) {
        JSONObject params = JSON.parseObject(data);
        Integer wfwfid = params.getInteger("wfwfid");
        // 查询所有市场id
        List<Market> markets = marketQueryService.listByFid(wfwfid);
        List<MhMarketDataCenterDTO> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(markets)) {
            markets.forEach(v -> result.add(MhMarketDataCenterDTO.buildFromMarket(v)));
        }
        result.add(MhMarketDataCenterDTO.builder()
                .name("我的活动")
                .divUrl(DomainConstant.API + "/mh/data-center/my")
                .searchClassifyUrl(DomainConstant.API + "/mh/activity-market/classifies/with-extra-params")
                .build());
        return RestRespDTO.success(result);
    }

    /**查询活动市场marketId的活动数据
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-26 17:57:11
    * @param data
    * @param marketId
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("market/{marketId}")
    public RestRespDTO index(@RequestBody String data, @PathVariable Integer marketId) {
        log.info("查询活动市场marketId的活动数据的参数:{}", data);
        JSONObject params = JSON.parseObject(data);
        Integer wfwfid = params.getInteger("wfwfid");
        String sw = params.getString("sw");

        Optional.ofNullable(wfwfid).orElseThrow(() -> new BusinessException("wfwfid不能为空"));
        Integer pageNum = params.getInteger("page");
        pageNum = Optional.ofNullable(pageNum).orElse(1);
        Integer pageSize = params.getInteger("pageSize");
        pageSize = Optional.ofNullable(pageSize).orElse(12);
        Integer activityClassifyId = null;
        String classifies = params.getString("classifies");
        String date = params.getString("date");
        if (StringUtils.isNotBlank(classifies)) {
            JSONArray jsonArray = JSON.parseArray(classifies);
            if (jsonArray.size() > 0) {
                JSONObject activityClassify = jsonArray.getJSONObject(0);
                activityClassifyId = activityClassify.getInteger("id");
            }
        }
        String preParams = params.getString("preParams");
        JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
        // 状态
        String statusParams = urlParams.getString("status");
        List<Integer> statusList = MhPreParamsUtils.resolveIntegerV(statusParams);
        // flag
        String flag = urlParams.getString("flag");
        // 标签名称
        String tagNames = urlParams.getString("tags");
        List<String> tags = Optional.ofNullable(tagNames).filter(StringUtils::isNotBlank).map(v -> Lists.newArrayList(v.split(CommonConstant.DEFAULT_SEPARATOR))).orElse(Lists.newArrayList());
        ActivityQueryDTO activityQuery = ActivityQueryDTO.builder()
                .topFid(wfwfid)
                .statusList(statusList)
                .marketId(marketId)
                .flag(flag)
                .date(date)
                .sw(sw)
                .activityClassifyId(activityClassifyId)
                .tags(tags)
                .build();
        List<Integer> fids = wfwAreaApiService.listSubFid(wfwfid);
        activityQuery.setFids(fids);
        Page<Activity> page = new Page(pageNum, pageSize);
        page = activityQueryService.listParticipate(page, activityQuery);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("curPage", page.getCurrent());
        jsonObject.put("totalPages", page.getPages());
        jsonObject.put("totalRecords", page.getTotal());
        List<Activity> records = page.getRecords();
        JSONArray activityJsonArray = activityMhService.packageActivities(records, urlParams);
        jsonObject.put("results", activityJsonArray);
        return RestRespDTO.success(jsonObject);
    }

    /**查询市场下分类的列表
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-26 17:58:09
    * @param data
    * @param marketId
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("market/{marketId}/classifies")
    public RestRespDTO listClassify(@RequestBody String data, @PathVariable Integer marketId) {
        List<Classify> classifies;
        if (marketId != null) {
            classifies = classifyQueryService.listMarketClassifies(marketId);
        } else {
            JSONObject params = JSON.parseObject(data);
            Integer wfwfid = params.getInteger("wfwfid");
            List<Integer> wfwfids = Lists.newArrayList();
            wfwfids.add(wfwfid);
            classifies = classifyQueryService.listByFids(wfwfids);
        }

        JSONObject jsonObject = new JSONObject();
        JSONArray activityClassifyJsonArray = new JSONArray();
        jsonObject.put("classifies", activityClassifyJsonArray);
        if (CollectionUtils.isNotEmpty(classifies)) {
            for (Classify classify : classifies) {
                JSONObject item = new JSONObject();
                item.put("id", classify.getId());
                item.put("typeId", classify.getId());
                item.put("name", classify.getName());
                activityClassifyJsonArray.add(item);
            }
        }
        return RestRespDTO.success(jsonObject);
    }

    /**我的活动(目前只查询报名的活动数据)列表
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-26 17:58:31
    * @param data
    * @return com.chaoxing.activity.dto.RestRespDTO
    */
    @RequestMapping("my")
    public RestRespDTO listMyActivities(@RequestBody String data) {
        JSONObject params = JSON.parseObject(data);
        Integer wfwfid = params.getInteger("wfwfid");
        Integer uid = params.getInteger("uid");
        String sw = params.getString("sw");
        Optional.ofNullable(wfwfid).orElseThrow(() -> new BusinessException("wfwfid不能为空"));
        Integer pageNum = params.getInteger("page");
        pageNum = Optional.ofNullable(pageNum).orElse(1);
        Integer pageSize = params.getInteger("pageSize");
        pageSize = Optional.ofNullable(pageSize).orElse(12);
        String preParams = params.getString("preParams");
        JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
        // flag
        String flag = urlParams.getString("flag");
        // classifyId
        Integer activityClassifyId = null;
        String classifies = params.getString("classifies");
        if (StringUtils.isNotBlank(classifies)) {
            JSONArray jsonArray = JSON.parseArray(classifies);
            if (jsonArray.size() > 0) {
                JSONObject activityClassify = jsonArray.getJSONObject(0);
                activityClassifyId = activityClassify.getInteger("id");
            }
        }
        Integer specificCurrOrg = urlParams.getInteger("specificCurrOrg");
        String marketIdStr = urlParams.getString("marketId");
        List<Integer> marketIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(marketIdStr)) {
            marketIds = Arrays.stream(marketIdStr.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        }
        Page<Activity> page = new Page(pageNum, pageSize);
        if (uid != null) {
            String orgName = passportApiService.getOrgName(wfwfid);
            String username = passportApiService.getUserRealName(uid);
            LoginUserDTO loginUser = LoginUserDTO.buildDefault(uid, username, wfwfid, orgName);
            page = activityQueryService.mhPageSignedUp(page, loginUser, sw, flag, activityClassifyId, marketIds, specificCurrOrg);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("curPage", page.getCurrent());
        jsonObject.put("totalPages", page.getPages());
        jsonObject.put("totalRecords", page.getTotal());
        List<Activity> records = page.getRecords();
        JSONArray activityJsonArray = activityMhService.packageActivities(records, urlParams);
        jsonObject.put("results", activityJsonArray);
        return RestRespDTO.success(jsonObject);
    }

    /**查询市场下或wfw机构下积分排行榜
     *
     * wfwfid必传参数，如果marketId为空且flag不为空，则根据flag查询对应的marketId
     * 若marketId依旧不存在，查询wfwfid下的用户活动计分排行榜
     * @Description
     * @author huxiaolong
     * @Date 2021-10-22 14:20:58
     * @param data
     * @return com.chaoxing.activity.dto.RestRespDTO
     */
    @RequestMapping("user/integral/ranking-list")
    public RestRespDTO integralRankingList(@RequestBody String data) {
        Page<UserSummaryStatDTO> page = rankingList(data, USER_TOTAL_INTEGRAL_RANK);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("curPage", page.getCurrent());
        jsonObject.put("totalPages", page.getPages());
        jsonObject.put("totalRecords", page.getTotal());
        List<UserSummaryStatDTO> records = page.getRecords();
        jsonObject.put("results", packageUserStatSummary(records));
        return RestRespDTO.success(jsonObject);
    }

    @RequestMapping("user/integral/ranking-list/v2")
    public RestRespDTO integralRankingListV2(@RequestBody String data) {
        Page<UserSummaryStatDTO> page = rankingList(data, USER_TOTAL_INTEGRAL_RANK);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("curPage", page.getCurrent());
        jsonObject.put("totalPages", page.getPages());
        jsonObject.put("totalRecords", page.getTotal());
        List<UserSummaryStatDTO> records = page.getRecords();
        jsonObject.put("results", packageUserStatSummaryV2(records));
        return RestRespDTO.success(jsonObject);
    }


    /**查询市场下或wfw机构下活动报名人数的排行榜
     * @Description
     * @author huxiaolong
     * @Date 2021-12-08 15:34:06
     * @param data
     * @return
     */
    @RequestMapping("activity/signed-up-num/ranking-list")
    public RestRespDTO activitySignedUpNumRankingList(@RequestBody String data) {
        Page<ActivityStatSummaryDTO> page = rankingList(data, ACTIVITY_SIGNED_UP_NUM_RANK);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("curPage", page.getCurrent());
        jsonObject.put("totalPages", page.getPages());
        jsonObject.put("totalRecords", page.getTotal());
        List<ActivityStatSummaryDTO> records = page.getRecords();
        jsonObject.put("results", packageActivityStatSummary(records));
        return RestRespDTO.success(jsonObject);
    }

    /**分页查询排行榜数据
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-09 14:12:31
     * @param data
     * @param rankListType 排行榜类型
     * @return
     */
    private Page rankingList(String data, String rankListType) {
        JSONObject params = JSON.parseObject(data);
        Integer wfwfid = params.getInteger("wfwfid");
        Integer pageNum = params.getInteger("page");
        pageNum = Optional.ofNullable(pageNum).orElse(1);
        Integer pageSize = params.getInteger("pageSize");
        pageSize = Optional.ofNullable(pageSize).orElse(10);
        Page page = new Page(pageNum, pageSize);
        String preParams = params.getString("preParams");
        JSONObject urlParams = MhPreParamsUtils.resolve(preParams);
        Integer marketId = urlParams.getInteger("marketId");
        String flag = urlParams.getString("flag");
        if (marketId == null && StringUtils.isNotBlank(flag)) {
            Optional.ofNullable(wfwfid).orElseThrow(() -> new BusinessException("wfwfid不能为空"));
            // 若flag不为空且市场id不存在，则查询结果为空
            marketId = marketQueryService.getMarketIdByFlag(wfwfid, flag);
        }
        if (Objects.equals(rankListType, USER_TOTAL_INTEGRAL_RANK)) {
            return userStatSummaryQueryService.pageUserSummaryStat(page, marketId, wfwfid);
        } else if (Objects.equals(rankListType, ACTIVITY_SIGNED_UP_NUM_RANK)) {
            return activityStatSummaryQueryService.activitySignedUpRankPage(page, marketId, wfwfid);
        }
        return page;
    }

    /**活动市场下活动数据统计
     *
     * 活动数统计
     * 报名数统计
     * 签到数统计
     * 评论数统计
     * @Description
     * @author huxiaolong
     * @Date 2021-12-07 16:44:17
     * @param data
     * @return
     */
    @RequestMapping("market/{marketId}/stat")
    public RestRespDTO marketActivityDataStat(@RequestBody String data, @PathVariable Integer marketId) {
        JSONObject params = JSON.parseObject(data);
        Integer wfwfid = params.getInteger("wfwfid");
        Optional.ofNullable(wfwfid).orElseThrow(() -> new BusinessException("wfwfid不能为空"));
        List<Activity> activities = activityQueryService.listActivityIdsByMarketIdOrFid(marketId, wfwfid);
        // 活动数统计
        String activityNum = Optional.of(activities.size()).map(String::valueOf).orElse("0");
        // 评论数统计
        Integer countRatingNum = activityRatingQueryService.countActivityRatingNum(marketId, wfwfid);
        String ratingNum = Optional.ofNullable(countRatingNum).map(String::valueOf).orElse("0");
        List<Integer> signIds = activities.stream().map(Activity::getSignId).filter(Objects::nonNull).collect(Collectors.toList());
        // 签到统计
        String signedInNum = Optional.ofNullable(signApiService.statSignedInNum(signIds)).map(String::valueOf).orElse("");
        // 报名统计
        String signedUpNum = Optional.ofNullable(signApiService.statSignedUpNum(signIds)).map(String::valueOf).orElse("");

        List<MhGeneralAppResultDataDTO> mainFields = Lists.newArrayList();
        JSONObject jsonObject = new JSONObject();
        MhDataBuildUtil.buildFieldWithUnit(cloudApiService.buildImageUrl(MhAppIconEnum.FOUR.TOTAL_ACTIVITY_NUM.getValue()), "活动数", "个", activityNum , mainFields);
        MhDataBuildUtil.buildFieldWithUnit(cloudApiService.buildImageUrl(MhAppIconEnum.FOUR.TOTAL_RATING_NUM.getValue()), "评论数", "条", ratingNum, mainFields);
        MhDataBuildUtil.buildFieldWithUnit(cloudApiService.buildImageUrl(MhAppIconEnum.FOUR.TOTAL_SIGNED_IN_NUM.getValue()), "签到数", "个", signedInNum, mainFields);
        MhDataBuildUtil.buildFieldWithUnit(cloudApiService.buildImageUrl(MhAppIconEnum.FOUR.TOTAL_SIGNED_UP_NUM.getValue()), "报名数", "次", signedUpNum, mainFields);
        jsonObject.put("results", mainFields);
        return RestRespDTO.success(jsonObject);
    }

    private JSONArray packageUserStatSummary(List<UserSummaryStatDTO> records) {
        JSONArray jsonArray = new JSONArray();
        if (CollectionUtils.isEmpty(records)) {
            return jsonArray;
        }
        for (UserSummaryStatDTO item : records) {
            // 活动
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", item.getUid());
            jsonObject.put("type", 3);
            jsonObject.put("orsUrl", "");
            JSONArray fields = new JSONArray();
            jsonObject.put("fields", fields);
            int fieldFlag = 0;
            fields.add(MhDataBuildUtil.buildField("姓名", item.getRealName(), fieldFlag));
            fields.add(MhDataBuildUtil.buildField("积分", item.getIntegralSum(), fieldFlag));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    private List<MhGeneralAppResultDataDTO> packageUserStatSummaryV2(List<UserSummaryStatDTO> records) {
        List<MhGeneralAppResultDataDTO> mainFields = Lists.newArrayList();
        records.forEach(v -> {
            MhGeneralAppResultDataDTO item = MhGeneralAppResultDataDTO.buildDefault();
            List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
            int flag = 0;
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("头像")
                    .value(cloudApiService.buildUserAvatar(v.getUid()))
                    .type("3")
                    .flag(String.valueOf(flag))
                    .build());
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("用户名")
                    .value(v.getRealName())
                    .type("3")
                    .flag(String.valueOf(++flag))
                    .build());
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("积分")
                    .value(Optional.ofNullable(v.getIntegralSum()).map(String::valueOf).orElse("0") + "分")
                    .type("3")
                    .flag(String.valueOf(++flag))
                    .build());
            item.setFields(fields);
            mainFields.add(item);
        });
        return mainFields;
    }

    private List<MhGeneralAppResultDataDTO> packageActivityStatSummary(List<ActivityStatSummaryDTO> records) {
        List<MhGeneralAppResultDataDTO> mainFields = Lists.newArrayList();
        records.forEach(v -> {
            MhGeneralAppResultDataDTO item = MhGeneralAppResultDataDTO.buildDefault();
            List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
            int flag = 0;
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("封面")
                    .value(v.getCoverUrl())
                    .type("3")
                    .flag(String.valueOf(flag))
                    .build());
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("活动名称")
                    .value(v.getActivityName())
                    .type("3")
                    .flag(String.valueOf(++flag))
                    .build());
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("报名数")
                    .value(Optional.ofNullable(v.getSignedUpNum()).map(String::valueOf).orElse("0") + "人报名")
                    .type("3")
                    .flag(String.valueOf(++flag))
                    .build());
            item.setFields(fields);
            mainFields.add(item);
        });
        return mainFields;
    }


}
