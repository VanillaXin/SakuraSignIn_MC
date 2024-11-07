package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xin.vanilla.mc.enums.ESignInType;
import xin.vanilla.mc.rewards.RewardManager;

import java.util.Date;
import java.util.function.Supplier;

@Getter
public class SignInPacket {
    private final Date signInTime;
    private final boolean autoRewarded;
    private final ESignInType signInType;

    public SignInPacket(Date signInTime, boolean autoRewarded, ESignInType signInType) {
        this.signInTime = signInTime;
        this.autoRewarded = signInType.equals(ESignInType.REWARD) || autoRewarded;
        this.signInType = signInType;
    }

    public SignInPacket(FriendlyByteBuf buf) {
        this.signInTime = buf.readDate();
        this.autoRewarded = buf.readBoolean();
        this.signInType = ESignInType.valueOf(buf.readInt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeDate(signInTime);
        buf.writeBoolean(autoRewarded);
        buf.writeInt(signInType.getCode());
    }

    public static void handle(SignInPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                RewardManager.signIn(player, packet);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
