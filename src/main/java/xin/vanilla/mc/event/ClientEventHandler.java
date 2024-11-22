package xin.vanilla.mc.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.rewards.RewardManager;
import xin.vanilla.mc.screen.RewardOptionScreen;
import xin.vanilla.mc.screen.SignInScreen;

import java.io.File;
import java.util.Date;

/**
 * 客户端事件处理器
 */
@Mod.EventBusSubscriber(modid = SakuraSignIn.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String CATEGORIES = "key.sakura_sign_in.categories";

    // 定义按键绑定
    public static KeyBinding SIGN_IN_SCREEN_KEY = new KeyBinding("key.sakura_sign_in.sign_in",
            GLFW.GLFW_KEY_H, CATEGORIES);
    public static KeyBinding REWARD_OPTION_SCREEN_KEY = new KeyBinding("key.sakura_sign_in.reward_option",
            GLFW.GLFW_KEY_O, CATEGORIES);

    /**
     * 注册键绑定
     */
    public static void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(SIGN_IN_SCREEN_KEY);
        ClientRegistry.registerKeyBinding(REWARD_OPTION_SCREEN_KEY);
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
                ClientPlayerEntity player = Minecraft.getInstance().player;
                if (player != null) {
                    player.sendMessage(new StringTextComponent("SakuraSignIn server is offline!"), player.getUUID());
                }
            }
        } else if (REWARD_OPTION_SCREEN_KEY.consumeClick()) {
            // 打开奖励配置界面
            Minecraft.getInstance().setScreen(new RewardOptionScreen());
        }
    }
}
