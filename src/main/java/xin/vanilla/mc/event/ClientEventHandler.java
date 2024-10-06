package xin.vanilla.mc.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import xin.vanilla.mc.client.CheckInScreen;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "sakura_sign_in", value = Dist.CLIENT)
public class ClientEventHandler {

    // 定义一个静态常量 SIGN_IN_KEY，用于绑定签到功能的快捷键
    // 该快捷键的名称为 "key.sakurasignin.signin"，按键为 G
    // 它属于 "key.categories.sakurasignin" 这一类别
    private static final KeyBinding SIGN_IN_KEY = new KeyBinding("key.sakurasignin.signin",
            GLFW.GLFW_KEY_G, "key.categories.sakurasignin");

    /**
     * 注册键绑定
     * 该方法主要用于在客户端注册自定义的键绑定在此处注册的键绑定能够在游戏中被识别和处理
     */
    public static void registerKeyBindings() {
        // 在客户端注册SIGN_IN键绑定，使得游戏能够识别玩家按下该键时执行对应的操作
        ClientRegistry.registerKeyBinding(SIGN_IN_KEY);
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
            // 打开签到界面
            Minecraft.getInstance().setScreen(new CheckInScreen());
        }
    }
}
