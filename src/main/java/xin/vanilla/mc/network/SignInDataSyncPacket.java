package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xin.vanilla.mc.config.RewardOptionData;
import xin.vanilla.mc.config.RewardOptionDataManager;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@Getter
public class SignInDataSyncPacket {
    /**
     * 签到奖励数据
     */
    private final RewardOptionData rewardOptionData;

    public SignInDataSyncPacket(RewardOptionData rewardOptionData) {
        this.rewardOptionData = rewardOptionData;
    }

    public SignInDataSyncPacket(PacketBuffer buf) {
        this.rewardOptionData = RewardOptionDataManager.deserializeSignInData(new String(buf.readByteArray(), StandardCharsets.UTF_8));
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeByteArray(RewardOptionDataManager.serializeSignInData(rewardOptionData).getBytes(StandardCharsets.UTF_8));
    }

    public static void handle(SignInDataSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                // 在客户端更新 SignInData
                RewardOptionDataManager.setRewardOptionData(packet.getRewardOptionData());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
