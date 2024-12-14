package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;
import xin.vanilla.mc.capability.IPlayerSignInData;
import xin.vanilla.mc.capability.PlayerSignInData;

import java.util.UUID;

@Getter
public class PlayerDataSyncPacket {
    private final UUID playerUUID;
    private final IPlayerSignInData data;

    public PlayerDataSyncPacket(UUID playerUUID, IPlayerSignInData data) {
        this.playerUUID = playerUUID;
        this.data = data;
    }

    public PlayerDataSyncPacket(FriendlyByteBuf buffer) {
        playerUUID = buffer.readUUID();
        data = new PlayerSignInData();
        data.readFromBuffer(buffer);
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUUID(playerUUID);
        data.writeToBuffer(buffer);
    }

    public static void handle(PlayerDataSyncPacket packet, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                // 在客户端更新 PlayerSignInDataCapability
                // 获取玩家并更新 Capability 数据
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientProxy.handleSynPlayerData(packet));
            }
        });
        ctx.setPacketHandled(true);
    }
}
