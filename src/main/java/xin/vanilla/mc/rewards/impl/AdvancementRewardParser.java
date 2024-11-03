package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import xin.vanilla.mc.rewards.RewardParser;

public class AdvancementRewardParser implements RewardParser<ResourceLocation> {

    @Override
    public ResourceLocation deserialize(JsonObject json) {
        String advancementId = json.get("advancement").getAsString();
        return new ResourceLocation(advancementId);
    }

    @Override
    public JsonObject serialize(ResourceLocation reward) {
        JsonObject json = new JsonObject();
        json.addProperty("advancement", reward.toString());
        return json;
    }
}
