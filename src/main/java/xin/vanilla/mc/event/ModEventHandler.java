package xin.vanilla.mc.event;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.capability.PlayerSignInDataCapability;
import xin.vanilla.mc.config.ServerConfig;

/**
 * Mod 事件处理器
 */
@Mod.EventBusSubscriber(modid = SakuraSignIn.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // 注册 PlayerDataCapability
        PlayerSignInDataCapability.register();
    }

    @SubscribeEvent
    public void onConfigLoadOrReload(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() == ServerConfig.SERVER_CONFIG) {
            LOGGER.debug("Server Config loaded/reloaded.");
        }
    }

}
