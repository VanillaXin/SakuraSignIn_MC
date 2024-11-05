package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xin.vanilla.mc.config.SignInData;
import xin.vanilla.mc.config.SignInDataManager;

import java.util.function.Supplier;

@Getter
public class SignInDataSyncPacket {
    /**
     * 签到奖励数据
     */
    private final SignInData signInData;

    public SignInDataSyncPacket(SignInData signInData) {
        this.signInData = signInData;
    }

    public SignInDataSyncPacket(PacketBuffer buf) {
        this.signInData = SignInDataManager.deserializeSignInData(buf.readUtf());
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(SignInDataManager.serializeSignInData(signInData));
    }

    public static void handle(SignInDataSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                // 在客户端更新 SignInData
                SignInDataManager.setSignInData(packet.getSignInData());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
