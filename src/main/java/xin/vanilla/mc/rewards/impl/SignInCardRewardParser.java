package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import xin.vanilla.mc.rewards.RewardParser;

public class SignInCardRewardParser implements RewardParser<Integer> {

    @Override
    public Integer deserialize(JsonObject json) {
        return json.get("signInCard").getAsInt();
    }

    @Override
    public JsonObject serialize(Integer reward) {
        JsonObject json = new JsonObject();
        json.addProperty("signInCard", reward);
        return json;
    }
}
