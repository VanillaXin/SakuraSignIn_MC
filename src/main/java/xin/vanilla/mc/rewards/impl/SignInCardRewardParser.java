package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import lombok.NonNull;
import net.minecraft.client.resources.I18n;
import xin.vanilla.mc.rewards.RewardParser;

public class SignInCardRewardParser implements RewardParser<Integer> {

    @Override
    public @NonNull Integer deserialize(JsonObject json) {
        try {
            return json.get("signInCard").getAsInt();
        } catch (Exception e) {
            LOGGER.error("Failed to parse signInCard reward", e);
            return 0;
        }
    }

    @Override
    public JsonObject serialize(Integer reward) {
        JsonObject json = new JsonObject();
        json.addProperty("signInCard", reward);
        return json;
    }

    @Override
    public String getName(JsonObject json) {
        return "补签卡";
    }
}
