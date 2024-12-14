package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.PacketDistributor;
import xin.vanilla.mc.config.RewardOptionData;
import xin.vanilla.mc.config.RewardOptionDataManager;

import java.nio.charset.StandardCharsets;

@Getter
public class RewardOptionSyncPacket {
    /**
     * 签到奖励数据
     */
    private final RewardOptionData rewardOptionData;

    public RewardOptionSyncPacket(RewardOptionData rewardOptionData) {
        this.rewardOptionData = rewardOptionData;
    }

    public RewardOptionSyncPacket(FriendlyByteBuf buf) {
        this.rewardOptionData = RewardOptionDataManager.deserializeRewardOption(new String(buf.readByteArray(), StandardCharsets.UTF_8));
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeByteArray(RewardOptionDataManager.serializeRewardOption(rewardOptionData).getBytes(StandardCharsets.UTF_8));
    }

    public static void handle(RewardOptionSyncPacket packet, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.isClientSide()) {
                // 备份 RewardOption
                RewardOptionDataManager.backupRewardOption();
                // 更新 RewardOption
                RewardOptionDataManager.setRewardOptionData(packet.getRewardOptionData());
                RewardOptionDataManager.setRewardOptionDataChanged(true);
                RewardOptionDataManager.saveRewardOption();
            } else if (ctx.isServerSide()) {
                ServerPlayer sender = ctx.getSender();
                if (sender != null) {
                    // 判断是否为管理员
                    if (sender.hasPermissions(3)) {
                        // 备份 RewardOption
                        RewardOptionDataManager.backupRewardOption(false);
                        // 更新 RewardOption
                        RewardOptionDataManager.setRewardOptionData(packet.getRewardOptionData());
                        RewardOptionDataManager.saveRewardOption();
                        for (ServerPlayer player : sender.server.getPlayerList().getPlayers()) {
                            // 排除发送者
                            if (player.getStringUUID().equalsIgnoreCase(sender.getStringUUID())) continue;
                            // 同步 RewardOption 至所有在线玩家
                            ModNetworkHandler.INSTANCE.send(new RewardOptionSyncPacket(RewardOptionDataManager.getRewardOptionData()), PacketDistributor.PLAYER.with(player));
                        }
                    }
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
