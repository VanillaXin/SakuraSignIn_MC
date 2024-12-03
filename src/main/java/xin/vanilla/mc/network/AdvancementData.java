package xin.vanilla.mc.network;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

/**
 * 进度信息
 */
@Data
@Accessors(chain = true)
public class AdvancementData {
    @NonNull
    public final ResourceLocation id;
    @NonNull
    public final String title;
    @NonNull
    public final String description;
    @NonNull
    public final ItemStack icon;

    public AdvancementData(@NonNull ResourceLocation id, @NonNull String title, @NonNull String description, @NonNull ItemStack icon) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public void writeToBuffer(PacketBuffer buffer) {
        buffer.writeResourceLocation(id);
        buffer.writeUtf(title);
        buffer.writeUtf(description);
        buffer.writeItemStack(icon, false);
    }

    public static AdvancementData fromAdvancement(Advancement advancement) {
        DisplayInfo displayInfo = advancement.getDisplay();
        if (displayInfo == null) {
            return new AdvancementData(
                    advancement.getId(),
                    advancement.getId().toString(),
                    "",
                    new ItemStack(Items.AIR)
            );
        }
        return new AdvancementData(
                advancement.getId(),
                displayInfo.getTitle().getString(),
                displayInfo.getDescription().getString(),
                displayInfo.getIcon()
        );
    }

    public static AdvancementData readFromBuffer(PacketBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        String title = buffer.readUtf(32767);
        String description = buffer.readUtf(32767);
        ItemStack icon = buffer.readItem();
        return new AdvancementData(id, title, description, icon);
    }
}
