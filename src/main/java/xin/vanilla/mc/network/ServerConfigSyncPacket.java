package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xin.vanilla.mc.config.ServerConfig;
import xin.vanilla.mc.enums.ETimeCoolingMethod;
import xin.vanilla.mc.util.DateUtils;

import java.util.Date;
import java.util.function.Supplier;

@Getter
public class ServerConfigSyncPacket {
    /**
     * 自动签到
     */
    private final boolean autoSignIn;
    /**
     * 签到时间冷却方式
     */
    private final ETimeCoolingMethod timeCoolingMethod;
    /**
     * 签到冷却刷新时间
     */
    private final double timeCoolingTime;
    /**
     * 签到冷却刷新间隔
     */
    private final double timeCoolingInterval;
    /**
     * 是否启用补签卡
     */
    private final boolean signInCard;
    /**
     * 补签卡是否只领取基础奖励
     */
    private final boolean signInCardOnlyBaseReward;

    /**
     * 服务器时间
     */
    private final Date serverTime;

    /**
     * 实际时间
     */
    private final Date actualTime;

    public ServerConfigSyncPacket() {
        this.autoSignIn = ServerConfig.AUTO_SIGN_IN.get();
        this.timeCoolingMethod = ServerConfig.TIME_COOLING_METHOD.get();
        this.timeCoolingTime = ServerConfig.TIME_COOLING_TIME.get();
        this.timeCoolingInterval = ServerConfig.TIME_COOLING_INTERVAL.get();
        this.signInCard = ServerConfig.SIGN_IN_CARD.get();
        this.signInCardOnlyBaseReward = ServerConfig.SIGN_IN_CARD_ONLY_BASE_REWARD.get();
        this.serverTime = DateUtils.format(ServerConfig.SERVER_TIME.get());
        this.actualTime = DateUtils.format(ServerConfig.ACTUAL_TIME.get());
    }

    public ServerConfigSyncPacket(PacketBuffer buf) {
        this.autoSignIn = buf.readBoolean();
        this.timeCoolingMethod = ETimeCoolingMethod.valueOf(buf.readInt());
        this.timeCoolingTime = buf.readDouble();
        this.timeCoolingInterval = buf.readDouble();
        this.signInCard = buf.readBoolean();
        this.signInCardOnlyBaseReward = buf.readBoolean();
        this.serverTime = buf.readDate();
        this.actualTime = buf.readDate();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBoolean(this.autoSignIn);
        buf.writeInt(this.timeCoolingMethod.getCode());
        buf.writeDouble(this.timeCoolingTime);
        buf.writeDouble(this.timeCoolingInterval);
        buf.writeBoolean(this.signInCard);
        buf.writeBoolean(this.signInCardOnlyBaseReward);
        buf.writeDate(this.serverTime);
        buf.writeDate(this.actualTime);
    }

    public static void handle(ServerConfigSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerConfig.AUTO_SIGN_IN.set(packet.autoSignIn);
            ServerConfig.TIME_COOLING_METHOD.set(packet.timeCoolingMethod);
            ServerConfig.TIME_COOLING_TIME.set(packet.timeCoolingTime);
            ServerConfig.TIME_COOLING_INTERVAL.set(packet.timeCoolingInterval);
            ServerConfig.SIGN_IN_CARD.set(packet.signInCard);
            ServerConfig.SIGN_IN_CARD_ONLY_BASE_REWARD.set(packet.signInCardOnlyBaseReward);
            ServerConfig.SERVER_TIME.set(DateUtils.toDateTimeString(packet.serverTime));
            ServerConfig.ACTUAL_TIME.set(DateUtils.toDateTimeString(packet.actualTime));
        });
        ctx.get().setPacketHandled(true);
    }
}
