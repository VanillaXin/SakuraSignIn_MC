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
import xin.vanilla.mc.screen.CalendarScreen;
import xin.vanilla.mc.screen.CheckInScreen;

@Mod.EventBusSubscriber(modid = "sakura_sign_in", value = Dist.CLIENT)
public class ClientEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CATEGORIES = "key.categories.sakurasignin";

    // 定义一个静态常量 SIGN_IN_KEY，用于绑定签到功能的快捷键
    // 该快捷键的名称为 "key.sakurasignin.signin"，按键为 G
    // 它属于类别 CATEGORIES
    public static KeyBinding SIGN_IN_KEY = new KeyBinding("key.sakurasignin.signin",
            GLFW.GLFW_KEY_G, CATEGORIES);
    public static KeyBinding CALENDAR_KEY = new KeyBinding("key.sakurasignin.calendar",
            GLFW.GLFW_KEY_H, CATEGORIES);

    /**
     * 注册键绑定
     * 该方法主要用于在客户端注册自定义的键绑定在此处注册的键绑定能够在游戏中被识别和处理
     */
    public static void registerKeyBindings() {
        // 在客户端注册SIGN_IN键绑定，使得游戏能够识别玩家按下该键时执行对应的操作
        ClientRegistry.registerKeyBinding(SIGN_IN_KEY);
        ClientRegistry.registerKeyBinding(CALENDAR_KEY);
    }

    /**
     * 在客户端Tick事件触发时执行
     * 此方法主要用于检测是否需要打开签到界面
     * 当检测到玩家尝试签到时，会打开签到界面，允许玩家进行签到操作
     *
     * @param event 客户端Tick事件
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 检测并消费签到点击事件
        if (SIGN_IN_KEY.consumeClick()) {
            // 打开测试界面
            Minecraft.getInstance().setScreen(new CheckInScreen());
        } else if (CALENDAR_KEY.consumeClick()) {
            // 打开日历界面
            Minecraft.getInstance().setScreen(new CalendarScreen());
        }
    }
}
