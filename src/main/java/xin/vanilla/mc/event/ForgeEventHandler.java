package xin.vanilla.mc.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.capability.IPlayerSignInData;
import xin.vanilla.mc.capability.PlayerSignInDataCapability;
import xin.vanilla.mc.capability.PlayerSignInDataProvider;
import xin.vanilla.mc.config.ServerConfig;
import xin.vanilla.mc.network.ConfigDataSyncPacket;
import xin.vanilla.mc.network.ModNetworkHandler;

/**
 * Forge 事件处理
 */
@Mod.EventBusSubscriber(modid = SakuraSignIn.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * 当 AttachCapabilitiesEvent 事件发生时，此方法会为玩家实体附加自定义的能力
     * 在 Minecraft 中，实体可以拥有多种能力，这是一种扩展游戏行为的强大机制
     * 此处我们利用这个机制，为玩家实体附加一个用于签到的数据管理能力
     *
     * @param event 事件对象，包含正在附加能力的实体信息
     */
    @SubscribeEvent
    public static void onAttachCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
        // 检查事件对象是否为玩家实体，因为我们的目标是为玩家附加能力
        if (event.getObject() instanceof PlayerEntity) {
            // 为玩家实体附加一个名为 "player_sign_in_data" 的能力
            // 这个能力由 PlayerSignInDataProvider 提供，用于管理玩家的签到数据
            event.addCapability(new ResourceLocation(SakuraSignIn.MODID, "player_sign_in_data"), new PlayerSignInDataProvider());
        }
    }

    /**
     * 玩家死亡后重生或者从末地回到主世界
     */
    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            LazyOptional<IPlayerSignInData> oldSpeedCap = event.getOriginal().getCapability(PlayerSignInDataCapability.PLAYER_DATA);
            LazyOptional<IPlayerSignInData> newSpeedCap = event.getPlayer().getCapability(PlayerSignInDataCapability.PLAYER_DATA);
            if (oldSpeedCap.isPresent() && newSpeedCap.isPresent()) {
                newSpeedCap.ifPresent((newCap) -> oldSpeedCap.ifPresent((oldCap) -> newCap.deserializeNBT(oldCap.serializeNBT())));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer().level.isClientSide()) {
            // 客户端逻辑
            LOGGER.debug("Client Side");
        } else {
            // 服务器端逻辑
            LOGGER.debug("Server Side");
            if (event.getPlayer() instanceof ServerPlayerEntity) {
                // 同步玩家签到数据到客户端
                PlayerSignInDataCapability.syncPlayerData((ServerPlayerEntity) event.getPlayer());
                // 同步服务器配置到客户端
                ModNetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new ConfigDataSyncPacket());
            }
        }
        // 获取玩家的自定义数据
        IPlayerSignInData data = PlayerSignInDataCapability.getData(event.getPlayer());
        if (ServerConfig.AUTO_SIGN_IN.get()) {

        }
    }
}
