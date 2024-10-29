package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xin.vanilla.mc.capability.PlayerSignInDataCapability;
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
        this.autoRewarded = autoRewarded;
        this.signInType = signInType;
    }

    public SignInPacket(PacketBuffer buf) {
        this.signInTime = buf.readDate();
        this.autoRewarded = buf.readBoolean();
        this.signInType = ESignInType.valueOf(buf.readInt());
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeDate(signInTime);
        buf.writeBoolean(autoRewarded);
        buf.writeInt(signInType.getCode());
    }

    public static void handle(SignInPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player != null) {
                RewardManager.signIn(player, packet);
                PlayerSignInDataCapability.syncPlayerData(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
