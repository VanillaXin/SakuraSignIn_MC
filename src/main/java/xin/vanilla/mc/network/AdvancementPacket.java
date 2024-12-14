package xin.vanilla.mc.network;

import lombok.Getter;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public class AdvancementPacket {
    // 存储要传输的AdvancementData对象
    private final List<AdvancementData> advancements;

    public AdvancementPacket(Collection<AdvancementHolder> advancements) {
        this.advancements = advancements.stream()
                .map(AdvancementData::fromAdvancement)
                .collect(Collectors.toList());
    }

    public AdvancementPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<AdvancementData> advancements = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            advancements.add(AdvancementData.readFromBuffer(buf));
        }
        this.advancements = advancements;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(this.advancements.size());
        for (AdvancementData data : this.advancements) {
            data.writeToBuffer(buf);
        }
    }

    public static void handle(AdvancementPacket packet, CustomPayloadEvent.Context ctx) {
        // 获取网络事件上下文并排队执行工作
        ctx.enqueueWork(() -> {
            if (ctx.isClientSide()) {
                // 在客户端更新 List<AdvancementData>
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientProxy.handleAdvancement(packet));
            }
        });
        // 设置数据包已处理状态，防止重复处理
        ctx.setPacketHandled(true);
    }

}
