package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.NonNull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import xin.vanilla.mc.rewards.RewardParser;

public class ItemRewardParser implements RewardParser<ItemStack> {

    @Override
    public @NonNull ItemStack deserialize(JsonObject json) {
        ItemStack itemStack;
        try {
            String itemId = json.get("item").getAsString();
            int count = json.get("count").getAsInt();
            count = Math.max(count, 1);
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
            if (item == null) {
                throw new JsonParseException("Unknown item ID: " + itemId);
            }
            itemStack = new ItemStack(item, count);

            // TODO 如果存在NBT数据，则解析
            // if (json.has("nbt")) {
            //     try {
            //         for (String s : json.get("nbt").getAsJsonObject().keySet()) {
            //             String asString = json.get(s).getAsString();
            //             itemStack.set(GSON.fromJson(s, DataComponentType.class), GSON.fromJson(asString, Object.class));
            //         }
            //     } catch (Exception e) {
            //         throw new JsonParseException("Failed to parse NBT data", e);
            //     }
            // }
        } catch (Exception e) {
            LOGGER.error("Failed to deserialize item reward", e);
            itemStack = new ItemStack(Items.AIR);
        }
        return itemStack;
    }

    @Override
    public JsonObject serialize(ItemStack reward) {
        JsonObject json = new JsonObject();
        try {
            json.addProperty("item", ForgeRegistries.ITEMS.getKey(reward.getItem()).toString());
            json.addProperty("count", reward.getCount());

            // TODO 如果物品有NBT数据，则序列化
            // if (!reward.getComponents().isEmpty()) {
            //     JsonObject nbt = new JsonObject();
            //     DataComponentMap components = reward.getComponents();
            //     for (DataComponentType<?> dataComponentType : components.keySet()) {
            //         nbt.addProperty(GSON.toJson(dataComponentType), GSON.toJson(components.get(dataComponentType)));
            //     }
            //     json.add("nbt", nbt);
            // }
        } catch (Exception e) {
            LOGGER.error("Failed to serialize item reward", e);
            json.addProperty("item", ForgeRegistries.ITEMS.getKey(Items.AIR).toString());
            json.addProperty("count", 0);
        }
        return json;
    }
}
