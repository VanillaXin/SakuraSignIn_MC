package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import lombok.NonNull;
import net.minecraft.util.ResourceLocation;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.rewards.RewardParser;

public class AdvancementRewardParser implements RewardParser<ResourceLocation> {

    @Override
    public @NonNull ResourceLocation deserialize(JsonObject json) {
        String advancementId;
        try {
            advancementId = json.get("advancement").getAsString();
        } catch (Exception e) {
            LOGGER.error("Failed to parse advancement reward", e);
            advancementId = SakuraSignIn.MODID + ":unknownAdvancement";
        }
        return new ResourceLocation(advancementId);
    }

    @Override
    public JsonObject serialize(ResourceLocation reward) {
        JsonObject json = new JsonObject();
        json.addProperty("advancement", reward.toString());
        return json;
    }

    @Override
    public String getDisplayName(JsonObject json) {
        // TODO 获取进度名称
        // ResourceLocation deserialize = deserialize(json);
        // return Minecraft.getInstance().getSingleplayerServer().getAdvancements().getAdvancement(deserialize).getDisplay().getTitle().getString();
        return "";
    }
}
