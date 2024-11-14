package xin.vanilla.mc.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import xin.vanilla.mc.SakuraSignIn;

/**
 * 客户端配置
 */
@Mod.EventBusSubscriber(modid = SakuraSignIn.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder().comment("Client Settings").push("client");

    /**
     * 主题设置
     */
    public static final ForgeConfigSpec.ConfigValue<String> THEME = BUILDER
            .comment("theme textures path, can be external path: config/sakura_sign_in/themes/your_theme.png"
                    , "主题材质路径，可为外部路径： config/sakura_sign_in/themes/your_theme.png")
            .define("theme", "textures/gui/sign_in_calendar_sakura.png");

    /**
     * 是否使用内置主题特殊图标
     */
    public static final ForgeConfigSpec.BooleanValue SPECIAL_THEME = BUILDER
            .comment("Whether or not to use the built-in theme special icons."
                    , "是否使用内置主题特殊图标。")
            .define("specialTheme", false);

    /**
     * 签到页面显示上月奖励
     */
    public static final ForgeConfigSpec.BooleanValue SHOW_LAST_REWARD = BUILDER
            .comment("The sign-in page displays last month's rewards. Someone said it didn't look good on display."
                    , "签到页面是否显示上个月的奖励，有人说它显示出来不好看。")
            .define("showLastReward", false);

    /**
     * 签到页面显示下月奖励
     */
    public static final ForgeConfigSpec.BooleanValue SHOW_NEXT_REWARD = BUILDER
            .comment("The sign-in page displays next month's rewards. Someone said it didn't look good on display."
                    , "签到页面是否显示下个月的奖励，有人说它显示出来不好看。")
            .define("showNextReward", false);

    /**
     * 自动领取
     */
    public static final ForgeConfigSpec.BooleanValue AUTO_REWARDED = BUILDER
            .comment("Whether the rewards will be automatically claimed when you sign in or re-sign-in."
                    , "签到或补签时是否自动领取奖励。")
            .define("autoRewarded", false);

    public static final ForgeConfigSpec CLIENT_CONFIG = BUILDER.build();

    /**
     * 主题设置
     */
    public static String theme;
    /**
     * 是否使用内置主题特殊图标
     */
    public static boolean specialTheme;
    /**
     * 签到页面显示上月奖励
     */
    public static boolean showLastReward;
    /**
     * 签到页面显示下月奖励
     */
    public static boolean showNextReward;
    /**
     * 自动领取
     */
    public static boolean autoRewarded;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == CLIENT_CONFIG) {
            theme = THEME.get();
            specialTheme = SPECIAL_THEME.get();
            showLastReward = SHOW_LAST_REWARD.get();
            showNextReward = SHOW_NEXT_REWARD.get();
            autoRewarded = AUTO_REWARDED.get();
        }
    }
}
