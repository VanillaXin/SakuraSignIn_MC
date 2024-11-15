package xin.vanilla.mc.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.enums.ETimeCoolingMethod;
import xin.vanilla.mc.util.DateUtils;

import java.util.Date;

/**
 * 服务器配置
 */
@Mod.EventBusSubscriber(modid = SakuraSignIn.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder().comment("Server Settings", "服务器设置").push("server");

    /**
     * 自动签到
     */
    private static final ForgeConfigSpec.BooleanValue AUTO_SIGN_IN = BUILDER
            .comment("Players automatically sign in when they enter the server."
                    , "是否允许玩家在进入服务器时自动签到。")
            .define("autoSignIn", false);

    /**
     * 签到时间冷却方式
     */
    private static final ForgeConfigSpec.EnumValue<ETimeCoolingMethod> TIME_COOLING_METHOD = BUILDER
            .comment("Sign in time cooling method. FIXED_TIME: Fixed time point, FIXED_INTERVAL: Fixed time interval."
                    , "签到时间冷却方式。 FIXED_TIME: 固定时间， FIXED_INTERVAL: 固定时间间隔， MIXED: 混合模式。")
            .defineEnum("timeCoolingMethod", ETimeCoolingMethod.FIXED_TIME, ETimeCoolingMethod.values());

    /**
     * 签到冷却刷新时间
     */
    private static final ForgeConfigSpec.DoubleValue TIME_COOLING_TIME = BUILDER
            .comment("Sign in time cooldown time, expressed as a decimal, with the integer part in hours and the decimal part in minutes."
                    , "If timeCoolingMethod=FIXED_TIME(default), it means that the sign-in cooldown is refreshed at 4.00(default) every day."
                    , "If timeCoolingMethod=MIXED, it means that the sign-in cooldown is refreshed at 4.00 (default) every day, and it will take 12 hours and 34 minutes (default) since the last sign-in before it can be signed in again."
                    , "签到时间冷却时间，以小数表示时间，整数部分为小时，小数部分为分钟。"
                    , "若timeCoolingMethod=FIXED_TIME(默认)，则表示每天4.00(默认)刷新签到冷却；"
                    , "若timeCoolingMethod=MIXED，则表示每天4.00(默认)刷新签到冷却，并且需要距离上次签到12小时34分钟(默认)后才能再次签到。")
            .defineInRange("timeCoolingTime", 4.00, -23.59, 23.59);

    /**
     * 签到冷却刷新间隔
     */
    private static final ForgeConfigSpec.DoubleValue TIME_COOLING_INTERVAL = BUILDER
            .comment("Sign in time cooldown time, expressed as a decimal, with the integer part in hours and the decimal part in minutes."
                    , "If timeCoolingMethod=FIXED_INTERVAL, it means that the player refreshed the sign-in cooldown 12 hours and 34 minutes(default) after the last sign-in;"
                    , "If timeCoolingMethod=MIXED, it means that the sign-in cooldown is refreshed at 4.00 (default) every day, and it will take 12 hours and 34 minutes (default) since the last sign-in before it can be signed in again."
                    , "签到时间冷却时间，以小数表示时间，整数部分为小时，小数部分为分钟。"
                    , "若timeCoolingMethod=FIXED_INTERVAL，则表示玩家在上次签到12小时34分钟(默认)后刷新签到冷却；"
                    , "若timeCoolingMethod=MIXED，则表示每天4.00(默认)刷新签到冷却，并且需要距离上次签到12小时34分钟(默认)后才能再次签到。")
            .defineInRange("timeCoolingInterval", 12.34, 0.00, 23.59);

    /**
     * 是否启用补签卡
     */
    private static final ForgeConfigSpec.BooleanValue SIGN_IN_CARD = BUILDER
            .comment("Allow players to use a Sign-in Card for missed sign-ins? (SIGN_IN_CARD not a sign in card, it's a Make-up Sign-in Card.)"
                    , "To obtain a Sign-in Card, you can add a reward of type SIGN_IN_CARD to the sign-in rewards."
                    , "是否允许玩家使用补签卡进行补签。(不是签到卡哦)"
                    , "可以在签到奖励里面添加类型为SIGN_IN_CARD的奖励来获得补签卡。")
            .define("signInCard", true);

    /**
     * 最大补签天数
     */
    private static final ForgeConfigSpec.IntValue RE_SIGN_IN_DAYS = BUILDER
            .comment("How many days can the Sign-in Card be renewed for."
                    , "补签卡最远可补签多少天以前的漏签。")
            .defineInRange("reSignInDays", 30, 1, 365);

    /**
     * 补签仅基础奖励
     */
    private static final ForgeConfigSpec.BooleanValue SIGN_IN_CARD_ONLY_BASE_REWARD = BUILDER
            .comment("Whether the player only gets the base rewards when using the Sign-in Card."
                    , "使用补签卡进行补签时是否仅获得基础奖励。")
            .define("signInCardOnlyBaseReward", false);

    /**
     * 服务器时间
     */
    private static final ForgeConfigSpec.ConfigValue<String> SERVER_TIME = BUILDER
            .comment("Calculate the server time offset by matching the original time with the actual time to calibrate the server time."
                    , "服务器原时间，与 实际时间 配合计算服务器时间偏移以校准服务器时间。")
            .define("serverTime", DateUtils.toDateTimeString(new Date()));

    /**
     * 实际时间
     */
    private static final ForgeConfigSpec.ConfigValue<String> ACTUAL_TIME = BUILDER
            .comment("Calculate the server time offset by matching the original time with the actual time to calibrate the server time."
                    , "实际时间，与 服务器原时间 配合计算服务器时间偏移以校准服务器时间。")
            .define("serverCalibrationTime", DateUtils.toDateTimeString(new Date()));

    public static final ForgeConfigSpec SERVER_CONFIG = BUILDER.build();

    /**
     * 获取是否自动签到
     */
    public static boolean isAutoSignIn() {
        return AUTO_SIGN_IN.get();
    }

    /**
     * 设置自动签到
     *
     * @param autoSignIn 是否自动签到
     */
    public static void setAutoSignIn(boolean autoSignIn) {
        AUTO_SIGN_IN.set(autoSignIn);
    }

    /**
     * 获取签到时间冷却方式
     */
    public static ETimeCoolingMethod getTimeCoolingMethod() {
        return TIME_COOLING_METHOD.get();
    }

    /**
     * 设置签到时间冷却方式
     *
     * @param timeCoolingMethod 签到时间冷却方式
     */
    public static void setTimeCoolingMethod(ETimeCoolingMethod timeCoolingMethod) {
        TIME_COOLING_METHOD.set(timeCoolingMethod);
    }

    /**
     * 获取签到冷却刷新时间
     */
    public static double getTimeCoolingTime() {
        return TIME_COOLING_TIME.get();
    }

    /**
     * 设置签到冷却刷新时间
     *
     * @param timeCoolingTime 签到冷却刷新时间
     */
    public static void setTimeCoolingTime(double timeCoolingTime) {
        TIME_COOLING_TIME.set(timeCoolingTime);
    }

    /**
     * 获取签到冷却刷新间隔
     */
    public static double getTimeCoolingInterval() {
        return TIME_COOLING_INTERVAL.get();
    }

    /**
     * 设置签到冷却刷新间隔
     *
     * @param timeCoolingInterval 签到冷却刷新间隔
     */
    public static void setTimeCoolingInterval(double timeCoolingInterval) {
        TIME_COOLING_INTERVAL.set(timeCoolingInterval);
    }

    /**
     * 获取是否启用补签卡
     */
    public static boolean isSignInCard() {
        return SIGN_IN_CARD.get();
    }

    /**
     * 设置是否启用补签卡
     *
     * @param signInCard 是否启用补签卡
     */
    public static void setSignInCard(boolean signInCard) {
        SIGN_IN_CARD.set(signInCard);
    }

    /**
     * 获取最大补签天数
     */
    public static int getReSignInDays() {
        return RE_SIGN_IN_DAYS.get();
    }

    /**
     * 设置最大补签天数
     *
     * @param reSignInDays 最大补签天数
     */
    public static void setReSignInDays(int reSignInDays) {
        RE_SIGN_IN_DAYS.set(reSignInDays);
    }

    /**
     * 获取是否补签仅基础奖励
     */
    public static boolean isBaseReward() {
        return SIGN_IN_CARD_ONLY_BASE_REWARD.get();
    }

    /**
     * 设置是否补签仅基础奖励
     *
     * @param baseReward 是否补签仅基础奖励
     */
    public static void setBaseReward(boolean baseReward) {
        SIGN_IN_CARD_ONLY_BASE_REWARD.set(baseReward);
    }

    /**
     * 获取服务器时间
     */
    public static Date getServerTime() {
        return DateUtils.format(SERVER_TIME.get());
    }

    /**
     * 设置服务器时间
     *
     * @param serverTime 服务器时间
     */
    public static void setServerTime(Date serverTime) {
        SERVER_TIME.set(DateUtils.toDateTimeString(serverTime));
    }

    /**
     * 获取实际时间
     */
    public static Date getActualTime() {
        return DateUtils.format(ACTUAL_TIME.get());
    }

    /**
     * 设置实际时间
     *
     * @param actualTime 实际时间
     */
    public static void setActualTime(Date actualTime) {
        ACTUAL_TIME.set(DateUtils.toDateTimeString(actualTime));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // 配置文件更新事件延迟较大, 不考虑使用Minecraft Development模板生成的推荐方法
        if (event.getConfig().getSpec() == SERVER_CONFIG) {
            SakuraSignIn.LOGGER.info("Loaded server config file {}", event.getConfig().getFileName());
        }
    }
}
