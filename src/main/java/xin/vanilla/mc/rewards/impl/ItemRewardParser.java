package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.NonNull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
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
                    CompoundNBT nbt = JsonToNBT.parseTag(json.get("nbt").getAsString());
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
            json.addProperty("item", getId(reward.getItem()));
            json.addProperty("count", reward.getCount());

            // 如果物品有NBT数据，则序列化
            if (reward.hasTag()) {
                if (reward.getTag() != null) {
                    json.addProperty("nbt", getNbtString(reward));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to serialize item reward", e);
            json.addProperty("item", getId(Items.AIR));
            json.addProperty("count", 0);
        }
        return json;
    }

    @Override
    public String getName(JsonObject json) {
        return this.deserialize(json).getDisplayName().getString().replaceAll("\\[(.*)]", "$1");
    }

    public static String getName(ItemStack itemStack) {
        return itemStack.getDisplayName().getString().replaceAll("\\[(.*)]", "$1");
    }

    public static String getName(Item item) {
        return new ItemStack(item).getDisplayName().getString().replaceAll("\\[(.*)]", "$1");
    }

    public static String getNbtString(ItemStack itemStack) {
        JsonObject jsonObject = new JsonObject();
        try {
            if (itemStack.hasTag()) {
                if (itemStack.getTag() != null) {
                    for (String key : itemStack.getTag().getAllKeys()) {
                        INBT inbt = itemStack.getTag().get(key);
                        if (inbt != null) {
                            jsonObject.addProperty(key, inbt.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get nbt string", e);
        }
        return jsonObject.toString();
    }

    public static String getId(Item item) {
        ResourceLocation resource = item.getRegistryName();
        if (resource == null) return "minecraft:air";
        else return resource.toString();
    }

    public static String getId(ItemStack itemStack) {
        return getId(itemStack.getItem()) + getNbtString(itemStack);
    }

    public static Item getItem(String id) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(id.substring(0, id.indexOf("{"))));
    }

    public static ItemStack getItemStack(String id) {
        Item item = getItem(id);
        if (item == null) {
            throw new JsonParseException("Unknown item ID: " + id);
        }
        ItemStack itemStack = new ItemStack(item);
        if (id.contains("{")) {
            try {
                CompoundNBT nbt = JsonToNBT.parseTag(id.substring(id.indexOf("{")));
                itemStack.setTag(nbt);
            } catch (Exception e) {
                LOGGER.error("Failed to parse NBT data", e);
            }
        }
        return itemStack;
    }
}
