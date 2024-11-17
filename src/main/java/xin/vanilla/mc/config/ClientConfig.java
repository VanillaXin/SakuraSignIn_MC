package xin.vanilla.mc.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ClientConfig {

    public static Configuration config;

    // 配置项
    public static String theme;
    public static boolean specialTheme;
    public static boolean showLastReward;
    public static boolean showNextReward;
    public static boolean autoRewarded;

    // 初始化配置方法
    public static void init(File configFile) {
        config = new Configuration(configFile);
        loadConfig();
    }

    // 加载配置文件
    private static void loadConfig() {
        try {
            config.load();

            // 主题
            theme = config.getString("theme", "client", "textures/gui/sign_in_calendar_sakura.png",
                    "theme textures path, can be external path: config/sakura_sign_in/themes/your_theme.png\n" +
                            "主题材质路径，可为外部路径： config/sakura_sign_in/themes/your_theme.png");

            // 内置主题特殊图标
            specialTheme = config.getBoolean("specialTheme", "client", false,
                    "Whether or not to use the built-in theme special icons.\n" +
                            "是否使用内置主题特殊图标。");

            // 显示上月奖励
            showLastReward = config.getBoolean("showLastReward", "client", false,
                    "The sign-in page displays last month's rewards. Someone said it didn't look good on display.\n" +
                            "签到页面是否显示上个月的奖励，有人说它显示出来不好看。");

            // 显示下月奖励
            showNextReward = config.getBoolean("showNextReward", "client", false,
                    "The sign-in page displays next month's rewards. Someone said it didn't look good on display.\n" +
                            "签到页面是否显示下个月的奖励，有人说它显示出来不好看。");

            // 自动领取
            autoRewarded = config.getBoolean("autoRewarded", "client", false,
                    "Whether the rewards will be automatically claimed when you sign in or re-sign-in.\n" +
                            "签到或补签时是否自动领取奖励。");

        } catch (Exception e) {
            System.out.println("Error loading client config file!");
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
