package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xin.vanilla.mc.capability.IPlayerSignInData;
import xin.vanilla.mc.capability.PlayerSignInData;
import xin.vanilla.mc.capability.PlayerSignInDataCapability;

import java.util.UUID;
import java.util.function.Supplier;

@Getter
public class PlayerDataSyncPacket {
    private final UUID playerUUID;
    private final IPlayerSignInData data;

    public PlayerDataSyncPacket(UUID playerUUID, IPlayerSignInData data) {
        this.playerUUID = playerUUID;
        this.data = data;
    }

    public PlayerDataSyncPacket(PacketBuffer buffer) {
        playerUUID = buffer.readUUID();
        data = new PlayerSignInData();
        data.readFromBuffer(buffer);
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeUUID(playerUUID);
        data.writeToBuffer(buffer);
    }

    public static void handle(PlayerDataSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 在客户端更新 PlayerSignInDataCapability
            // 获取玩家并更新 Capability 数据
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null) {
                IPlayerSignInData clientData = PlayerSignInDataCapability.getData(player);
                PlayerSignInDataCapability.PLAYER_DATA.readNBT(clientData, null, packet.data.serializeNBT());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
