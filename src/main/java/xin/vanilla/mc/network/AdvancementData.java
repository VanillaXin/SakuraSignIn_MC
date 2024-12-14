package xin.vanilla.mc.network;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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

    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(id);
        buffer.writeUtf(title);
        buffer.writeUtf(description);
        ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buffer, icon);
    }

    public static AdvancementData fromAdvancement(AdvancementHolder advancement) {
        DisplayInfo displayInfo = advancement.value().display().orElse(null);
        if (displayInfo == null) {
            return new AdvancementData(
                    advancement.id(),
                    advancement.id().toString(),
                    "",
                    new ItemStack(Items.AIR)
            );
        }
        return new AdvancementData(
                advancement.id(),
                displayInfo.getTitle().getString(),
                displayInfo.getDescription().getString(),
                displayInfo.getIcon()
        );
    }

    public static AdvancementData readFromBuffer(FriendlyByteBuf buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        String title = buffer.readUtf(32767);
        String description = buffer.readUtf(32767);
        ItemStack icon = ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buffer);
        return new AdvancementData(id, title, description, icon);
    }
}
