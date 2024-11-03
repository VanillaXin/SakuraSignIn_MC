package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xin.vanilla.mc.rewards.RewardParser;

public class ItemRewardParser implements RewardParser<ItemStack> {

    @Override
    public ItemStack deserialize(JsonObject json) {
        String itemId = json.get("item").getAsString();
        int count = json.get("count").getAsInt();
        count = Math.max(count, 1);
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item == null) {
            throw new JsonParseException("Unknown item ID: " + itemId);
        }
        ItemStack itemStack = new ItemStack(item, count);

        // 如果存在NBT数据，则解析
        if (json.has("nbt")) {
            try {
                CompoundNBT nbt = JsonToNBT.parseTag(json.get("nbt").getAsString());
                itemStack.setTag(nbt);
            } catch (CommandSyntaxException e) {
                throw new JsonParseException("Failed to parse NBT data", e);
            }
        }
        return itemStack;
    }

    @Override
    public JsonObject serialize(ItemStack reward) {
        JsonObject json = new JsonObject();
        json.addProperty("item", reward.getItem().getRegistryName().toString());
        json.addProperty("count", reward.getCount());

        // 如果物品有NBT数据，则序列化
        if (reward.hasTag()) {
            if (reward.getTag() != null) {
                json.addProperty("nbt", reward.getTag().toString());
            }
        }
        return json;
    }
}
