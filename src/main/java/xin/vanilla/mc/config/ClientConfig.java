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
     * 主题路径
     */
    private static final ForgeConfigSpec.ConfigValue<String> THEME = BUILDER
            .comment("theme textures path, can be external path: config/sakura_sign_in/themes/your_theme.png"
                    , "主题材质路径，可为外部路径： config/sakura_sign_in/themes/your_theme.png")
            .define("theme", "textures/gui/sign_in_calendar_sakura.png");

    /**
     * 是否使用内置主题特殊图标
     */
    private static final ForgeConfigSpec.BooleanValue SPECIAL_THEME = BUILDER
            .comment("Whether or not to use the built-in theme special icons."
                    , "是否使用内置主题特殊图标。")
            .define("specialTheme", false);

    /**
     * 签到页面显示上月奖励
     */
    private static final ForgeConfigSpec.BooleanValue SHOW_LAST_REWARD = BUILDER
            .comment("The sign-in page displays last month's rewards. Someone said it didn't look good on display."
                    , "签到页面是否显示上个月的奖励，有人说它显示出来不好看。")
            .define("showLastReward", false);

    /**
     * 签到页面显示下月奖励
     */
    private static final ForgeConfigSpec.BooleanValue SHOW_NEXT_REWARD = BUILDER
            .comment("The sign-in page displays next month's rewards. Someone said it didn't look good on display."
                    , "签到页面是否显示下个月的奖励，有人说它显示出来不好看。")
            .define("showNextReward", false);

    /**
     * 自动领取
     */
    private static final ForgeConfigSpec.BooleanValue AUTO_REWARDED = BUILDER
            .comment("Whether the rewards will be automatically claimed when you sign in or re-sign-in."
                    , "签到或补签时是否自动领取奖励。")
            .define("autoRewarded", false);

    public static final ForgeConfigSpec CLIENT_CONFIG = BUILDER.build();

    /**
     * 获取主题文件路径
     */
    public static String getTheme() {
        return THEME.get();
    }

    /**
     * 设置主题文件路径
     *
     * @param theme 主题文件路径
     */
    public static void setTheme(String theme) {
        THEME.set(theme);
    }

    /**
     * 获取是否使用内置主题特殊图标
     */
    public static boolean isSpecialTheme() {
        return SPECIAL_THEME.get();
    }

    /**
     * 设置是否使用内置主题特殊图标
     *
     * @param specialTheme 是否使用内置主题特殊图标
     */
    public static void setSpecialTheme(boolean specialTheme) {
        SPECIAL_THEME.set(specialTheme);
    }

    /**
     * 获取签到页面显示上月奖励
     */
    public static boolean isShowLastReward() {
        return SHOW_LAST_REWARD.get();
    }

    /**
     * 设置签到页面显示上月奖励
     *
     * @param showLastReward 是否显示上月奖励
     */
    public static void setShowLastReward(boolean showLastReward) {
        SHOW_LAST_REWARD.set(showLastReward);
    }

    /**
     * 获取签到页面显示下月奖励
     */
    public static boolean isShowNextReward() {
        return SHOW_NEXT_REWARD.get();
    }

    /**
     * 设置签到页面显示下月奖励
     *
     * @param showNextReward 是否显示下月奖励
     */
    public static void setShowNextReward(boolean showNextReward) {
        SHOW_NEXT_REWARD.set(showNextReward);
    }

    /**
     * 获取自动领取
     */
    public static boolean isAutoRewarded() {
        return AUTO_REWARDED.get();
    }

    /**
     * 设置自动领取
     *
     * @param autoRewarded 是否自动领取
     */
    public static void setAutoRewarded(boolean autoRewarded) {
        AUTO_REWARDED.set(autoRewarded);
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // 配置文件更新事件延迟较大, 不考虑使用Minecraft Development模板生成的推荐方法
        if (event.getConfig().getSpec() == CLIENT_CONFIG) {
            SakuraSignIn.LOGGER.info("Loaded client config file {}", event.getConfig().getFileName());
        }
    }
}
