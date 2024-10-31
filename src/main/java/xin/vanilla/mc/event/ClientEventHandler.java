package xin.vanilla.mc.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.screen.CalendarScreen;
import xin.vanilla.mc.screen.TestScreen;

/**
 * 客户端事件处理器
 */
@Mod.EventBusSubscriber(modid = SakuraSignIn.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String CATEGORIES = "key.categories.sakura_sign_in";

    // 定义按键绑定
    public static KeyBinding SIGN_IN_KEY = new KeyBinding("key.sakura_sign_in.sign_in",
            GLFW.GLFW_KEY_G, CATEGORIES);
    public static KeyBinding CALENDAR_KEY = new KeyBinding("key.sakura_sign_in.calendar",
            GLFW.GLFW_KEY_H, CATEGORIES);

    /**
     * 注册键绑定
     */
    public static void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(SIGN_IN_KEY);
        ClientRegistry.registerKeyBinding(CALENDAR_KEY);
    }

    /**
     * 在客户端Tick事件触发时执行
     *
     * @param event 客户端Tick事件
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 检测并消费点击事件
        if (SIGN_IN_KEY.consumeClick()) {
            // 打开测试界面
            Minecraft.getInstance().setScreen(new TestScreen());
        } else if (CALENDAR_KEY.consumeClick()) {
            // 打开日历界面
            Minecraft.getInstance().setScreen(new CalendarScreen());
        }
    }
}
