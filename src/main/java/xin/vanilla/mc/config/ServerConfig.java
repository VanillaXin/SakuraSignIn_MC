package xin.vanilla.mc.config;

import net.minecraftforge.common.config.Configuration;
import xin.vanilla.mc.enums.ETimeCoolingMethod;
import xin.vanilla.mc.util.DateUtils;

import java.io.File;
import java.util.Date;

public class ServerConfig {

    public static Configuration config;

    // 配置项
    public static boolean autoSignIn;
    public static ETimeCoolingMethod timeCoolingMethod;
    public static double timeCoolingTime;
    public static double timeCoolingInterval;
    public static boolean signInCard;
    public static int reSignInDays;
    public static boolean signInCardOnlyBaseReward;
    public static String serverTime;
    public static String actualTime;

    // 配置初始化方法
    public static void init(File configFile) {
        config = new Configuration(configFile);
        loadConfig();
    }

    // 加载配置文件
    private static void loadConfig() {
        try {
            config.load();

            // 自动签到
            autoSignIn = config.getBoolean("autoSignIn", "server", false,
                    "Players automatically sign in when they enter the server. 是否允许玩家在进入服务器时自动签到。");

            // 签到时间冷却方式
            String defaultMethod = ETimeCoolingMethod.FIXED_TIME.name();
            timeCoolingMethod = ETimeCoolingMethod.valueOf(config.getString("timeCoolingMethod", "server", defaultMethod,
                    "Sign in time cooling method. FIXED_TIME: Fixed time point, FIXED_INTERVAL: Fixed time interval."));

            // 签到冷却刷新时间
            timeCoolingTime = config.getFloat("timeCoolingTime", "server", 4.00f, -23.59f, 23.59f,
                    "Sign in time cooldown time, expressed as a decimal. Integer part in hours, decimal part in minutes.");

            // 签到冷却刷新间隔
            timeCoolingInterval = config.getFloat("timeCoolingInterval", "server", 12.34f, 0.00f, 23.59f,
                    "Sign in time cooldown time, expressed as a decimal. Integer part in hours, decimal part in minutes.");

            // 补签卡
            signInCard = config.getBoolean("signInCard", "server", true,
                    "Allow players to use a Sign-in Card for missed sign-ins?");

            // 最大补签天数
            reSignInDays = config.getInt("reSignInDays", "server", 30, 1, 365,
                    "How many days can the Sign-in Card be renewed for.");

            // 补签仅基础奖励
            signInCardOnlyBaseReward = config.getBoolean("signInCardOnlyBaseReward", "server", false,
                    "Whether the player only gets the base rewards when using the Sign-in Card.");

            // 服务器时间
            serverTime = config.getString("serverTime", "server", DateUtils.toDateTimeString(new Date()),
                    "Calculate the server time offset by matching the original time with the actual time to calibrate the server time.");

            // 实际时间
            actualTime = config.getString("actualTime", "server", DateUtils.toDateTimeString(new Date()),
                    "Calculate the server time offset by matching the original time with the actual time to calibrate the server time.");

        } catch (Exception e) {
            System.out.println("Error loading config file!");
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    // 更新配置
    public static void reloadConfig() {
        if (config != null) {
            loadConfig();
            config.save();
        }
    }
}
