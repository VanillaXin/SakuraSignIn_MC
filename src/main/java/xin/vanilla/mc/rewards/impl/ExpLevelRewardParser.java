package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import xin.vanilla.mc.rewards.RewardParser;

public class ExpLevelRewardParser implements RewardParser<Integer> {

    @Override
    public Integer deserialize(JsonObject json) {
        return json.get("expLevel").getAsInt();
    }

    @Override
    public JsonObject serialize(Integer reward) {
        JsonObject json = new JsonObject();
        json.addProperty("expLevel", reward);
        return json;
    }
}
