package xin.vanilla.mc;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xin.vanilla.mc.command.CheckInCommand;
import xin.vanilla.mc.config.ClientConfig;
import xin.vanilla.mc.config.ServerConfig;
import xin.vanilla.mc.event.ClientEventHandler;
import xin.vanilla.mc.network.ModNetworkHandler;

@Mod(SakuraSignIn.MODID)
public class SakuraSignIn {

    public static final String MODID = "sakura_sign_in";

    private static final Logger LOGGER = LogManager.getLogger();

    public SakuraSignIn() {

        // 注册网络通道
        ModNetworkHandler.registerPackets();

        // 注册当前实例到MinecraftForge的事件总线，以便监听和处理游戏内的各种事件
        MinecraftForge.EVENT_BUS.register(this);

        // 注册服务器和客户端配置
        // MinecraftForge.EVENT_BUS.addListener(this::onLoadConfig);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SERVER_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);

        // 注册客户端设置事件到MOD事件总线
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    /**
     * 在客户端设置阶段触发的事件处理方法
     * 此方法主要用于接收 FML 客户端设置事件，并执行相应的初始化操作
     *
     * @param event FMLClientSetupEvent 类型的事件参数，包含 Minecraft 的供应商对象
     */
    @SubscribeEvent
    public void onClientSetup(final FMLClientSetupEvent event) {
        LOGGER.debug("Got game settings {}", event.getMinecraftSupplier().get().options);
        // 注册键绑定
        LOGGER.debug("Registering key bindings");
        ClientEventHandler.registerKeyBindings();
    }

    /**
     * 注册命令事件的处理方法
     * 当注册命令事件被触发时，此方法将被调用
     * 该方法主要用于注册签到命令到事件调度器
     *
     * @param event 注册命令事件对象，通过该对象可以获取到事件调度器
     */
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        // 注册签到命令到事件调度器
        LOGGER.debug("Registering commands");
        CheckInCommand.register(event.getDispatcher());
    }
}
