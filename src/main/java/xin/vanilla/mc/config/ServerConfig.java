package xin.vanilla.mc.config;

import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final ForgeConfigSpec SERVER_CONFIG;
    public static final ForgeConfigSpec.BooleanValue AUTO_SIGN_IN;
    public static final ForgeConfigSpec.EnumValue<E_TIME_COOLING_METHOD> TIME_COOLING_METHOD;
    public static final ForgeConfigSpec.DoubleValue TIME_COOLING_VALUE;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        // 定义服务器配置项
        SERVER_BUILDER.comment("Server Settings", "服务器设置").push("server");

        // 自动签到
        AUTO_SIGN_IN = SERVER_BUILDER
                .comment("Players automatically sign in when they enter the server.", "是否允许玩家在进入服务器时自动签到。")
                .define("autoSignIn", false);

        // 签到时间冷却方式
        TIME_COOLING_METHOD = SERVER_BUILDER
                .comment("Sign in time cooling method. FIXED_TIME: Fixed time point, FIXED_INTERVAL: Fixed time interval."
                        , "签到时间冷却方式。 FIXED_TIME: 固定时间， FIXED_INTERVAL: 固定时间间隔。")
                .defineEnum("timeCoolingMethod", E_TIME_COOLING_METHOD.FIXED_TIME, E_TIME_COOLING_METHOD.values());

        // 签到时间冷却值
        TIME_COOLING_VALUE = SERVER_BUILDER
                .comment("Sign in time cooldown time, expressed as a decimal, with the integer part in hours and the decimal part in minutes, ranging from 0.00 to 23.59."
                        , "If timeCoolingMethod=FIXED_TIME(default), it means that the sign-in cooldown is refreshed at 4.00(default) every day;"
                        , "If timeCoolingMethod=FIXED_INTERVAL, it means that the player refreshed the sign-in cooldown 4 hours and 00 minutes(default) after the last sign-in."
                        , "签到时间冷却时间，以小数表示时间，整数部分为小时，小数部分为分钟，值范围0.00~23.59。"
                        , "若timeCoolingMethod=FIXED_TIME(默认)，则表示每天4.00(默认)刷新签到冷却；若timeCoolingMethod=FIXED_INTERVAL，则表示玩家在上次签到4小时00分钟(默认)后刷新签到冷却。")
                .defineInRange("timeCoolingValue", 4.00, 0.00, 23.59);

        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    @Getter
    public enum E_TIME_COOLING_METHOD {
        FIXED_TIME("固定时间"),
        FIXED_INTERVAL("固定间隔");

        private final String name;

        E_TIME_COOLING_METHOD(String name) {
            this.name = name;
        }
    }
}
