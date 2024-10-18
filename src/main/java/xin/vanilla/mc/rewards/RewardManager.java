package xin.vanilla.mc.rewards;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import xin.vanilla.mc.capability.IPlayerSignInData;
import xin.vanilla.mc.capability.SignInRecord;
import xin.vanilla.mc.config.ServerConfig;
import xin.vanilla.mc.config.SignInData;
import xin.vanilla.mc.config.SignInDataManager;
import xin.vanilla.mc.enums.ERewardType;
import xin.vanilla.mc.rewards.impl.*;
import xin.vanilla.mc.util.CollectionUtils;
import xin.vanilla.mc.util.DateUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RewardManager {
    private static final Map<ERewardType, RewardParser<?>> rewardParsers = new HashMap<>();

    // 注册不同类型的奖励解析器
    static {
        rewardParsers.put(ERewardType.ITEM, new ItemRewardParser());
        rewardParsers.put(ERewardType.EFFECT, new EffectRewardParser());
        rewardParsers.put(ERewardType.EXP_POINT, new ExpPointRewardParser());
        rewardParsers.put(ERewardType.EXP_LEVEL, new ExpLevelRewardParser());
        rewardParsers.put(ERewardType.SIGN_IN_CARD, new SignInCardRewardParser());
        rewardParsers.put(ERewardType.ADVANCEMENT, new AdvancementRewardParser());
        // MORE ...
    }

    /**
     * 反序列化奖励
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserializeReward(Reward reward) {
        RewardParser<T> parser = (RewardParser<T>) rewardParsers.get(reward.getType());
        if (parser == null) {
            throw new JSONException("Unknown reward type: " + reward.getType());
        }
        return parser.deserialize(reward.getContent());
    }

    /**
     * 序列化奖励
     */
    @SuppressWarnings("unchecked")
    public static <T> JSONObject serializeReward(T reward, ERewardType type) {
        RewardParser<T> parser = (RewardParser<T>) rewardParsers.get(type);
        if (parser == null) {
            throw new JSONException("Unknown reward type: " + type);
        }
        return parser.serialize(reward);
    }

    public static Map<Integer, RewardList> getMonthRewardList(Date currentMonth, IPlayerSignInData playerData) {
        SignInData serverData = SignInDataManager.getSignInData();
        Map<Integer, RewardList> result = new LinkedHashMap<>();

        // 签到冷却刷新时间
        int coolingHour = (int) Math.floor(ServerConfig.TIME_COOLING_TIME.get());
        int coolingMinute = (int) Math.floor((coolingHour - ServerConfig.TIME_COOLING_TIME.get()) * 100);
        // 校准后当前时间
        Date nowCompensate = DateUtils.addMinute(DateUtils.addHour(new Date(), coolingHour), coolingMinute);
        int nowCompensate8 = DateUtils.toDateInt(nowCompensate);
        long nowCompensate14 = DateUtils.toDateTimeInt(nowCompensate);
        // 选中月份的上一个月
        Date lastMonth = DateUtils.addMonth(currentMonth, -1);
        // 选中月份的下一个月
        Date nextMonth = DateUtils.addMonth(currentMonth, 1);
        // 上月的总天数
        int daysOfLastMonth = DateUtils.getDaysOfMonth(lastMonth);
        // 本月总天数
        int daysOfCurrentMonth = DateUtils.getDaysOfMonth(currentMonth);
        // 本年总天数
        int daysOfCurrentYear = DateUtils.getDaysOfYear(currentMonth);

        // 计算本月+上月最后七天+下月开始七天的奖励
        for (int i = 1; i <= daysOfCurrentMonth + 14; i++) {
            int month, day, year;
            if (i <= 7) {
                // 属于上月的日期
                year = DateUtils.getYearPart(lastMonth);
                month = DateUtils.getMonthOfDate(lastMonth);
                day = daysOfLastMonth - (7 - i);
            } else if (i <= 7 + daysOfCurrentMonth) {
                // 属于当前月的日期
                year = DateUtils.getYearPart(currentMonth);
                month = DateUtils.getMonthOfDate(currentMonth);
                day = i - 7;
            } else {
                // 属于下月的日期
                year = DateUtils.getYearPart(nextMonth);
                month = DateUtils.getMonthOfDate(nextMonth);
                day = i - daysOfCurrentMonth - 7;
            }
            int key = year * 10000 + month * 100 + day;
            Date date = DateUtils.getDate(year, month, day);
            int curDayOfYear = DateUtils.getDayOfYear(date);
            int curDayOfMonth = DateUtils.getDayOfMonth(date);
            int curDayOfWeek = DateUtils.getDayOfWeek(date);
            RewardList rewardList = new RewardList();

            // 已签到的奖励记录
            List<Reward> rewardRecords = null;
            // 如果日历日期小于当前日期, 则从签到记录中获取已签到的奖励记录
            if (key < nowCompensate8) {
                rewardRecords = playerData.getSignInRecords().stream()
                        .map(SignInRecord::clone)
                        // 若签到日期等于当前日期
                        .filter(record -> DateUtils.toDateInt(record.getCompensateTime()) == key)
                        .flatMap(record -> record.getRewardList().stream())
                        .peek(reward -> {
                            reward.setClaimed(true);
                            reward.setDisabled(true);
                        }).collect(Collectors.toList());
            }

            // 若签到记录存在，则添加签到奖励记录
            if (!CollectionUtils.isNullOrEmpty(rewardRecords)) {
                rewardList.addAll(rewardRecords);
            }
            // 若签到记录不存在，则添加基础奖励
            else {
                // 基础奖励
                rewardList.addAll(serverData.getBaseRewards());
                // 年度签到奖励(正数第几天)
                rewardList.addAll(serverData.getYearRewards().getOrDefault(String.valueOf(curDayOfYear), new RewardList()));
                // 年度签到奖励(倒数第几天)
                rewardList.addAll(serverData.getYearRewards().getOrDefault(String.valueOf(curDayOfYear - 1 - daysOfCurrentYear), new RewardList()));
                // 月度签到奖励(正数第几天)
                rewardList.addAll(serverData.getMonthRewards().getOrDefault(String.valueOf(curDayOfMonth), new RewardList()));
                // 月度签到奖励(倒数第几天)
                rewardList.addAll(serverData.getMonthRewards().getOrDefault(String.valueOf(curDayOfMonth - 1 - daysOfCurrentMonth), new RewardList()));
                // 周度签到奖励(每周固定7天, 没有倒数的说法)
                rewardList.addAll(serverData.getWeekRewards().getOrDefault(String.valueOf(curDayOfWeek), new RewardList()));
                // 自定义日期奖励
                rewardList.addAll(
                        serverData.getDateTimeRewardsRelation().keySet().stream()
                                .filter(getDateStringList(currentMonth)::contains)
                                .map(serverData.getDateTimeRewardsRelation()::get)
                                .distinct()
                                .map(serverData.getDateTimeRewards()::get)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList())
                );

                //  若日历日期>=当前日期，则添加连续签到奖励(不同玩家不一样)
                if (key >= nowCompensate8) {
                    // 连续签到天数
                    int continuousSignInDays = playerData.getContinuousSignInDays();
                    if (DateUtils.toDateInt(playerData.getLastSignInTime()) < nowCompensate8) {
                        continuousSignInDays++;
                    }
                    // 连续签到奖励
                    int continuousMax = serverData.getContinuousRewardsRelation().keySet().stream().map(Integer::parseInt).max(Comparator.naturalOrder()).orElse(0);
                    rewardList.addAll(
                            serverData.getContinuousRewards().get(
                                    serverData.getContinuousRewardsRelation().get(
                                            String.valueOf(Math.min(continuousMax, continuousSignInDays))
                                    )
                            )
                    );
                    // 连续签到周期奖励
                    int cycleMax = serverData.getCycleRewardsRelation().keySet().stream().map(Integer::parseInt).max(Comparator.naturalOrder()).orElse(0);
                    rewardList.addAll(
                            serverData.getCycleRewards().get(
                                    serverData.getCycleRewardsRelation().get(
                                            String.valueOf(continuousSignInDays % (cycleMax + 1))
                                    )
                            )
                    );
                }
            }
            result.put(key, rewardList);
        }
        return result;
    }

    public static List<String> getDateStringList(Date date) {
        List<String> result = new ArrayList<>();
        result.add(DateUtils.toDateTimeString(date));
        result.add(DateUtils.toString(date));
        result.add(DateUtils.toString(date, "'0000'-MM-dd"));
        result.add(DateUtils.toString(date, "yyyy-'00'-dd"));
        result.add(DateUtils.toString(date, "yyyy-MM-'00'"));
        result.add(DateUtils.toString(date, "'0000'-'00'-dd"));
        result.add(DateUtils.toString(date, "'0000'-MM-'00'"));
        result.add(DateUtils.toString(date, "yyyy-'00'-'00'"));
        result.add(DateUtils.toString(date, "'0000'-MM-dd HH:mm:ss"));
        result.add(DateUtils.toString(date, "yyyy-'00'-dd HH:mm:ss"));
        result.add(DateUtils.toString(date, "yyyy-MM-'00' HH:mm:ss"));
        result.add(DateUtils.toString(date, "'0000'-'00'-dd HH:mm:ss"));
        result.add(DateUtils.toString(date, "'0000'-MM-'00' HH:mm:ss"));
        result.add(DateUtils.toString(date, "yyyy-'00'-'00' HH:mm:ss"));
        return result;
    }

}
