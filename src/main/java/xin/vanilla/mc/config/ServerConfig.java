package xin.vanilla.mc.config;

import net.minecraftforge.common.ForgeConfigSpec;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.enums.ETimeCoolingMethod;

public class ServerConfig {
    public static final String SIGN_IN_CARD_ITEM_NAME = SakuraSignIn.MODID + ":sign_in_card";

    public static final ForgeConfigSpec SERVER_CONFIG;
    /**
     * 自动签到
     */
    public static final ForgeConfigSpec.BooleanValue AUTO_SIGN_IN;
    /**
     * 签到时间冷却方式
     */
    public static final ForgeConfigSpec.EnumValue<ETimeCoolingMethod> TIME_COOLING_METHOD;
    /**
     * 签到冷却刷新时间
     */
    public static final ForgeConfigSpec.DoubleValue TIME_COOLING_TIME;
    /**
     * 签到冷却刷新间隔
     */
    public static final ForgeConfigSpec.DoubleValue TIME_COOLING_INTERVAL;
    /**
     * 是否启用补签卡
     */
    public static final ForgeConfigSpec.BooleanValue SIGN_IN_CARD;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        // 定义服务器配置项
        SERVER_BUILDER.comment("Server Settings", "服务器设置").push("server");

        // 自动签到
        AUTO_SIGN_IN = SERVER_BUILDER
                .comment("Players automatically sign in when they enter the server."
                        , "是否允许玩家在进入服务器时自动签到。")
                .define("autoSignIn", false);

        // 签到时间冷却方式
        TIME_COOLING_METHOD = SERVER_BUILDER
                .comment("Sign in time cooling method. FIXED_TIME: Fixed time point, FIXED_INTERVAL: Fixed time interval."
                        , "签到时间冷却方式。 FIXED_TIME: 固定时间， FIXED_INTERVAL: 固定时间间隔， MIXED: 混合模式。")
                .defineEnum("timeCoolingMethod", ETimeCoolingMethod.FIXED_TIME, ETimeCoolingMethod.values());

        // 签到冷却刷新时间
        TIME_COOLING_TIME = SERVER_BUILDER
                .comment("Sign in time cooldown time, expressed as a decimal, with the integer part in hours and the decimal part in minutes."
                        , "If timeCoolingMethod=FIXED_TIME(default), it means that the sign-in cooldown is refreshed at 4.00(default) every day."
                        , "If timeCoolingMethod=MIXED, it means that the sign-in cooldown is refreshed at 4.00 (default) every day, and it will take 12 hours and 34 minutes (default) since the last sign-in before it can be signed in again."
                        , "签到时间冷却时间，以小数表示时间，整数部分为小时，小数部分为分钟。"
                        , "若timeCoolingMethod=FIXED_TIME(默认)，则表示每天4.00(默认)刷新签到冷却；"
                        , "若timeCoolingMethod=MIXED，则表示每天4.00(默认)刷新签到冷却，并且需要距离上次签到12小时34分钟(默认)后才能再次签到。")
                .defineInRange("timeCoolingTime", 4.00, 0.00, 23.59);

        // 签到冷却刷新间隔
        TIME_COOLING_INTERVAL = SERVER_BUILDER
                .comment("Sign in time cooldown time, expressed as a decimal, with the integer part in hours and the decimal part in minutes."
                        , "If timeCoolingMethod=FIXED_INTERVAL, it means that the player refreshed the sign-in cooldown 12 hours and 34 minutes(default) after the last sign-in;"
                        , "If timeCoolingMethod=MIXED, it means that the sign-in cooldown is refreshed at 4.00 (default) every day, and it will take 12 hours and 34 minutes (default) since the last sign-in before it can be signed in again."
                        , "签到时间冷却时间，以小数表示时间，整数部分为小时，小数部分为分钟。"
                        , "若timeCoolingMethod=FIXED_INTERVAL，则表示玩家在上次签到12小时34分钟(默认)后刷新签到冷却；"
                        , "若timeCoolingMethod=MIXED，则表示每天4.00(默认)刷新签到冷却，并且需要距离上次签到12小时34分钟(默认)后才能再次签到。")
                .defineInRange("timeCoolingInterval", 12.34, 0.00, 23.59);

        // 补签卡(不是签到卡哦)
        SIGN_IN_CARD = SERVER_BUILDER
                .comment("Allow players to use a Make-up Sign-in Card for missed sign-ins? (SIGN_IN_CARD not a sign in card, it's a Make-up Sign-in Card.)"
                        , String.format("To obtain a Make-up Sign-in Card, you can add the item [%s] to the sign-in rewards.", SIGN_IN_CARD_ITEM_NAME)
                        , "是否允许玩家使用补签卡进行补签。(不是签到卡哦)"
                        , String.format("可以在签到奖励里面添加物品[%s]来获得补签卡。", SIGN_IN_CARD_ITEM_NAME))
                .define("signInCard", true);

        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
    }

}
