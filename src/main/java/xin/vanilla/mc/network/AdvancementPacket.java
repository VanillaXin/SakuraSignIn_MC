package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Getter
public class AdvancementPacket {
    // 存储要传输的AdvancementData对象
    private final List<AdvancementData> advancements;

    public AdvancementPacket(Collection<Advancement> advancements) {
        this.advancements = advancements.stream()
                .map(AdvancementData::fromAdvancement)
                .collect(Collectors.toList());
    }

    public AdvancementPacket(PacketBuffer buf) {
        int size = buf.readVarInt();
        List<AdvancementData> advancements = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            advancements.add(AdvancementData.readFromBuffer(buf));
        }
        this.advancements = advancements;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeVarInt(this.advancements.size());
        for (AdvancementData data : this.advancements) {
            data.writeToBuffer(buf);
        }
    }

    public static void handle(AdvancementPacket packet, Supplier<NetworkEvent.Context> ctx) {
        // 获取网络事件上下文并排队执行工作
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient()) {
                // 在客户端更新 List<AdvancementData>
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientProxy.handleAdvancement(packet));
            }
        });
        // 设置数据包已处理状态，防止重复处理
        ctx.get().setPacketHandled(true);
    }

}
