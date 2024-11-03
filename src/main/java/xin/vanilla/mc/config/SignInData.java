package xin.vanilla.mc.config;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import xin.vanilla.mc.enums.ERewardType;
import xin.vanilla.mc.rewards.Reward;
import xin.vanilla.mc.rewards.RewardList;
import xin.vanilla.mc.rewards.impl.*;
import xin.vanilla.mc.util.DateUtils;
import xin.vanilla.mc.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
public class SignInData implements Serializable {
    /**
     * 每次签到基础奖励
     */
    @NonNull
    private RewardList baseRewards;
    /**
     * 连续签到奖励<p>
     * 天数超过 maxDay=max(cycleRewards.keySet()) 时按maxDay计算奖励<p>
     * 例
     * 1 : 苹果
     * 2 : 西瓜
     * 4 : 面包
     * 8 : 蛋糕<p>
     * 连续签到第4天时奖励为面包<p>
     * 连续签到第8,9,10,...天时奖励为蛋糕<p>
     */
    @NonNull
    private Map<String, RewardList> continuousRewards;

    /**
     * 连续签到奖励映射关系
     */
    @NonNull
    @Expose(deserialize = false)
    private Map<String, String> continuousRewardsRelation;

    /**
     * 连续签到周期奖励<p>
     * 每 maxDay=max(cycleRewards.keySet()) 一个循环周期<p>
     * 例
     * 1 : 苹果
     * 2 : 西瓜
     * 4 : 面包
     * 8 : 蛋糕<p>
     * 连续签到第1,9,1+maxDay*n天时奖励为苹果(maxDay=8天一个周期)<p>
     * 连续签到第3,11,3+maxDay*n天时奖励为西瓜(大于2天但不足下一个奖励天)
     */
    @NonNull
    private Map<String, RewardList> cycleRewards;

    /**
     * 连续签到周期奖励映射关系
     */
    @NonNull
    @Expose(deserialize = false)
    private Map<String, String> cycleRewardsRelation;

    /**
     * 年度签到奖励<p>
     * 可以设置每年第几天奖励, 正数为正数第几天, 负数为倒数第几天<p>
     * 例
     * 1 : 苹果
     * 2 : 西瓜
     * 59 : 面包
     * 60 : 蛋糕<p>
     * 每年第1天(1月1号)奖励为苹果,每年第2天(1月2号)奖励为西瓜,每年第59天(2月28号)奖励为面包,每年第60天(2月29号(闰年)或3月1号)奖励为蛋糕
     */
    @NonNull
    private Map<String, RewardList> yearRewards;
    /**
     * 月度签到奖励<p>
     * 可以设置每月第几天奖励, 正数为正数第几天, 负数为倒数第几天<p>
     * 例
     * 1 : 苹果
     * 2 : 西瓜
     * 4 : 面包
     * 8 : 蛋糕<p>
     * 每月1号奖励为苹果,2号奖励为西瓜,4号奖励为面包,8号奖励为蛋糕
     */
    @NonNull
    private Map<String, RewardList> monthRewards;
    /**
     * 周度签到奖励<p>
     * 可以设置每周几奖励, 与<strong>连续签到周期奖励</strong>类似, 但不需要<strong>连续签到</strong><p>
     * 例
     * 1 : 苹果
     * 2 : 西瓜
     * 4 : 面包
     * 7 : 蛋糕<p>
     * 每周一奖励为苹果,周二奖励为西瓜,周四奖励为面包,周日奖励为蛋糕
     */
    @NonNull
    private Map<String, RewardList> weekRewards;
    /**
     * 指定日期/日期范围签到奖励<p>
     * 日期格式支持 yyyy-MM-dd, 0000-MM-dd, yyyy-MM-00, 0000-MM-00<p>
     * yyyy~n-MM~n-dd, 0000-MM~n-dd~n, yyyy-MM~n-00, 0000-MM~n-00<p>
     * yyyy-MM-ddTHH:mm, yyyy-MM-ddTHH~n:mm~n<p>
     * 指定具体时间时, 日期与时间需要'T'分隔<p>
     * ~n表示区间, 例 2024-10-05~5 表示 2024年10月05日到10日的5天<p>
     * 0000(yyyy) 表示不限年份, 00(MM) 表示不限月份, 00(dd) 表示不限日期
     */
    @NonNull
    private Map<String, RewardList> dateTimeRewards;

    /**
     * 日期时间奖励映射关系
     */
    @NonNull
    @Expose(deserialize = false)
    private Map<String, String> dateTimeRewardsRelation;

    public SignInData() {
        this.baseRewards = new RewardList();
        this.continuousRewards = new LinkedHashMap<>();
        this.continuousRewardsRelation = new LinkedHashMap<>();
        this.cycleRewards = new LinkedHashMap<>();
        this.cycleRewardsRelation = new LinkedHashMap<>();
        this.yearRewards = new LinkedHashMap<>();
        this.monthRewards = new LinkedHashMap<>();
        this.weekRewards = new LinkedHashMap<>();
        this.dateTimeRewards = new LinkedHashMap<>();
        this.dateTimeRewardsRelation = new LinkedHashMap<>();
    }

    /**
     * 设置连续签到奖励
     */
    public void setContinuousRewards(@NonNull Map<String, RewardList> continuousRewards) {
        this.continuousRewards = new LinkedHashMap<>();
        // 只有键为有效正整数字符串时才会被添加到映射中，以确保数据的合法性
        continuousRewards.forEach((key, value) -> {
            if (StringUtils.isNotNullOrEmpty(key)) {
                try {
                    int keyInt = Integer.parseInt(key);
                    if (keyInt > 0) {
                        this.continuousRewards.put(String.valueOf(keyInt), value);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });
        // 处理映射关系
        this.continuousRewardsRelation = new LinkedHashMap<>();
        if (!continuousRewards.isEmpty()) {
            List<Integer> keyList = continuousRewards.keySet().stream().map(Integer::parseInt).sorted().collect(Collectors.toList());
            int max = keyList.stream().max(Comparator.naturalOrder()).orElse(0);
            int cur = keyList.get(0);
            for (int i = 1; i <= max; i++) {
                if (keyList.contains(i)) cur = i;
                this.continuousRewardsRelation.put(String.valueOf(i), String.valueOf(cur));
            }
        }
    }

    /**
     * 设置周期签到奖励
     */
    public void setCycleRewards(@NonNull Map<String, RewardList> cycleRewards) {
        this.cycleRewards = new LinkedHashMap<>();
        // 只有键为有效正整数字符串时才会被添加到映射中，以确保数据的合法性
        cycleRewards.forEach((key, value) -> {
            if (StringUtils.isNotNullOrEmpty(key)) {
                try {
                    int keyInt = Integer.parseInt(key);
                    if (keyInt > 0) {
                        this.cycleRewards.put(String.valueOf(keyInt), value);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });
        // 处理映射关系
        this.cycleRewardsRelation = new LinkedHashMap<>();
        if (!cycleRewards.isEmpty()) {
            List<Integer> keyList = cycleRewards.keySet().stream().map(Integer::parseInt).sorted().collect(Collectors.toList());
            int max = keyList.stream().max(Comparator.naturalOrder()).orElse(0);
            int cur = keyList.get(0);
            for (int i = 1; i <= max; i++) {
                if (keyList.contains(i)) cur = i;
                this.cycleRewardsRelation.put(String.valueOf(i), String.valueOf(cur));
            }
        }
    }

    /**
     * 设置年度签到奖励
     */
    public void setYearRewards(@NonNull Map<String, RewardList> yearRewards) {
        this.yearRewards = new LinkedHashMap<>();
        // 只有键为有效整数字符串时才会被添加到映射中，以确保数据的合法性
        yearRewards.forEach((key, value) -> {
            if (StringUtils.isNotNullOrEmpty(key)) {
                try {
                    int keyInt = Integer.parseInt(key);
                    if (keyInt > -366 && keyInt <= 366 && keyInt != 0) {
                        this.yearRewards.put(String.valueOf(keyInt), value);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });
    }

    /**
     * 设置月度签到奖励
     */
    public void setMonthRewards(@NonNull Map<String, RewardList> monthRewards) {
        this.monthRewards = new LinkedHashMap<>();
        // 只有键为有效整数字符串时才会被添加到映射中，以确保数据的合法性
        monthRewards.forEach((key, value) -> {
            if (StringUtils.isNotNullOrEmpty(key)) {
                try {
                    int keyInt = Integer.parseInt(key);
                    if (keyInt > -31 && keyInt <= 31 && keyInt != 0) {
                        this.monthRewards.put(String.valueOf(keyInt), value);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });
    }

    /**
     * 设置周度签到奖励
     */
    public void setWeekRewards(@NonNull Map<String, RewardList> weekRewards) {
        this.weekRewards = new LinkedHashMap<>();
        // 只有键为有效正整数字符串时才会被添加到映射中，以确保数据的合法性
        weekRewards.forEach((key, value) -> {
            if (StringUtils.isNotNullOrEmpty(key)) {
                try {
                    int keyInt = Integer.parseInt(key);
                    if (keyInt > 0 && keyInt <= 7) {
                        this.weekRewards.put(String.valueOf(keyInt), value);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });
    }

    /**
     * 设置日期时间奖励映射
     * <p>
     * 本方法用于接收一个日期时间与奖励列表的映射，并根据日期范围解析生成一个更详细的日期与奖励关系映射
     * 这有助于在给定特定日期时，能够快速确定该日期所属的日期范围及其对应的奖励
     */
    public void setDateTimeRewards(@NonNull Map<String, RewardList> dateTimeRewards) {
        this.dateTimeRewards = dateTimeRewards;
        this.dateTimeRewardsRelation = new LinkedHashMap<>();
        dateTimeRewards.forEach((key, rewardList) -> {
            // 解析日期范围并生成具体日期
            List<String> parsedDates = this.parseDateRange(key);
            if (parsedDates.isEmpty()) {
                dateTimeRewards.remove(key);
            } else {
                for (String date : parsedDates) {
                    this.dateTimeRewardsRelation.put(date, key);
                }
            }
        });
    }

    public static SignInData getDefault() {
        return new SignInData() {{
            setBaseRewards(new RewardList() {{
                add(new Reward() {{
                    setContent(new ItemRewardParser().serialize(new ItemStack(Items.APPLE, 1)));
                    setType(ERewardType.ITEM);
                }});
                add(new Reward() {{
                    setContent(new SignInCardRewardParser().serialize(1));
                    setType(ERewardType.SIGN_IN_CARD);
                }});
            }});
            setContinuousRewards(new LinkedHashMap<String, RewardList>() {{
                put("1", new RewardList() {{
                    add(new Reward() {{
                        setContent(new ExpPointRewardParser().serialize(5));
                        setType(ERewardType.EXP_POINT);
                    }});
                }});
                put("2", new RewardList() {{
                    add(new Reward() {{
                        setContent(new EffectRewardParser().serialize(new EffectInstance(Effects.LUCK, 300, 1)));
                        setType(ERewardType.EFFECT);
                    }});
                }});
                put("4", new RewardList() {{
                    add(new Reward() {{
                        setContent(new ItemRewardParser().serialize(new ItemStack(Items.BREAD, 1)));
                        setType(ERewardType.ITEM);
                    }});
                }});
                put("8", new RewardList() {{
                    add(new Reward() {{
                        setContent(new ItemRewardParser().serialize(new ItemStack(Items.CAKE, 1)));
                        setType(ERewardType.ITEM);
                    }});
                }});
            }});
            setCycleRewards(new LinkedHashMap<String, RewardList>() {{
                put("1", new RewardList() {{
                    add(new Reward() {{
                        setContent(new ExpLevelRewardParser().serialize(1));
                        setType(ERewardType.EXP_LEVEL);
                    }});
                }});
                put("2", new RewardList() {{
                    add(new Reward() {{
                        setContent(new ItemRewardParser().serialize(new ItemStack(Items.MELON_SLICE, 1)));
                        setType(ERewardType.ITEM);
                    }});
                }});
                put("4", new RewardList() {{
                    add(new Reward() {{
                        setContent(new ItemRewardParser().serialize(new ItemStack(Items.BREAD, 3)));
                        setType(ERewardType.ITEM);
                    }});
                }});
                put("8", new RewardList() {{
                    add(new Reward() {{
                        setContent(new ItemRewardParser().serialize(new ItemStack(Items.CAKE, 5)));
                        setType(ERewardType.ITEM);
                    }});
                }});
            }});
            setYearRewards(new LinkedHashMap<>());
            setMonthRewards(new LinkedHashMap<>());
            setWeekRewards(new LinkedHashMap<String, RewardList>() {{
                put("6", new RewardList() {{
                    add(new Reward() {{
                        setContent(new EffectRewardParser().serialize(new EffectInstance(Effects.LUCK, 6000, 1)));
                        setType(ERewardType.EFFECT);
                    }});
                }});
                put("7", new RewardList() {{
                    add(new Reward() {{
                        setContent(new EffectRewardParser().serialize(new EffectInstance(Effects.DIG_SPEED, 6000, 0)));
                        setType(ERewardType.EFFECT);
                    }});
                    add(new Reward() {{
                        setContent(new EffectRewardParser().serialize(new EffectInstance(Effects.JUMP, 6000, 0)));
                        setType(ERewardType.EFFECT);
                    }});
                    add(new Reward() {{
                        setContent(new ItemRewardParser().serialize(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1)));
                        setType(ERewardType.ITEM);
                    }});
                }});
            }});
            setDateTimeRewards(new LinkedHashMap<String, RewardList>() {{
                put("0000-10-06~1", new RewardList() {{
                    add(new Reward() {{
                        setContent(new ItemRewardParser().serialize(new ItemStack(Items.EXPERIENCE_BOTTLE, 1)));
                        setType(ERewardType.ITEM);
                    }});
                    add(new Reward() {{
                        setContent(new EffectRewardParser().serialize(new EffectInstance(Effects.DAMAGE_RESISTANCE, 1, 300)));
                        setType(ERewardType.EFFECT);
                    }});
                }});
            }});
        }};
    }

    /**
     * 解析日期范围
     */
    private List<String> parseDateRange(String dateRange) {
        List<String> result = new ArrayList<>();

        // 处理具体日期 yyyy-MM-dd 和 yyyy-MM-ddTHH:mm:ss
        Pattern fixedDatePattern = Pattern.compile(
                "(\\d{4}|0000)-(\\d{2}|00)-(\\d{2}|00)(?:T(\\d{2}):(\\d{2}):(\\d{2}))?"
        );
        Matcher matcher = fixedDatePattern.matcher(dateRange);

        if (matcher.matches()) {
            String yearString = matcher.group(1);
            String monthString = matcher.group(2);
            String dayString = matcher.group(3);
            String hourString = matcher.group(4);
            String minuteString = matcher.group(5);
            String secondString = matcher.group(6);

            // 对于"0000"年、"00"月和"00"日的情况，不做处理，按原样返回
            if ("0000".equals(yearString) || "00".equals(monthString) || "00".equals(dayString)) {
                result.add(dateRange);
                return result;
            }

            // 解析日期
            Date parsedDate = DateUtils.getDate(yearString, monthString, dayString, hourString, minuteString, secondString);
            if (StringUtils.isNotNullOrEmpty(hourString) && StringUtils.isNotNullOrEmpty(minuteString) && StringUtils.isNotNullOrEmpty(secondString)) {
                result.add(DateUtils.toDateTimeString(parsedDate));
            } else {
                result.add(DateUtils.toString(parsedDate));
            }
            return result;
        } else {
            // 处理 yyyy-MM-dd~n 或 yyyy-MM~n-dd~n 这种格式
            Pattern rangePattern = Pattern.compile(
                    "(\\d{4})(?:~(\\d+))?-(\\d{2})(?:~(\\d+))?-(\\d{2})(?:~(\\d+))?"
            );
            matcher = rangePattern.matcher(dateRange);

            if (matcher.matches()) {
                // 提取年份、月份、日期及其范围部分
                String startYear = matcher.group(1);
                String yearRange = matcher.group(2);  // 可能为空
                String startMonth = matcher.group(3);
                String monthRange = matcher.group(4); // 可能为空
                String startDay = matcher.group(5);
                String dayRange = matcher.group(6);   // 可能为空

                // 如果没有年份、月份或日期的范围，默认赋值为0
                int yearDiff = StringUtils.isNullOrEmpty(yearRange) ? 0 : Integer.parseInt(yearRange);
                int monthDiff = StringUtils.isNullOrEmpty(monthRange) ? 0 : Integer.parseInt(monthRange);
                int dayDiff = StringUtils.isNullOrEmpty(dayRange) ? 0 : Integer.parseInt(dayRange);

                Date startDate = DateUtils.getDate("0000".equals(startYear) ? "2020" : startYear, "00".equals(startMonth) ? "01" : startMonth, "00".equals(startDay) ? "01" : startDay);
                for (int i = 0; i <= yearDiff && i < 10; i++) {
                    for (int i1 = 0; i1 <= monthDiff && i1 < 12; i1++) {
                        for (int i2 = 0; i2 <= dayDiff && i2 < 31; i2++) {
                            startDate = DateUtils.addYear(startDate, i);
                            startDate = DateUtils.addMonth(startDate, i1);
                            startDate = DateUtils.addDay(startDate, i2);
                            String year = "0000".equals(startYear) ? startYear : String.valueOf(DateUtils.getYearPart(startDate));
                            String month = "00".equals(startMonth) ? startMonth : String.valueOf(DateUtils.getMonthOfDate(startDate));
                            String day = "00".equals(startDay) ? startDay : String.valueOf(DateUtils.getDayOfMonth(startDate));
                            result.add(year + "-" + month + "-" + day);
                        }
                    }
                }
                return result;
            }
        }
        return result;
    }

    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.add("baseRewards", baseRewards.toJsonArray());

        JsonObject continuousRewardsJson = new JsonObject();
        for (Map.Entry<String, RewardList> entry : continuousRewards.entrySet()) {
            continuousRewardsJson.add(entry.getKey(), entry.getValue().toJsonArray());
        }
        json.add("continuousRewards", continuousRewardsJson);

        JsonObject continuousRewardsRelationJson = new JsonObject();
        for (Map.Entry<String, String> entry : continuousRewardsRelation.entrySet()) {
            continuousRewardsRelationJson.addProperty(entry.getKey(), entry.getValue());
        }
        json.add("continuousRewardsRelation", continuousRewardsRelationJson);

        JsonObject cycleRewardsJson = new JsonObject();
        for (Map.Entry<String, RewardList> entry : cycleRewards.entrySet()) {
            cycleRewardsJson.add(entry.getKey(), entry.getValue().toJsonArray());
        }
        json.add("cycleRewards", cycleRewardsJson);

        JsonObject cycleRewardsRelationJson = new JsonObject();
        for (Map.Entry<String, String> entry : cycleRewardsRelation.entrySet()) {
            cycleRewardsRelationJson.addProperty(entry.getKey(), entry.getValue());
        }
        json.add("cycleRewardsRelation", cycleRewardsRelationJson);

        JsonObject yearRewardsJson = new JsonObject();
        for (Map.Entry<String, RewardList> entry : yearRewards.entrySet()) {
            yearRewardsJson.add(entry.getKey(), entry.getValue().toJsonArray());
        }
        json.add("yearRewards", yearRewardsJson);

        JsonObject monthRewardsJson = new JsonObject();
        for (Map.Entry<String, RewardList> entry : monthRewards.entrySet()) {
            monthRewardsJson.add(entry.getKey(), entry.getValue().toJsonArray());
        }
        json.add("monthRewards", monthRewardsJson);

        JsonObject weekRewardsJson = new JsonObject();
        for (Map.Entry<String, RewardList> entry : weekRewards.entrySet()) {
            weekRewardsJson.add(entry.getKey(), entry.getValue().toJsonArray());
        }
        json.add("weekRewards", weekRewardsJson);

        JsonObject dateTimeRewardsJson = new JsonObject();
        for (Map.Entry<String, RewardList> entry : dateTimeRewards.entrySet()) {
            dateTimeRewardsJson.add(entry.getKey(), entry.getValue().toJsonArray());
        }
        json.add("dateTimeRewards", dateTimeRewardsJson);

        JsonObject dateTimeRewardsRelationJson = new JsonObject();
        for (Map.Entry<String, String> entry : dateTimeRewardsRelation.entrySet()) {
            dateTimeRewardsRelationJson.addProperty(entry.getKey(), entry.getValue());
        }
        json.add("dateTimeRewardsRelation", dateTimeRewardsRelationJson);
        return json;
    }
}
