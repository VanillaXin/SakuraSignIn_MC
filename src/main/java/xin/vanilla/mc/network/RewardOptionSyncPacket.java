package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import xin.vanilla.mc.config.RewardOptionData;
import xin.vanilla.mc.config.RewardOptionDataManager;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@Getter
public class RewardOptionSyncPacket {
    /**
     * 签到奖励数据
     */
    private final RewardOptionData rewardOptionData;

    public RewardOptionSyncPacket(RewardOptionData rewardOptionData) {
        this.rewardOptionData = rewardOptionData;
    }

    public RewardOptionSyncPacket(PacketBuffer buf) {
        this.rewardOptionData = RewardOptionDataManager.deserializeRewardOption(new String(buf.readByteArray(), StandardCharsets.UTF_8));
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeByteArray(RewardOptionDataManager.serializeRewardOption(rewardOptionData).getBytes(StandardCharsets.UTF_8));
    }

    public static void handle(RewardOptionSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                // 备份 RewardOption
                RewardOptionDataManager.backupRewardOption();
                // 更新 RewardOption
                RewardOptionDataManager.setRewardOptionData(packet.getRewardOptionData());
                RewardOptionDataManager.setRewardOptionDataChanged(true);
                RewardOptionDataManager.saveRewardOption();
            } else if (ctx.get().getDirection().getReceptionSide().isServer()) {
                // 备份 RewardOption
                RewardOptionDataManager.backupRewardOption(false);
                // 更新 RewardOption
                RewardOptionDataManager.setRewardOptionData(packet.getRewardOptionData());
                RewardOptionDataManager.saveRewardOption();
                ServerPlayerEntity sender = ctx.get().getSender();
                if (sender != null) {
                    for (ServerPlayerEntity player : sender.server.getPlayerList().getPlayers()) {
                        // 同步 RewardOption 至所有在线玩家
                        ModNetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new RewardOptionSyncPacket(RewardOptionDataManager.getRewardOptionData()));
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
