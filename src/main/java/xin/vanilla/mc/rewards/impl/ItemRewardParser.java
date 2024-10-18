package xin.vanilla.mc.rewards.impl;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
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
    public ItemStack deserialize(JSONObject json) {
        String itemId = json.getString("item");
        int count = json.getIntValue("count", 1);
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item == null) {
            throw new JSONException("Unknown item ID: " + itemId);
        }
        ItemStack itemStack = new ItemStack(item, count);

        // 如果存在NBT数据，则解析
        if (json.containsKey("nbt")) {
            try {
                CompoundNBT nbt = JsonToNBT.parseTag(json.getString("nbt"));
                itemStack.setTag(nbt);
            } catch (CommandSyntaxException e) {
                throw new JSONException("Failed to parse NBT data", e);
            }
        }
        return itemStack;
    }

    @Override
    public JSONObject serialize(ItemStack reward) {
        JSONObject json = new JSONObject();
        json.put("item", reward.getItem().getRegistryName().toString());
        json.put("count", reward.getCount());

        // 如果物品有NBT数据，则序列化
        if (reward.hasTag()) {
            if (reward.getTag() != null) {
                json.put("nbt", reward.getTag().toString());
            }
        }
        return json;
    }
}
