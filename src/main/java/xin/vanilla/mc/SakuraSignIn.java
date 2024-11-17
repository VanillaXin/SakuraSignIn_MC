package xin.vanilla.mc;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xin.vanilla.mc.command.SignInCommand;
import xin.vanilla.mc.config.ClientConfig;
import xin.vanilla.mc.config.ServerConfig;
import xin.vanilla.mc.config.SignInDataManager;
import xin.vanilla.mc.event.ClientEventHandler;
import xin.vanilla.mc.network.ModNetworkHandler;

import java.io.File;
import java.nio.file.Path;

@Mod(modid = SakuraSignIn.MODID, name = SakuraSignIn.MODNAME, version = SakuraSignIn.MODVERSION)
public class SakuraSignIn {
    public static final String MODID = "sakura_sign_in";
    public static final String MODNAME = "樱花签";
    public static final String MODVERSION = "1.12.2-0.0.2-beta.1";
    public static final String PNG_CHUNK_NAME = "vacb";

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MODID)
    public static SakuraSignIn INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * 是否有对应的服务端
     */
    @Getter
    @Setter
    private static boolean enabled;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // 注册网络通道
        ModNetworkHandler.registerPackets();

        // 注册服务器和客户端配置
        File serverConfigFile = new File(event.getModConfigurationDirectory(), String.format("%s-server.cfg", MODID));
        ServerConfig.init(serverConfigFile);

        File clientConfigFile = new File(event.getModConfigurationDirectory(), String.format("%s-client.cfg", MODID));
        ClientConfig.init(clientConfigFile);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // 仅在客户端执行的代码
        if (event.getSide().isClient()) {
            // 注册键盘按键绑定
            ClientEventHandler.registerKeyBindings();
            // 创建配置文件目录
            ClientEventHandler.createConfigPath();
        }
    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {

    }

    // 服务器启动时加载数据
    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        // 加载签到数据
        SignInDataManager.loadSignInData();
        LOGGER.debug("SignIn data loaded.");
        LOGGER.debug("Registering commands");
        // 注册签到命令到事件调度器
        SignInCommand.register(event.getServer().getCommands().getDispatcher());
    }

    // 服务器关闭时保存数据
    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        // SignInDataManager.saveSignInData();
    }

    /**
     * 玩家注销事件
     *
     * @param event 玩家注销事件对象，通过该对象可以获取到注销的玩家对象
     */
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        LOGGER.debug("Player has logged out.");
        // 获取退出的玩家对象
        EntityPlayer player = event.getPlayer();
        // 判断是否在客户端并且退出的玩家是客户端的当前玩家
        if (player.getCommandSenderWorld().isClientSide) {
            if (Minecraft.getInstance().player.getUUID().equals(player.getUUID())) {
                LOGGER.debug("Current player has logged out.");
                // 当前客户端玩家与退出的玩家相同
                enabled = false;
            }
        }
    }

    /**
     * 世界卸载事件
     *
     * @param event 世界卸载事件对象，通过该对象可以获取到卸载的世界对象
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onWorldUnload(WorldEvent.Unload event) {
        LOGGER.debug("World has unloaded.");
        // 当玩家离开世界时
        enabled = false;
    }

    /**
     * 打开指定路径的文件夹
     */
    @SideOnly(Side.CLIENT)
    public static void openFolder(Path path) {
        try {
            // Windows
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("explorer.exe", path.toString()).start();
            }
            // macOS
            else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                new ProcessBuilder("open", path.toString()).start();
            }
            // Linux
            else {
                new ProcessBuilder("xdg-open", path.toString()).start();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to open folder: {}", e.getMessage());
        }
    }
}
