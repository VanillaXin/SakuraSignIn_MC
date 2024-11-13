package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.NonNull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
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

            // 如果存在NBT数据，则解析
            if (json.has("nbt")) {
                try {
                    CompoundTag nbt = TagParser.parseTag(json.get("nbt").getAsString());
                    itemStack.setTag(nbt);
                } catch (CommandSyntaxException e) {
                    throw new JsonParseException("Failed to parse NBT data", e);
                }
            }
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

            // 如果物品有NBT数据，则序列化
            if (reward.hasTag()) {
                if (reward.getTag() != null) {
                    json.addProperty("nbt", reward.getTag().toString());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to serialize item reward", e);
            json.addProperty("item", ForgeRegistries.ITEMS.getKey(Items.AIR).toString());
            json.addProperty("count", 0);
        }
        return json;
    }
}
