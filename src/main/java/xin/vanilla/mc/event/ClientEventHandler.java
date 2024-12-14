package xin.vanilla.mc.event;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.config.ClientConfig;
import xin.vanilla.mc.rewards.RewardManager;
import xin.vanilla.mc.screen.RewardOptionScreen;
import xin.vanilla.mc.screen.SignInScreen;
import xin.vanilla.mc.screen.coordinate.TextureCoordinate;
import xin.vanilla.mc.util.PNGUtils;
import xin.vanilla.mc.util.TextureUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static xin.vanilla.mc.SakuraSignIn.PNG_CHUNK_NAME;
import static xin.vanilla.mc.util.I18nUtils.getI18nKey;

/**
 * 客户端事件处理器
 */
@Mod.EventBusSubscriber(modid = SakuraSignIn.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String CATEGORIES = "key.sakura_sign_in.categories";

    // 定义按键绑定
    public static KeyMapping SIGN_IN_SCREEN_KEY = new KeyMapping("key.sakura_sign_in.sign_in",
            GLFW.GLFW_KEY_H, CATEGORIES);
    public static KeyMapping REWARD_OPTION_SCREEN_KEY = new KeyMapping("key.sakura_sign_in.reward_option",
            GLFW.GLFW_KEY_O, CATEGORIES);

    /**
     * 注册键绑定
     */
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        LOGGER.debug("Registering key bindings");
        event.register(SIGN_IN_SCREEN_KEY);
        event.register(REWARD_OPTION_SCREEN_KEY);
    }

    /**
     * 创建配置文件目录
     */
    public static void createConfigPath() {
        File themesPath = new File(FMLPaths.CONFIGDIR.get().resolve(SakuraSignIn.MODID).toFile(), "themes");
        if (!themesPath.exists()) {
            themesPath.mkdirs();
        }
    }

    /**
     * 加载主题纹理
     */
    public static void loadThemeTexture() {
        try {
            SakuraSignIn.setThemeTexture(TextureUtils.loadCustomTexture(ClientConfig.THEME.get()));
            SakuraSignIn.setSpecialVersionTheme(Boolean.TRUE.equals(ClientConfig.SPECIAL_THEME.get()));
            InputStream inputStream = Minecraft.getInstance().getResourceManager().getResourceOrThrow(SakuraSignIn.getThemeTexture()).open();
            SakuraSignIn.setThemeTextureCoordinate(PNGUtils.readLastPrivateChunk(inputStream, PNG_CHUNK_NAME));
        } catch (IOException | ClassNotFoundException ignored) {
        }
        if (SakuraSignIn.getThemeTextureCoordinate(false) == null) {
            // 使用默认配置
            SakuraSignIn.setThemeTextureCoordinate(TextureCoordinate.getDefault());
        }
        // 设置内置主题特殊图标UV的偏移量
        if (SakuraSignIn.isSpecialVersionTheme() && SakuraSignIn.getThemeTextureCoordinate().isSpecial()) {
            SakuraSignIn.getThemeTextureCoordinate().getNotSignedInUV().setX(320);
            SakuraSignIn.getThemeTextureCoordinate().getSignedInUV().setX(320);
        } else {
            SakuraSignIn.getThemeTextureCoordinate().getNotSignedInUV().setX(0);
            SakuraSignIn.getThemeTextureCoordinate().getSignedInUV().setX(0);
        }
    }

    /**
     * 在客户端Tick事件触发时执行
     *
     * @param event 客户端Tick事件
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 检测并消费点击事件
        if (SIGN_IN_SCREEN_KEY.consumeClick()) {
            // 打开签到界面
            if (SakuraSignIn.isEnabled()) {
                SakuraSignIn.setCalendarCurrentDate(RewardManager.getCompensateDate(new Date()));
                Minecraft.getInstance().setScreen(new SignInScreen());
            } else {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    player.sendSystemMessage(Component.translatable(getI18nKey("SakuraSignIn server is offline!")));
                }
            }
        } else if (REWARD_OPTION_SCREEN_KEY.consumeClick()) {
            // 打开奖励配置界面
            Minecraft.getInstance().setScreen(new RewardOptionScreen());
        }
    }
}
