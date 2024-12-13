package xin.vanilla.mc.event;

import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.capability.IPlayerSignInData;
import xin.vanilla.mc.config.ServerConfig;

/**
 * Mod 事件处理器
 */
@Mod.EventBusSubscriber(modid = SakuraSignIn.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IPlayerSignInData.class);
    }

    @SubscribeEvent
    public void onConfigLoadOrReload(ModConfigEvent event) {
        if (event.getConfig().getSpec() == ServerConfig.SERVER_CONFIG) {
            LOGGER.debug("Server Config loaded/reloaded.");
        }
    }

}
