package xin.vanilla.mc.rewards.impl;

import com.alibaba.fastjson2.JSONObject;
import net.minecraft.util.ResourceLocation;
import xin.vanilla.mc.rewards.RewardParser;

public class AdvancementRewardParser implements RewardParser<ResourceLocation> {

    @Override
    public ResourceLocation deserialize(JSONObject json) {
        String advancementId = json.getString("advancement");
        return new ResourceLocation(advancementId);
    }

    @Override
    public JSONObject serialize(ResourceLocation reward) {
        JSONObject json = new JSONObject();
        json.put("advancement", reward.toString());
        return json;
    }

    // server.getAdvancements().getAdvancement((ResourceLocation) content);
}
