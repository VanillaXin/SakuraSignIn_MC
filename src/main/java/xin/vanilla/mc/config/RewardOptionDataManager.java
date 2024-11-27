package xin.vanilla.mc.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.enums.ERewaedRule;
import xin.vanilla.mc.rewards.Reward;
import xin.vanilla.mc.rewards.RewardList;
import xin.vanilla.mc.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class RewardOptionDataManager {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String FILE_NAME = "reward_option_data.json";

    @Getter
    @Setter
    @NonNull
    private static RewardOptionData rewardOptionData = new RewardOptionData();

    /**
     * 获取配置文件路径
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get().resolve(SakuraSignIn.MODID);
    }

    /**
     * 加载 JSON 数据
     */
    public static void loadSignInData() {
        File file = new File(RewardOptionDataManager.getConfigDirectory().toFile(), FILE_NAME);
        if (file.exists()) {
            try {
                rewardOptionData = RewardOptionDataManager.deserializeSignInData(new String(Files.readAllBytes(Paths.get(file.getPath()))));
            } catch (IOException e) {
                LOGGER.error("Error loading sign-in data: ", e);
            }
        } else {
            // 如果文件不存在，初始化默认值
            rewardOptionData = RewardOptionData.getDefault();
            RewardOptionDataManager.saveSignInData();
        }
    }

    /**
     * 保存 JSON 数据
     */
    public static void saveSignInData() {
        File dir = RewardOptionDataManager.getConfigDirectory().toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, FILE_NAME);
        try (FileWriter writer = new FileWriter(file)) {
            // 格式化输出
            Gson gson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();
            writer.write(gson.toJson(rewardOptionData.toJsonObject()));
        } catch (IOException e) {
            LOGGER.error("Error saving sign-in data: ", e);
        }
    }

    /**
     * 校验 keyName 是否有效
     *
     * @param rule    规则类型
     * @param keyName 键名
     */
    public static boolean validateKeyName(@NonNull ERewaedRule rule, @NonNull String keyName) {
        boolean result;
        switch (rule) {
            case BASE_REWARD:
                throw new IllegalArgumentException("Base reward has no key name");
            case CONTINUOUS_REWARD:
            case CYCLE_REWARD:
                result = StringUtils.toInt(keyName) > 0;
                break;
            case YEAR_REWARD: {
                int anInt = StringUtils.toInt(keyName);
                result = anInt > 0 && anInt <= 366;
            }
            break;
            case MONTH_REWARD: {
                int anInt = StringUtils.toInt(keyName);
                result = anInt > 0 && anInt <= 31;
            }
            break;
            case WEEK_REWARD: {
                int anInt = StringUtils.toInt(keyName);
                result = anInt > 0 && anInt <= 7;
            }
            break;
            case DATE_TIME_REWARD:
                result = !RewardOptionData.parseDateRange(keyName).isEmpty();
                break;
            default:
                result = false;
        }
        return result;
    }

    /**
     * 获取奖励规则
     *
     * @param rule    规则类型
     * @param keyName 规则
     */
    @NonNull
    public static RewardList getKeyName(@NonNull ERewaedRule rule, @NonNull String keyName) {
        RewardList result;
        switch (rule) {
            case BASE_REWARD:
                result = rewardOptionData.getBaseRewards();
                break;
            case CONTINUOUS_REWARD:
                result = rewardOptionData.getContinuousRewards().get(keyName);
                break;
            case CYCLE_REWARD:
                result = rewardOptionData.getCycleRewards().get(keyName);
                break;
            case YEAR_REWARD:
                result = rewardOptionData.getYearRewards().get(keyName);
                break;
            case MONTH_REWARD:
                result = rewardOptionData.getMonthRewards().get(keyName);
                break;
            case WEEK_REWARD:
                result = rewardOptionData.getWeekRewards().get(keyName);
                break;
            case DATE_TIME_REWARD:
                result = rewardOptionData.getDateTimeRewards().get(keyName);
                break;
            default:
                throw new IllegalArgumentException("Unknown rule: " + rule);
        }
        return result == null ? new RewardList() : result;
    }

    /**
     * 添加奖励规则
     *
     * @param rule       规则类型
     * @param keyName    规则
     * @param rewardList 奖励列表
     */
    public static void addKeyName(@NonNull ERewaedRule rule, @NonNull String keyName, @NonNull RewardList rewardList) {
        switch (rule) {
            case BASE_REWARD:
                rewardOptionData.setBaseRewards(rewardList);
                break;
            case CONTINUOUS_REWARD:
                rewardOptionData.addContinuousRewards(keyName, rewardList);
                break;
            case CYCLE_REWARD:
                rewardOptionData.addCycleRewards(keyName, rewardList);
                break;
            case YEAR_REWARD:
                rewardOptionData.addYearRewards(keyName, rewardList);
                break;
            case MONTH_REWARD:
                rewardOptionData.addMonthRewards(keyName, rewardList);
                break;
            case WEEK_REWARD:
                rewardOptionData.addWeekRewards(keyName, rewardList);
                break;
            case DATE_TIME_REWARD:
                rewardOptionData.addDateTimeRewards(keyName, rewardList);
                break;
            default:
                throw new IllegalArgumentException("Unknown rule: " + rule);
        }
    }

    /**
     * 修改奖励规则的 keyName
     *
     * @param rule       规则类型
     * @param oldKeyName 旧的 keyName
     * @param newKeyName 新的 keyName
     */
    public static void updateKeyName(@NonNull ERewaedRule rule, @NonNull String oldKeyName, @NonNull String newKeyName) {
        switch (rule) {
            case BASE_REWARD:
                throw new IllegalArgumentException("Base reward has no key name");
            case CONTINUOUS_REWARD: {
                RewardList remove = rewardOptionData.getContinuousRewards().remove(oldKeyName);
                rewardOptionData.addContinuousRewards(newKeyName, remove);
            }
            break;
            case CYCLE_REWARD: {
                RewardList remove = rewardOptionData.getCycleRewards().remove(oldKeyName);
                rewardOptionData.addCycleRewards(newKeyName, remove);
            }
            break;
            case YEAR_REWARD: {
                RewardList remove = rewardOptionData.getYearRewards().remove(oldKeyName);
                rewardOptionData.addYearRewards(newKeyName, remove);
            }
            break;
            case MONTH_REWARD: {
                RewardList remove = rewardOptionData.getMonthRewards().remove(oldKeyName);
                rewardOptionData.addMonthRewards(newKeyName, remove);
            }
            break;
            case WEEK_REWARD: {
                RewardList remove = rewardOptionData.getWeekRewards().remove(oldKeyName);
                rewardOptionData.addWeekRewards(newKeyName, remove);
            }
            break;
            case DATE_TIME_REWARD: {
                RewardList remove = rewardOptionData.getDateTimeRewards().remove(oldKeyName);
                rewardOptionData.addDateTimeRewards(newKeyName, remove);
            }
            break;
            default:
                throw new IllegalArgumentException("Unknown rule: " + rule);
        }
    }

    /**
     * 清空奖励规则 keyName 下的奖励列表
     *
     * @param rule    规则类型
     * @param keyName 规则
     */
    public static void clearKey(@NonNull ERewaedRule rule, @NonNull String keyName) {
        switch (rule) {
            case BASE_REWARD:
                rewardOptionData.getBaseRewards().clear();
                break;
            case CONTINUOUS_REWARD:
                rewardOptionData.getContinuousRewards().get(keyName).clear();
                break;
            case CYCLE_REWARD:
                rewardOptionData.getCycleRewards().get(keyName).clear();
                break;
            case YEAR_REWARD:
                rewardOptionData.getYearRewards().get(keyName).clear();
                break;
            case MONTH_REWARD:
                rewardOptionData.getMonthRewards().get(keyName).clear();
                break;
            case WEEK_REWARD:
                rewardOptionData.getWeekRewards().get(keyName).clear();
                break;
            case DATE_TIME_REWARD:
                rewardOptionData.getDateTimeRewards().get(keyName).clear();
                break;
            default:
                throw new IllegalArgumentException("Unknown rule: " + rule);
        }
    }

    /**
     * 删除奖励规则
     *
     * @param rule    规则类型
     * @param keyName 规则
     */
    public static void deleteKey(@NonNull ERewaedRule rule, @NonNull String keyName) {
        switch (rule) {
            case BASE_REWARD:
                throw new IllegalArgumentException("Base reward has no key name");
            case CONTINUOUS_REWARD:
                rewardOptionData.getContinuousRewards().remove(keyName);
                break;
            case CYCLE_REWARD:
                rewardOptionData.getCycleRewards().remove(keyName);
                break;
            case YEAR_REWARD:
                rewardOptionData.getYearRewards().remove(keyName);
                break;
            case MONTH_REWARD:
                rewardOptionData.getMonthRewards().remove(keyName);
                break;
            case WEEK_REWARD:
                rewardOptionData.getWeekRewards().remove(keyName);
                break;
            case DATE_TIME_REWARD:
                rewardOptionData.getDateTimeRewards().remove(keyName);
                break;
            default:
                throw new IllegalArgumentException("Unknown rule: " + rule);
        }
    }

    /**
     * 获取奖励规则下的奖励
     *
     * @param rule    规则类型
     * @param keyName 规则
     * @param index   奖励索引
     */
    @NonNull
    public static Reward getReward(ERewaedRule rule, String keyName, int index) {
        Reward result;
        try {
            switch (rule) {
                case BASE_REWARD:
                    result = rewardOptionData.getBaseRewards().get(index);
                    break;
                case CONTINUOUS_REWARD:
                    result = rewardOptionData.getContinuousRewards().get(keyName).get(index);
                    break;
                case CYCLE_REWARD:
                    result = rewardOptionData.getCycleRewards().get(keyName).get(index);
                    break;
                case YEAR_REWARD:
                    result = rewardOptionData.getYearRewards().get(keyName).get(index);
                    break;
                case MONTH_REWARD:
                    result = rewardOptionData.getMonthRewards().get(keyName).get(index);
                    break;
                case WEEK_REWARD:
                    result = rewardOptionData.getWeekRewards().get(keyName).get(index);
                    break;
                case DATE_TIME_REWARD:
                    result = rewardOptionData.getDateTimeRewards().get(keyName).get(index);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown rule: " + rule);
            }
        } catch (Exception ignored) {
            result = new Reward();
        }
        return result == null ? new Reward() : result;
    }

    /**
     * 添加奖励规则下的奖励
     *
     * @param rule    规则类型
     * @param keyName 规则
     * @param reward  奖励
     */
    public static void addReward(ERewaedRule rule, String keyName, Reward reward) {
        switch (rule) {
            case BASE_REWARD:
                rewardOptionData.getBaseRewards().add(reward);
                break;
            case CONTINUOUS_REWARD:
                rewardOptionData.getContinuousRewards().get(keyName).add(reward);
                break;
            case CYCLE_REWARD:
                rewardOptionData.getCycleRewards().get(keyName).add(reward);
                break;
            case YEAR_REWARD:
                rewardOptionData.getYearRewards().get(keyName).add(reward);
                break;
            case MONTH_REWARD:
                rewardOptionData.getMonthRewards().get(keyName).add(reward);
                break;
            case WEEK_REWARD:
                rewardOptionData.getWeekRewards().get(keyName).add(reward);
                break;
            case DATE_TIME_REWARD:
                rewardOptionData.getDateTimeRewards().get(keyName).add(reward);
                break;
            default:
                throw new IllegalArgumentException("Unknown rule: " + rule);
        }
    }

    /**
     * 删除奖励规则下的奖励
     *
     * @param rule    规则类型
     * @param keyName 规则
     * @param index   奖励索引
     */
    public static void deleteReward(ERewaedRule rule, String keyName, int index) {
        try {
            switch (rule) {
                case BASE_REWARD:
                    rewardOptionData.getBaseRewards().remove(index);
                    break;
                case CONTINUOUS_REWARD:
                    rewardOptionData.getContinuousRewards().get(keyName).remove(index);
                    break;
                case CYCLE_REWARD:
                    rewardOptionData.getCycleRewards().get(keyName).remove(index);
                    break;
                case YEAR_REWARD:
                    rewardOptionData.getYearRewards().get(keyName).remove(index);
                    break;
                case MONTH_REWARD:
                    rewardOptionData.getMonthRewards().get(keyName).remove(index);
                    break;
                case WEEK_REWARD:
                    rewardOptionData.getWeekRewards().get(keyName).remove(index);
                    break;
                case DATE_TIME_REWARD:
                    rewardOptionData.getDateTimeRewards().get(keyName).remove(index);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown rule: " + rule);
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting reward: ", e);
        }
    }

    // TODO 添加排序

    /**
     * 序列化 SignInData
     */
    public static String serializeSignInData(RewardOptionData rewardOptionData) {
        return GSON.toJson(rewardOptionData.toJsonObject());
    }

    /**
     * 反序列化 SignInData
     */
    public static RewardOptionData deserializeSignInData(String jsonString) {
        RewardOptionData result = new RewardOptionData();
        if (StringUtils.isNotNullOrEmpty(jsonString)) {
            try {
                JsonObject jsonObject = GSON.fromJson(jsonString, JsonObject.class);
                result.setBaseRewards(GSON.fromJson(jsonObject.get("baseRewards"), new TypeToken<RewardList>() {
                }.getType()));
                result.setContinuousRewards(GSON.fromJson(jsonObject.get("continuousRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
                result.setCycleRewards(GSON.fromJson(jsonObject.get("cycleRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
                result.setYearRewards(GSON.fromJson(jsonObject.get("yearRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
                result.setMonthRewards(GSON.fromJson(jsonObject.get("monthRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
                result.setWeekRewards(GSON.fromJson(jsonObject.get("weekRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
                result.setDateTimeRewards(GSON.fromJson(jsonObject.get("dateTimeRewards"), new TypeToken<LinkedHashMap<String, RewardList>>() {
                }.getType()));
            } catch (JsonSyntaxException | JsonIOException e) {
                LOGGER.error("Error loading sign-in data: ", e);
            }
        } else {
            // 如果文件不存在，初始化默认值
            result = RewardOptionData.getDefault();
            RewardOptionDataManager.saveSignInData();
        }
        return result;
    }
}
