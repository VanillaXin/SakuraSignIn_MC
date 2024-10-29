package xin.vanilla.mc.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec CLIENT_CONFIG;
    /**
     * 主题设置
     */
    public static final ForgeConfigSpec.ConfigValue<String> THEME;
    /**
     * 签到页面显示上月奖励
     */
    public static final ForgeConfigSpec.BooleanValue SHOW_LAST_REWARD;
    /**
     * 签到页面显示下月奖励
     */
    public static final ForgeConfigSpec.BooleanValue SHOW_NEXT_REWARD;
    /**
     * 自动领取
     */
    public static final ForgeConfigSpec.BooleanValue AUTO_REWARDED;

    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        // 定义客户端配置项
        CLIENT_BUILDER.comment("Client Settings").push("client");

        // 主题
        THEME = CLIENT_BUILDER
                .comment("theme textures path, can be external path: config/sakura_sign_in/themes/your_theme.png"
                        , "主题材质路径，可为外部路径： config/sakura_sign_in/themes/your_theme.png")
                .define("theme", "textures/gui/sign_in_calendar_sakura.png");

        // 签到页面显示上月奖励
        SHOW_LAST_REWARD = CLIENT_BUILDER
                .comment("The sign-in page displays last month's rewards. Someone said it didn't look good on display."
                        , "签到页面是否显示上个月的奖励，有人说它显示出来不好看。")
                .define("showLastReward", false);

        // 签到页面显示下月奖励
        SHOW_NEXT_REWARD = CLIENT_BUILDER
                .comment("The sign-in page displays next month's rewards. Someone said it didn't look good on display."
                        , "签到页面是否显示下个月的奖励，有人说它显示出来不好看。")
                .define("showNextReward", false);

        // 自动领取
        AUTO_REWARDED = CLIENT_BUILDER
                .comment("Whether the rewards will be automatically claimed when you sign in or re-sign-in."
                        , "签到或补签时是否自动领取奖励。")
                .define("autoRewarded", false);

        CLIENT_BUILDER.pop();

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
