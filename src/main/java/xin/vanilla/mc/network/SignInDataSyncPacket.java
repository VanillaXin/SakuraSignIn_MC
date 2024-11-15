package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import xin.vanilla.mc.config.SignInData;
import xin.vanilla.mc.config.SignInDataManager;

@Getter
public class SignInDataSyncPacket {
    /**
     * 签到奖励数据
     */
    private final SignInData signInData;

    public SignInDataSyncPacket(SignInData signInData) {
        this.signInData = signInData;
    }

    public SignInDataSyncPacket(FriendlyByteBuf buf) {
        this.signInData = SignInDataManager.deserializeSignInData(buf.readUtf());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(SignInDataManager.serializeSignInData(signInData));
    }

    public static void handle(SignInDataSyncPacket packet, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.isClientSide()) {
                // 在客户端更新 SignInData
                SignInDataManager.setSignInData(packet.getSignInData());
            }
        });
        ctx.setPacketHandled(true);
    }
}
